package com.saplo.api.client;

public class ErrorResponse extends ClientError {

	private static final long serialVersionUID = 1L;
	
	String trace;

	public ErrorResponse(Integer code, String message, String trace) {
		super(formatMessage(code, message, trace));
		this.trace = trace;
	}

	static String formatMessage(Integer code, String message, String trace) {
		StringBuilder result = new StringBuilder(); 
		result.append(code == null ? "JSONRPC error: "
				: "JSONRPC error code " + code.toString() + ": ");
		if(message != null)
			result.append("\n Caused by " + message);
		if(trace != null)
			result.append("\n Stack trace: \n" + trace);
		return result.toString();
	}
}
