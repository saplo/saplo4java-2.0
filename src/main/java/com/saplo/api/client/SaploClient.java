package com.saplo.api.client;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saplo.api.client.entity.JSONRPCErrorObject;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.entity.SaploFuture;
import com.saplo.api.client.manager.SaploAccountManager;
import com.saplo.api.client.manager.SaploAuthManager;
import com.saplo.api.client.manager.SaploCollectionManager;
import com.saplo.api.client.manager.SaploGroupManager;
import com.saplo.api.client.manager.SaploTextManager;
import com.saplo.api.client.session.Session;
import com.saplo.api.client.session.TransportRegistry;
import com.saplo.api.client.session.impl.HTTPSSession;
import com.saplo.api.client.session.impl.HTTPSessionApache;
import com.saplo.api.client.util.ClientUtil;

/**
 * A Saplo-API client class.
 * 
 * @author progre55
 */
public class SaploClient implements Serializable {

	private static final long serialVersionUID = -3012934547356595675L;

	private static final Logger logger = LoggerFactory.getLogger(SaploClient.class);

	private static Session session;

	private final boolean ssl;
	private final String endpoint;
	private final String apiKey;
	private final String secretKey;
	private String accessToken;

	// an ES for handling "async" methods
	private ExecutorService es;

	private static final String DEFAULT_ENDPOINT = "http://api.saplo.com/rpc/json";
	private static final String DEFAULT_SSL_ENDPOINT = "https://api.saplo.com/rpc/json";


	/**
	 * A constructor that uses a default endpoint - {@value SaploClient#DEFAULT_ENDPOINT}
	 * and not SSL secure
	 *
	 * @param apiKey - your API-KEY
	 * @param secretKey - your SECRET-KEY
	 *
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploClient(String apiKey, String secretKey) throws SaploClientException {
		this(apiKey, secretKey, DEFAULT_ENDPOINT);
	}

	public SaploClient(String apiKey, String secretKey, ClientProxy proxy) throws SaploClientException {
		this(apiKey, secretKey, "", DEFAULT_ENDPOINT, false, proxy);
	}

	/**
	 * A constructor to use the default endpoint
	 *
	 * @param apiKey - your API-KEY
	 * @param secretKey - your SECRET-KEY
	 * @param ssl - should use SSL?
	 *
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploClient(String apiKey, String secretKey, boolean ssl) throws SaploClientException {
		this(apiKey, secretKey, "", ssl ? DEFAULT_SSL_ENDPOINT : DEFAULT_ENDPOINT, ssl, null);
	}

	/**
	 * A Constructor with a custom endpoint and non SSL secure <br>
	 * NOTE: It is recommended to use {@link #SaploClient(String, String)}
	 *
	 * @param apiKey - your API-KEY
	 * @param secretKey - your SECRET-KEY
	 * @param endpoint - the endpoint URL for the client to connect to
	 *
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploClient(String apiKey, String secretKey, String endpoint) throws SaploClientException {
		this(apiKey, secretKey, "", endpoint, false, null);
	}

	/**
	 * Another constructor
	 *
	 * @param apiKey - your API-KEY
	 * @param secretKey - your SECRET-KEY
	 * @param endpoint - the endpoint URL for the client to connect to
	 * @param ssl - should use SSL?
	 *
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploClient(String apiKey, String secretKey, String endpoint, boolean ssl) throws SaploClientException {
		this(apiKey, secretKey, "", endpoint, ssl);
	}

	/**
	 * If you have a valid accessToken, use this constructor
	 *
	 * @param apiKey - your API-KEY
	 * @param secretKey - your SECRET-KEY
	 * @param accessToken - a valid accessToken
	 * @param endpoint - the endpoint URL for the client to connect to
	 * @param ssl - should use SSL?
	 *
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploClient(String apiKey, String secretKey, String accessToken,
			String endpoint, boolean ssl) throws SaploClientException {
		this(apiKey, secretKey, "", endpoint, ssl, null);
	}

	/**
	 * If you have a valid accessToken, use this constructor
	 *
	 * @param apiKey - your API-KEY
	 * @param secretKey - your SECRET-KEY
	 * @param accessToken - a valid accessToken
	 * @param endpoint - the endpoint URL for the client to connect to
	 * @param ssl - should use SSL?
	 * @param proxy - should this transport use proxy
	 *
	 * @throws HttpException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploClient(String apiKey, String secretKey, String accessToken, String endpoint, 
			boolean ssl, ClientProxy proxy) throws SaploClientException {

		if(endpoint == null || !endpoint.startsWith("http"))
			throw new SaploClientException("Invalid endpoint!");
		if(ssl && !endpoint.startsWith("https://"))
			throw new SaploClientException("Invalid SSL endpoint! An SSL URL should start with https://");
		if(!ssl && !endpoint.startsWith("http://"))
			throw new SaploClientException("Invalid endpoint! Should start with http://");

		this.apiKey = apiKey;
		this.secretKey = secretKey;
		this.ssl = ssl;
		this.endpoint = endpoint;

		es = Executors.newFixedThreadPool(20);

		this.setupServerEnvironment();
		createSession(accessToken, proxy);
		lock = new ReentrantLock();
		sleeping = lock.newCondition();

		// create the managers
		collectionMgr = new SaploCollectionManager(this);
		textMgr = new SaploTextManager(this);
		groupMgr = new SaploGroupManager(this);
		authMgr = new SaploAuthManager(this);
		accountMgr = new SaploAccountManager(this);
	}

	private final SaploCollectionManager collectionMgr;
	public SaploCollectionManager getCollectionManager() {
		return collectionMgr;
	}

	private final SaploTextManager textMgr;
	public SaploTextManager getTextManager() {
		return textMgr;
	}

	private final SaploGroupManager groupMgr;
	public SaploGroupManager getGroupManager() {
		return groupMgr;
	}

	private final SaploAuthManager authMgr;
	public SaploAuthManager getAuthManager() {
		return authMgr;
	}

	private final SaploAccountManager accountMgr;
	public SaploAccountManager getAccountManager() {
		return accountMgr;
	}

	private long lastReconnectAttempt = 0;
	private long lastSuccessfulReconnect = 0;
	private final long reconnectTimeout = 3 * 1000; // 3 seconds
	private long reconnectCount = 0;
	private long maxReconnectCount = 10;

	private final Lock lock;
	private final Condition sleeping;

	/**
	 * Set a proxy for the client to communicate with the API
	 * NOTE: set it before getting authed
	 * 
	 * @param proxy - a {@link ClientProxy} instance
	 */
	public void setProxy(ClientProxy proxy) {
		session.setProxy(proxy);
	}

