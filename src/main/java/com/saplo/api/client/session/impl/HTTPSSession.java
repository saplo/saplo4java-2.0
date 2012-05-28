/**
 * 
 */
package com.saplo.api.client.session.impl;

import java.net.URI;
import java.util.HashMap;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;

import com.saplo.api.client.ClientProxy;
import com.saplo.api.client.session.Session;
import com.saplo.api.client.session.TransportRegistry;
import com.saplo.api.client.session.TransportRegistry.SessionFactory;

/**
 * @author progre55
 *
 */
public class HTTPSSession extends HTTPSessionApache {

	public HTTPSSession(URI uri, String params) {
		super(uri, params);
	}

	public HTTPSSession(URI uri, String params, ClientProxy proxy) {
		super(uri, params, proxy);
	}

	@Override
	protected SchemeRegistry registerScheme() {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(
				new Scheme("https", (endpoint.getPort() > 0 ? endpoint.getPort() : 443), 
						SSLSocketFactory.getSocketFactory()));
		
		return schemeRegistry;
	}
	
	
	static class SessionFactoryImpl implements SessionFactory {
		volatile HashMap<URI, Session> sessionMap = new HashMap<URI, Session>();
		
		public Session newSession(URI uri, String params, ClientProxy proxy) {
			Session session = sessionMap.get(uri);
			if (session == null) {
				synchronized (sessionMap) {
					session = sessionMap.get(uri);
					if(session == null) {
						if(null != proxy)
							session = new HTTPSSession(uri, params, proxy);
						else
							session = new HTTPSSession(uri, params);
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
