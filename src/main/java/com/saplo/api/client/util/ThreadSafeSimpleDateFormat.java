/**
 * 
 */
package com.saplo.api.client.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A ThreadSafe implementation of SimpleDateFormat, wraps a SimpleDateFormat and synchronizes it
 * (might be a liittle slow, but makes sure dates don't get messed up)
 * 
 * @author progre55
 */
public class ThreadSafeSimpleDateFormat {
	
	private SimpleDateFormat sf;

	public ThreadSafeSimpleDateFormat(String format) {
		this.sf = new SimpleDateFormat(format);
	}

	public synchronized String format(Date date) {
		return sf.format(date);
	}

	public synchronized Date parse(String string) throws ParseException {
		return sf.parse(string);
	}
}