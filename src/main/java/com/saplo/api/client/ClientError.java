package com.saplo.api.client;

public class ClientError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClientError(String message) {
		super(message);
	}

	public ClientError(Throwable ex) {
		super(ex);
	}
}