	/*
	 * Get authenticated and store the accessToken in the session
	 */
	private synchronized void createSession(String accToken, ClientProxy proxy) throws SaploClientException {
		session = TransportRegistry.getTransportRegistryInstance()
				.createSession(endpoint, "access_token=" + accToken, proxy);

		if(accToken != null && accToken.length() > 0) {
			this.accessToken = accToken;
			return;
		}
		authenticateSession();
	}

	/*
	 * FIXME fix, it's too complicated
	 */
	private synchronized boolean reAuthenticateSession() throws SaploClientException {

		lock.lock();
		try  {
			long now = System.currentTimeMillis();
			if((now - lastReconnectAttempt) < reconnectTimeout * maxReconnectCount 
					|| reconnectCount > maxReconnectCount)
				return false;

			// if have successfully reconnected during the last 10 seconds, return true
			if((now - lastSuccessfulReconnect) < 10000 && reconnectCount == 0)
				return true;

			logger.info("Trying to reconnect to the API..");
			while(reconnectCount <= maxReconnectCount) {

				try {
					// wait a bit before attempting to reconnect
					long toSleep = (reconnectCount + 1) * reconnectTimeout;

					sleeping.await(toSleep, TimeUnit.MILLISECONDS);

					authenticateSession();

					// if haven't got an exception so far, then assume connected
					reconnectCount = 0;
					lastReconnectAttempt = 0;
					lastSuccessfulReconnect = System.currentTimeMillis();
					logger.info("Successfully reconnected to the API..");

					return true;
				} catch (SaploClientException e) {
					reconnectCount++;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}
			lastReconnectAttempt = System.currentTimeMillis();
			logger.warn("Could not reconnect to the API after {} attempts.", reconnectCount);
			throw new SaploClientException(ResponseCodes.MSG_ERR_NOSESSION, ResponseCodes.CODE_ERR_NOSESSION);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * Get authenticated and save the access_token
	 */
	private void authenticateSession() throws SaploClientException {
		SaploAuthManager auth = new SaploAuthManager(this);
		accessToken = auth.accessToken(apiKey, secretKey);

		session.setParams("access_token=" + accessToken);
	}

	/**
	 * 
	 * @return lastSuccessfulReconnect in milliseconds
	 */
	public long getLastSuccessfulReconnect() {
		return lastSuccessfulReconnect;
	}

	/**
	 * Get the next incremental JSON-RPC id
	 * 
	 * @return nextId
	 */
	public int getNextId() {
		return ClientUtil.getNextId();
	}

	/**
	 * Get the accessToken for this session, if already logged in
	 *  
	 * @return accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Check to see if the endpoint is actually a Saplo-API endpoint
	 * 
	 * @return up / notUp
	 * @throws SaploClientException 
	 */
	public boolean isUp() throws SaploClientException {

		JSONArray params = new JSONArray();
		params.put("ping");

		JSONRPCRequestObject req = new JSONRPCRequestObject(1, "ping.ping", params);

		JSONRPCResponseObject json = sendAndReceive(req);
		String result = (String) parseResponse(json);

		return "pong".equals(result);

	}

	/**
	 * Shut down the session
	 * 
	 * @return success / fail
	 * @throws SaploClientException 
	 */
	public boolean shutdown() throws SaploClientException {

		JSONArray params = new JSONArray();

		sendAndReceive(new JSONRPCRequestObject(getNextId(), "auth.invalidateToken", params));

		if (session != null)
			session.close();

		if(ssl)
			HTTPSSession.deregister(TransportRegistry.getTransportRegistryInstance());
		else
			HTTPSessionApache.deregister(TransportRegistry.getTransportRegistryInstance());

		es.shutdownNow();

		return true;
	}

	/*
	 * Register a Session instance with the TransportRegistry
	 */
	private void setupServerEnvironment() {
		if(ssl)
			HTTPSSession.register(TransportRegistry.getTransportRegistryInstance());
		else
			HTTPSessionApache.register(TransportRegistry.getTransportRegistryInstance());

	}

	/**
	 * Send message to server and receive response.
	 * 
	 * @param message - a JSONRPCRequestObject to send to the server (API)
	 * @return JSONRPCResponseObject with response params.
	 * 
	 * @throws SaploClientException 
	 */
	public JSONRPCResponseObject sendAndReceive(JSONRPCRequestObject request) throws SaploClientException {
		logger.debug(">>>>>>Sending request: " + request);
		JSONRPCResponseObject response = (JSONRPCResponseObject)session.sendAndReceive(request);
		logger.debug("<<<<<<Got response: " + response);
		return response;
	}

	/**
	 * An Async version of {@link #sendAndReceive(JSONRPCRequestObject)}
	 * 
	 * @param request
	 * @return
	 */
	public SaploFuture<JSONRPCResponseObject> sendAndReceiveAsync(final JSONRPCRequestObject request) {
		return new SaploFuture<JSONRPCResponseObject>(es.submit(new Callable<JSONRPCResponseObject>() {
			public JSONRPCResponseObject call() throws SaploClientException {
				return sendAndReceive(request);
			}
		}));
	}

	/**
	 * Get the executor service to execute async tasks by managers
	 * 
	 * @return es
	 */
	public ExecutorService getAsyncExecutor() {
		return es;
	}

	/**
	 * Parse the response for result or error.
	 * 
	 * @param responseMessage - a JSONRPCResponseObject received from the server (API)
	 * @return object - a raw object encapsulated in the "result" JSON parameter of the response
	 * 
	 * @throws SaploClientException
	 */
	public Object parseResponse(JSONRPCResponseObject responseMessage)
			throws SaploClientException {

		if(!responseMessage.isSuccess())
			processException(responseMessage.getError());

		Object rawResult = responseMessage.getResult();

		if (rawResult == null) {
			processException(responseMessage.getError());
		}

		return rawResult;
	}

	/*
	 * process an error, or rather throw one
	 */
	private void processException(JSONRPCErrorObject error)
			throws SaploClientException {
		if(error.getClientException().getErrorCode() == ResponseCodes.CODE_ERR_NOSESSION
				|| error.getClientException().getErrorCode() == ResponseCodes.CODE_API_DOWN_EXCEPTION) {
			boolean reconnected = reAuthenticateSession();
			if(reconnected)
				throw new SaploClientException(ResponseCodes.MSG_RECONNECTED, ResponseCodes.CODE_RECONNECTED);
		}
		throw error.getClientException();
	}
}
