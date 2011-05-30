/**
 * 
 */
package com.saplo.api.client;

import java.net.URI;
import java.util.HashMap;

import com.saplo.api.client.TransportRegistry.SessionFactory;

/**
 * @author progre55
 *
 */
public class HTTPSSession extends HTTPSession {

	public HTTPSSession(URI uri, String params, int count) {
		super(uri, params, count);
	}
	
	static class SessionFactoryImpl implements SessionFactory {
		volatile HashMap<URI, Session> sessionMap = new HashMap<URI, Session>();

		public Session newSession(URI uri, String params, int count) {
			Session session = sessionMap.get(uri);
			if (session == null) {
				synchronized (sessionMap) {
					session = sessionMap.get(uri);
					if(session == null) {
						session = new HTTPSSession(uri, params, count);
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
		registry.registerTransport("https", new SessionFactoryImpl());
	}

	/**
	 * De-register this transport from the 'registry'
	 */
	public static void deregister(TransportRegistry registry) {
		registry.deregisterTransport("https");
	}


}
