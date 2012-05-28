/**
 * 
 */
package com.saplo.api.client.entity;

import org.json.JSONObject;

/**
 * A wrapper for JSON-RPC v2.0 JSONRPC-Response
 * as specified here http://groups.google.com/group/json-rpc/web/json-rpc-2-0
 * 
 * @author progre55
 */
public class JSONRPCResponseObject {

	private Integer id;
	private Object result;
	private JSONRPCErrorObject error;
	private String version;
	private boolean success = false;
	private String rawMessage;
	
	/**
	 * A constructor that takes in a JSON-RPC response and parses the fields
	 * 
	 * @param response - a JSON-RPC response object, MUST have a "result"
	 *  parameter on SUCCESS or "error" on FAILURE
	 */
	public JSONRPCResponseObject(JSONObject response) {
		if(response.has("result")) {
			result = response.opt("result");
			success = true;
			if(result == null) {
				error = handleError(response);
				success = false;
			}
		} else {
			error = handleError(response);
			success = false;
		}
		id = response.optInt("id");
		version = response.optString("jsonrpc");
		rawMessage = response.toString();
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @return the error
	 */
	public JSONRPCErrorObject getError() {
		return error;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the status
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Returns the raw JSON message received from the server.
	 */
	public String toString() {
		return rawMessage;
	}

	/*
	 * tries to parse an error object in a given response object 
	 */
	private JSONRPCErrorObject handleError(JSONObject response) {
		JSONObject err;
		if(response.has("error")) {
			err = response.optJSONObject("error");
			return new JSONRPCErrorObject(err);
		}
		return null;
	}
}
