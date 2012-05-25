package com.saplo.api.client;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.saplo.api.client.entity.JSONRPCErrorObject;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.manager.SaploAuthManager;
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

	protected static final Logger logger = LoggerFactory.getLogger(SaploClient.class);

	protected static Session session;

	protected boolean ssl;
	protected String endpoint;
	protected String apiKey;
	protected String secretKey;
	protected String accessToken;

	protected static final String DEFAULT_ENDPOINT = "http://api.saplo.com/rpc/json";
	protected static final String DEFAULT_SSL_ENDPOINT = "https://api.saplo.com/rpc/json";

	/**
	 * A Builder class for SaploClient.
	 * {@link #apiKey} and {@link #secretKey} are required, 
	 * and the rest of the params are optional and will be set to default if not specified
	 *  
	 * @author progre55
	 */
	public static class Builder {
		// required params
		private final String apiKey;
		private final String secretKey;
		
		// optional params
		private boolean ssl = false;
		private String endpoint = DEFAULT_ENDPOINT;
		private String accessToken = "";
		private ClientProxy proxy = null;
		
		public Builder(String apiKey, String secretKey) {
			this.apiKey = apiKey;
			this.secretKey = secretKey;
		}
		
		public Builder endpoint(String endpoint)
		{ this.endpoint = endpoint;	return this; }
		public Builder ssl(boolean ssl)
		{ this.ssl = ssl;	return this; }
		public Builder accessToken(String accessToken)
		{ this.accessToken = accessToken;	return this; }
		public Builder proxy(ClientProxy proxy)
		{ this.proxy = proxy;	return this; }
		
		/**
		 * Build a SaploClient with the specified params
		 * 
		 * @return SaploClient
		 * @throws SaploClientException
		 */
		public SaploClient build() throws SaploClientException {
			if(this.endpoint == null || !this.endpoint.startsWith("http"))
				throw new SaploClientException(String.format("Invalid endpoint \"%s\"!", this.endpoint));
			if(this.ssl && !this.endpoint.startsWith("https://"))
				throw new SaploClientException("Invalid SSL endpoint! An SSL URL should start with https://");
			if(!this.ssl && !this.endpoint.startsWith("http://"))
				throw new SaploClientException("Invalid endpoint! Should start with http://");
			
			return new SaploClient(this);
		}
	}
	
	private SaploClient(Builder builder) throws SaploClientException {
		apiKey = builder.apiKey;
		secretKey = builder.secretKey;
		
		this.ssl = builder.ssl;
		this.apiKey = builder.apiKey;
		this.secretKey = builder.secretKey;
		this.endpoint = builder.endpoint;
		this.setupServerEnvironment();
		createSession(builder.accessToken, builder.proxy);
		lock = new ReentrantLock();
		sleeping = lock.newCondition();
	}
	
	protected long lastReconnectAttempt = 0;
	protected long lastSuccessfulReconnect = 0;
	protected final long reconnectTimeout = 3 * 1000; // 3 seconds
	protected long reconnectCount = 0;
	protected long maxReconnectCount = 10;

	protected final Lock lock;
	protected final Condition sleeping;

	/**
	 * Set a proxy address for the client to commumicate with the API
	 * TODO set it before authing
	 * @param host
	 * @param port
	 */
	public void setProxy(ClientProxy proxy) {
		session.setProxy(proxy);
	}

	/*
	 * Get authenticated and store the accessToken in the session
	 */
	protected synchronized void createSession(String accToken, ClientProxy proxy) throws SaploClientException {
		session = TransportRegistry.getTransportRegistryInstance()
				.createSession(endpoint, "access_token=" + accToken, proxy);

		if(accToken != null && accToken.length() > 0) {
			this.accessToken = accToken;
			return;
		}
		authenticateSession();
	}

	protected synchronized boolean reCreateSession() throws SaploClientException {

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

	protected void authenticateSession() throws SaploClientException {
		SaploAuthManager auth = new SaploAuthManager(this);
		try {
			accessToken = auth.accessToken(apiKey, secretKey);

		} catch (JSONException ex) {
			logger.error("Exception occured ", ex);
			throw new ClientError(ex);
		}

		session.setParams("access_token=" + accessToken);
	}

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

		try {
			JSONArray params = new JSONArray();
			params.put("ping");

			JSONRPCRequestObject req = new JSONRPCRequestObject(1, "ping.ping", params);

			JSONRPCResponseObject json = sendAndReceive(req);
			String result = (String) parseResponse(json);

			return "pong".equals(result);

		} catch (JSONException e) {
			return false;
		}
	}

	/**
	 * Shut down the session
	 * 
	 * @return success / fail
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public boolean shutdown() throws JSONException, SaploClientException {

		JSONArray params = new JSONArray();

		sendAndReceive(new JSONRPCRequestObject(getNextId(), "auth.invalidateToken", params));

		if (session != null)
			session.close();

		if(ssl)
			HTTPSSession.deregister(TransportRegistry.getTransportRegistryInstance());
		else
			HTTPSessionApache.deregister(TransportRegistry.getTransportRegistryInstance());

		return true;
	}

	/*
	 * Register a Session instance with the TransportRegistry
	 */
	protected void setupServerEnvironment() {
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
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public JSONRPCResponseObject sendAndReceive(JSONRPCRequestObject request) throws JSONException, SaploClientException {
		logger.debug(">>>>>>Sending request: " + request);
		JSONRPCResponseObject response = (JSONRPCResponseObject)session.sendAndReceive(request);
		logger.debug("<<<<<<Got response: " + response);
		return response;
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
	protected void processException(JSONRPCErrorObject error)
			throws SaploClientException {
		if(error.getClientException().getErrorCode() == ResponseCodes.CODE_ERR_NOSESSION
				|| error.getClientException().getErrorCode() == ResponseCodes.CODE_API_DOWN_EXCEPTION) {
			boolean reconnected = reCreateSession();
			if(reconnected)
				throw new SaploClientException(ResponseCodes.MSG_RECONNECTED, ResponseCodes.CODE_RECONNECTED);
		}
		throw error.getClientException();
	}
}
