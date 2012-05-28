package com.saplo.api.client.session.impl;

import java.net.URI;
import java.util.HashMap;
import org.apache.http.HttpStatus;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.saplo.api.client.ClientError;
import com.saplo.api.client.ClientProxy;
import com.saplo.api.client.ResponseCodes;
import com.saplo.api.client.SaploClientException;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.session.Session;
import com.saplo.api.client.session.TransportRegistry;
import com.saplo.api.client.session.TransportRegistry.SessionFactory;

/**
 * @author progre55
 * 
 */
public class HTTPSessionWink implements Session {

	protected URI uri;
	protected volatile String params;
	protected ClientProxy clientProxy = null;

	public HTTPSessionWink(URI uri, String params) {
		this.uri = uri;
		this.params = params;
	}

	public HTTPSessionWink(URI uri, String params, ClientProxy clientProxy) {
		this(uri, params);
		this.setProxy(clientProxy);
	}

	public JSONRPCResponseObject sendAndReceive(JSONRPCRequestObject message) throws SaploClientException {
		RestClient client = new RestClient();

		if (clientProxy != null) {
			ClientConfig config = new ClientConfig();
			config.proxyHost(clientProxy.getHost());
			config.proxyPort(clientProxy.getPort());
			// Proxy authentication is not necessary.- JVM System default

			if (clientProxy.isSecure()) {
				// TODO
				// Create a proxyAuthentication Handler and attach it to the client
			}

			client = new RestClient(config);
		}

		Resource resource = client.resource(uri + "?" + params);
		ClientResponse response = resource.post(message.toString());

		String responseString = response.getEntity(String.class);
		int statusCode = response.getStatusCode();

		// probably the API is down..
		if (statusCode != HttpStatus.SC_OK)
			throw new SaploClientException(ResponseCodes.MSG_API_DOWN_EXCEPTION, ResponseCodes.CODE_API_DOWN_EXCEPTION,
					statusCode);

		JSONTokener tokener = new JSONTokener(responseString);
		Object rawResponseMessage;
		try {
			rawResponseMessage = tokener.nextValue();
		} catch (JSONException e) {
			throw new SaploClientException(ResponseCodes.MSG_MALFORMED_RESPONSE, ResponseCodes.CODE_MALFORMED_RESPONSE);
		}
		JSONObject responseMessage = (JSONObject) rawResponseMessage;

		if (responseMessage == null)
			throw new ClientError("Invalid response type - " + rawResponseMessage);

		return new JSONRPCResponseObject(responseMessage);
	}

	public void setParams(String params) {
		this.params = params;
	}

	/**
	 * Set a proxy of type ClientProxy to use for this transport connections
	 * 
	 * @param proxy
	 */
	public void setProxy(ClientProxy clientProxy) {
		this.clientProxy = clientProxy;
	}

	/**
	 * Close all the clients and clear the pool.
	 */
	public synchronized void close() {
	}

	static class SessionFactoryImpl implements SessionFactory {
		volatile HashMap<URI, Session> sessionMap = new HashMap<URI, Session>();

		public Session newSession(URI uri, String params, ClientProxy proxy) {
			Session session = sessionMap.get(uri);
			if (session == null) {
				synchronized (sessionMap) {
					session = sessionMap.get(uri);
					if (session == null) {
						if (proxy != null)
							session = new HTTPSessionApache(uri, params, proxy);
						else
							session = new HTTPSessionApache(uri, params);
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
