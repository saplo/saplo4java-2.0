/**
 * 
 */
package com.saplo.api.client;

/**
 * A class to hold "proxy" data to connect to the API
 * 
 * @author progre55
 */
public class ClientProxy {
	private String host;
	private int port;
	private String username;
	private String password;
	private boolean secure;
	
	/**
	 * @param host
	 * @param port
	 */
	public ClientProxy(String host, int port) {
		this.host = host;
		this.port = port;
		this.secure = false;
	}
	
	/**
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public ClientProxy(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.secure = true;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * When/if a username is set, proxy is assumed to be "secure"
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
		this.secure = true;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the secure
	 */
	public boolean isSecure() {
		return secure;
	}
}
