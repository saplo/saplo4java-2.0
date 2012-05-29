/**
 * 
 */
package com.saplo.api.client.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A utility class. Well, as the name says.
 * 
 * @author progre55
 */
public class ClientUtil {

	private ClientUtil() { }
	
	public static final String NULL_STRING = "DEFAULT_STRING";
	public static final int NULL_INT = -1;
	
	private static AtomicInteger idCounter = new AtomicInteger(0); 
	
	public static int getCurrentId() {
		return idCounter.get();
	}
	
	public static synchronized int getNextId() {
		return idCounter.incrementAndGet();
	}
}
