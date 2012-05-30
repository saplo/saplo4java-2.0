/**
 * 
 */
package com.saplo.api.client.manager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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

	/**
	 * Get a SaploAccount object containing your account information, calls made/left, etc.
	 * 
	 * @return SaploAccount
	 * 
	 * @throws SaploClientException
	 */
	public SaploAccount get() throws SaploClientException {
		JSONObject params = new JSONObject();

		JSONRPCRequestObject message = new JSONRPCRequestObject(client.getNextId(), "account.get", params);
		JSONRPCResponseObject responseMessage = client.sendAndReceive(message);

		JSONObject rawResult = (JSONObject)client.parseResponse(responseMessage);

		return SaploAccount.convertFromJSONToAccount(rawResult);
	}

	/**
	 * Get a list of methods available to us. Knowing the list of methods and their parameters, 
	 * it's possible to make custom calls to the API, as described below:
	 * 
	 *  <pre>
	 *	 JSONObject collectionParams = new JSONObject();
	 *	 collectionParams.put("name", "Custom");
	 *	 collectionParams.put("language", "en");
	 *	 collectionParams.put("description", "created manually!");
	 *	 
	 *	 JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.create", collectionParams);
	 * 	 
	 *	 JSONRPCResponseObject response = client.sendAndReceive(request);
	 *	 
	 *	 JSONObject result = (JSONObject)client.parseResponse(response);
	 *
	 *	 int collectionId = result.getInt("collection_id");
	 *  </pre>
	 *  
	 * @return methods
	 * 
	 * @throws SaploClientException
	 */
	public List<String> listMethods() throws SaploClientException {
		JSONObject params = new JSONObject();

		JSONRPCRequestObject message = new JSONRPCRequestObject(client.getNextId(), "saplo.listMethods", params);
		JSONRPCResponseObject responseMessage = client.sendAndReceive(message);

		JSONArray rawResult = (JSONArray)client.parseResponse(responseMessage);

		List<String> methods = new ArrayList<String>();
		for(int i = 0; i < rawResult.length(); i++) {
			methods.add(rawResult.optString(i));
		}
		return methods;
	}

}
