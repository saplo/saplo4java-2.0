/**
 * 
 */
package com.saplo.api.client.manager;

import org.json.JSONException;
import org.json.JSONObject;

import com.saplo.api.client.SaploClient;
import com.saplo.api.client.SaploClientException;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.entity.SaploAccount;

/**
 * @author progre55
 *
 */
public class SaploAccountManager {

	private SaploClient client;
	
	public SaploAccountManager(SaploClient clientToUse) {
		this.client = clientToUse;
	}
	
	public SaploAccount get() throws JSONException, SaploClientException {
		JSONObject params = new JSONObject();
		
		JSONRPCRequestObject message = new JSONRPCRequestObject(client.getNextId(), "auth.accessToken", params);
		JSONRPCResponseObject responseMessage = client.sendAndReceive(message);
		
		JSONObject rawResult = (JSONObject)client.parseResponse(responseMessage);

		return SaploAccount.convertFromJSONToAccount(rawResult);
	}

}
