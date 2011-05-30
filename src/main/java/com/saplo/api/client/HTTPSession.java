package com.saplo.api.client;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.util.HashMap;
import java.util.Stack;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.saplo.api.client.TransportRegistry.SessionFactory;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;

/**
 * An implementation of {@link Session} over HTTP.
 * Creates a pools of HttpClients for faster results, 
 * but uses the same state, uri and params for all the clients.
 * 
 * @author progre55
 *
 */
public class HTTPSession implements Session {
	private static Log log = LogFactory.getLog(HTTPSession.class);

	protected HttpState state;
	protected URI uri;
	protected volatile String params;
	protected int clientsCount;
	protected Stack<HttpClient> clientPool;
	
	public HTTPSession(URI uri, String params, int count) {
		this.uri = uri;
		this.params = params;
		this.clientsCount = count;
		clientPool = new Stack<HttpClient>();
		fillPool();
	}

	/**
	 * An option to set state from the outside. for example, to provide existing
	 * session parameters.
	 */
	public void setState(HttpState state) {
		this.state = state;
	}

	public JSONRPCResponseObject sendAndReceive(JSONRPCRequestObject message) throws JSONException, SaploClientException {
//		if (log.isDebugEnabled())
//			log.debug("Sending: " + message.toString(2));

		PostMethod postMethod = new PostMethod(uri.toString() + "?" + params);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.setRequestHeader("Proxy-Connection", "Keep-Alive");

		HttpClient client = http();
		try {
			RequestEntity requestEntity = new StringRequestEntity(message.toString(),
					"application/x-www-form-urlencoded", "utf-8");
			postMethod.setRequestEntity(requestEntity);
			client.executeMethod(null, postMethod);
			int statusCode = postMethod.getStatusCode();
			if (statusCode != HttpStatus.SC_OK)
				// probably the API is down..
				throw new SaploClientException(ResponseCodes.MSG_API_DOWN_EXCEPTION, ResponseCodes.CODE_API_DOWN_EXCEPTION, statusCode+"", HttpStatus.getStatusText(statusCode));
//				throw new ClientError("HTTP Status - " + HttpStatus.getStatusText(statusCode) + " (" + statusCode + ")");
			JSONTokener tokener = new JSONTokener(
					postMethod.getResponseBodyAsString());
			Object rawResponseMessage = tokener.nextValue();
			JSONObject responseMessage = (JSONObject) rawResponseMessage;
			if (responseMessage == null)
				throw new ClientError("Invalid response type - "
						+ rawResponseMessage);
			return new JSONRPCResponseObject(responseMessage);
		} catch (HttpException e) {
			throw new ClientError(e);
		} catch (SocketException e) {
			throw new SaploClientException(ResponseCodes.MSG_SOCKET_EXCEPTION, ResponseCodes.CODE_SOCKET_EXCEPTION, e);
		} catch (IOException e) {
			throw new ClientError(e);
		} finally {
			releaseClient(client);
		}
	}

	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * Get a client from the pool.
	 * 
	 * @return - HttpClient
	 */
	protected synchronized HttpClient http() {
		HttpClient cl = null;
		try {
			while (clientPool.empty())
				this.wait();
			log.debug("Remaining clients: " + clientPool.size());
			cl = clientPool.pop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return cl;
	}

	/**
	 * Put the client back into the pool.
	 * 
	 * @param client - the client to return into the pool
	 */
	public synchronized void releaseClient(HttpClient client) {
		clientPool.push(client);
		this.notify();
	}

	private void fillPool() {
		if (state == null)
			state = new HttpState();
		for (int i = 0; i < clientsCount; i++) {
			HttpClient cl = new HttpClient();
			cl.setState(state);
			clientPool.push(cl);
		}
	}

	/**
	 * Close all the clients and clear the pool.
	 */
	public synchronized void close() {
		if (state != null) {
			state.clear();
			state = null;
		}
		clientPool.clear();
	}

	static class SessionFactoryImpl implements SessionFactory {
		volatile HashMap<URI, Session> sessionMap = new HashMap<URI, Session>();

		public Session newSession(URI uri, String params, int count) {
			Session session = sessionMap.get(uri);
			if (session == null) {
				synchronized (sessionMap) {
					session = sessionMap.get(uri);
					if(session == null) {
						session = new HTTPSession(uri, params, count);
						sessionMap.put(uri, session);
					}
				}
			}
			return session;
		}
	}

	/**
	 * Register this transport in 'registry'
	 */
	public static void register(TransportRegistry registry) {
		registry.registerTransport("http", new SessionFactoryImpl());
	}

	/**
	 * De-register this transport from the 'registry'
	 */
	public static void deregister(TransportRegistry registry) {
		registry.deregisterTransport("http");
	}

}
