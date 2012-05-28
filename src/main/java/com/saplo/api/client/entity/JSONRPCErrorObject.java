/**
 * 
 */
package com.saplo.api.client.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.saplo.api.client.ResponseCodes;
import com.saplo.api.client.SaploClientException;

/**
 * A wrapper for JSON-RPC v2.0 JSONRPC-Error object
 * as specified here http://groups.google.com/group/json-rpc/web/json-rpc-2-0
 * 
 * @author progre55
 *
 */
public class JSONRPCErrorObject {

    private Integer code;
    private String message;
    private Object data;
    private String rawErrorMessage;
    private SaploClientException clientException;

    /**
     * A constructor that takes in an "error" object that 
     * MUST contain an error code of type int
     * 
     * @param error - and error object received from an rpc-response
     */
    public JSONRPCErrorObject(JSONObject error) {
	try {
		code = error.getInt("code");
	} catch (JSONException e) {
		code = ResponseCodes.CODE_MALFORMED_RESPONSE;
	}

	message = error.optString("msg");
	
	if(error.has("data")) {
	    data = error.opt("data");
	    clientException = new SaploClientException(message, code, new Throwable(data.toString()));
	} else {
	    clientException = new SaploClientException(message, code);
	}

	rawErrorMessage = error.toString();



    }

    /**
     * @return the code
     */
    public int getCode() {
	return code;
    }

    /**
     * @return the message
     */
    public String getMessage() {
	return message;
    }

    /**
     * @return the data
     */
    public Object getData() {
	if(data != null)
	    return data.toString();

	return "";
    }

    /**
     * Returns the raw JSON error message received from the server.
     */
    public String toString() {
	return rawErrorMessage;
    }
    
    /**
     * Get the clientException to throw it
     * @return clientException
     */
    public SaploClientException getClientException() {
	return clientException;
    }
}
