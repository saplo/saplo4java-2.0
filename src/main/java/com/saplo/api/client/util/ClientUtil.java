/**
 * 
 */
package com.saplo.api.client.util;

/**
 * A utility class. Well, as the name says.
 * 
 * @author progre55
 */
public class ClientUtil {

	public static final String NULL_STRING = "DEFAULT_STRING";
	public static final int NULL_INT = -1;
	
	private static int idCounter = 0;
	
	public static int getCurrentId() {
		return idCounter;
	}
	
	public static synchronized int getNextId() {
		return ++idCounter;
	}
}
