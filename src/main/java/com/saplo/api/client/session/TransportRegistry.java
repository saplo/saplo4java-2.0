package com.saplo.api.client.session;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import com.saplo.api.client.ClientError;
import com.saplo.api.client.ClientProxy;
import com.saplo.api.client.session.impl.HTTPSessionApache;

/**
 * A registry of transports serving JSON-RPC-Client
 */
public class TransportRegistry {

	/**
	 * A static class holder for TransportRegistry to hold only
	 * a single instance per class-loader. Faster than having
	 * a synchronized method.
	 * 
	 * @author progre55
	 *
	 */
	private static class TransportRegistryHolder {
		private static final TransportRegistry singleton = new TransportRegistry();
	}

	/**
	 * @param registerDefault
	 *            'true' if to register default transports
	 */
	public TransportRegistry(boolean registerDefault) {
		if (registerDefault) {
			HTTPSessionApache.register(this);
		}
	}

	public TransportRegistry() {
		this(true);
	}

	/**
	 * @return singleton instance of the class, created if necessary.
	 */
	public static TransportRegistry getTransportRegistryInstance() {
		return TransportRegistryHolder.singleton;
	}

	private HashMap<String, SessionFactory> registry = new HashMap<String, SessionFactory>();

	public void registerTransport(String scheme, SessionFactory factory) {
		registry.put(scheme, factory);
	}

	public void deregisterTransport(String scheme) {
		registry.remove(scheme);
	}

	public Session createSession(String uriString, String params, ClientProxy proxy) {
		try {
			URI uri = new URI(uriString);
			SessionFactory found = registry.get(uri.getScheme());
			if (found == null)
				throw new ClientError("Could not open URI '" + uriString
						+ "'. Unknown scheme - '" + uri.getScheme() + "'." +
				"Make sure you have registered your SessionFactory with this transport.");
			return found.newSession(uri, params, proxy);
		} catch (URISyntaxException e) {
			throw new ClientError(e);
		}
	}

	public interface SessionFactory {
		/**
		 * @param uri - URI used to open this session
		 * @param params - jsessionid or access_token param
		 */
		Session newSession(URI uri, String params, ClientProxy proxy);
	}

}
