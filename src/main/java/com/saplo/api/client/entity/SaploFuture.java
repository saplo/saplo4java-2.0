/**
 * 
 */
package com.saplo.api.client.entity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.saplo.api.client.ResponseCodes;
import com.saplo.api.client.SaploClientException;

/**
 * A class to wrap around {@link Future} objects and convert {@link ExecutionException}s to {@link SaploClientException}
 * 
 * @author progre55
 */
public class SaploFuture<V> {

	private Future<V> future;
	
	public SaploFuture(Future<V> future) {
		this.future = future;
	}
	
	/**
	 * @see Future#get()
	 * 
	 * @return
	 * @throws SaploClientException - converts the {@link Future#get()} exceptions into a SaploClientException
	 */
	public V get() throws SaploClientException {
		try {
			return future.get();
		} catch (ExecutionException ee) {
			if(ee.getCause() instanceof SaploClientException)
				throw (SaploClientException)ee.getCause();
			else
				throw new SaploClientException(ResponseCodes.MSG_UNKNOWN_EXCEPTION, ResponseCodes.CODE_UNKNOWN_EXCEPTION, ee);
		} catch (InterruptedException ie) {
			throw new SaploClientException(ResponseCodes.MSG_UNKNOWN_EXCEPTION, ResponseCodes.CODE_UNKNOWN_EXCEPTION, ie);
		}
	}
	
	/**
	 * @see Future#get(long, TimeUnit)
	 * 
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws SaploClientException - converts the {@link Future#get(long, TimeUnit)} exceptions into a SaploClientException
	 */
	public V get(long timeout, TimeUnit unit) throws SaploClientException {
		try {
			return future.get(timeout, unit);
		} catch (ExecutionException ee) {
			if(ee.getCause() instanceof SaploClientException)
				throw (SaploClientException)ee.getCause();
			else
				throw new SaploClientException(ResponseCodes.MSG_UNKNOWN_EXCEPTION, ResponseCodes.CODE_UNKNOWN_EXCEPTION, ee);
		} catch (InterruptedException ie) {
			throw new SaploClientException(ResponseCodes.MSG_UNKNOWN_EXCEPTION, ResponseCodes.CODE_UNKNOWN_EXCEPTION, ie);
		} catch (TimeoutException te) {
			throw new SaploClientException(ResponseCodes.MSG_STILL_PROCESSING, ResponseCodes.CODE_STILL_PROCESSING, te);
		}
	}
	
	/**
	 * @see Future#cancel(boolean)
	 * @param mayInterruptIfRunning
	 */
	public void cancel(boolean mayInterruptIfRunning) {
		future.cancel(mayInterruptIfRunning);
	}
	
	/**
	 * @see Future#isCancelled()
	 * @return
	 */
	public boolean isCancelled() {
		return future.isCancelled();
	}

	/**
	 * @see Future#isDone()
	 * @return
	 */
	public boolean isDone() {
		return future.isDone();
	}
	
	
}
