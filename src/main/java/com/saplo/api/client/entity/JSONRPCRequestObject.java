/**
 * 
 */
package com.saplo.api.client.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A wrapper for JSON-RPC v2.0 JSONRPC-Request
 * as specified here http://groups.google.com/group/json-rpc/web/json-rpc-2-0
 * 
 * @author progre55
 */
public class JSONRPCRequestObject {
	
	private Integer id;
	private String method;
	private Object params;
	private final String version = "2.0";
	
	public JSONRPCRequestObject(Integer id) {
		this.id = id;
	}
	
	public JSONRPCRequestObject(Integer id, String method) {
		this.id = id;
		this.method = method;
	}
	
	public JSONRPCRequestObject(Integer id, String method, JSONObject params) {
		this.id = id;
		this.method = method;
		this.params = params;
	}

	public JSONRPCRequestObject(Integer id, String method, JSONArray params) {
		this.id = id;
		this.method = method;
		this.params = params;
	}

	/**
	 * Constructs a JSONObject from the given wrapper and returns
	 * 
	 * @return the JSONObject
	 */
	public JSONObject getJSONObject() {
		JSONObject object = new JSONObject();
		
		try {
			object.put("jsonrpc", version);
			object.put("method", method);
			object.put("params", params);
			object.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the params
	 */
	public Object getParams() {
		return params;
	}

	/**
	 * Set params of type JSONObject
	 * 
	 * @param params - JSONObject params
	 */
	public void setParams(JSONObject params) {
		this.params = params;
	}
	
	/**
	 * Set params of type JSONArray
	 * 
	 * @param params - JSONArray params
	 */
	public void setParams(JSONArray params) {
		this.params = params;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getJSONObject().toString();
	}

	/**
	 * toString() with indentation
	 *  
	 * @param indent
	 * @return 
	 */
	public String toString(int indent) {
		try {
			return this.getJSONObject().toString(indent);
		} catch (JSONException e) {
			e.printStackTrace();
			return toString();
		}
	}
}
