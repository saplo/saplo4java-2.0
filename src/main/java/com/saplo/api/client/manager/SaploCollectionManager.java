/**
 * 
 */
package com.saplo.api.client.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.saplo.api.client.ResponseCodes;
import com.saplo.api.client.SaploClient;
import com.saplo.api.client.SaploClientException;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.entity.SaploCollection;
import com.saplo.api.client.entity.SaploFuture;
import com.saplo.api.client.entity.SaploText;
import com.saplo.api.client.util.ClientUtil;

/**
 * A manager class for operations on {@link SaploCollection} objects
 * 
 * @author progre55
 */
public class SaploCollectionManager {

	private SaploClient client;
	private ExecutorService es;
	private static final int nThreads = 5;

	/**
	 * Constructor
	 * 
	 * @param clientToUse - the {@link SaploClient} to use with this manager
	 */
	public SaploCollectionManager(SaploClient clientToUse) {
		this.client = clientToUse;
		es = Executors.newFixedThreadPool(nThreads);
	}

	/**
	 * Create a new collection to store texts in.
	 * After the collection is created, the method populates the collectionId
	 * 
	 * @param saploCollection - the new {@link SaploCollection} object
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void create(SaploCollection saploCollection) throws JSONException, SaploClientException {

		if(ClientUtil.NULL_STRING.equals(saploCollection.getName()))
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection.name");
		if(null == saploCollection.getLanguage())
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection.language");

		JSONObject params = new JSONObject();
		params.put("name", saploCollection.getName());
		params.put("language", saploCollection.getLanguage().toString());
		if(!ClientUtil.NULL_STRING.equals(saploCollection.getDescription()))
			params.put("description", saploCollection.getDescription());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.create", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonColl = (JSONObject)client.parseResponse(response);

		SaploCollection.convertFromJSONToCollection(jsonColl, saploCollection);
	}

	/**
	 * Asynchronously create a new collection to store texts in.
	 * This method returns a {@link SaploFuture}<{@link Boolean}> object, 
	 * which is <code>true</code> in case of success and <code>false</code> in case of failure.
	 * 
	 * Here is an example usage:
	 * <pre>
	 *	SaploCollection myCollection = new SaploCollection("test col", Language.en);
	 *	Future<Boolean> future = collectionMgr.createAsync(myCollection);
	 *
	 *	// do some other stuff here
	 *
	 *	boolean createOk = false;
	 *	try {
	 *		createOk = future.get(3, TimeUnit.SECONDS);
	 *	} catch (InterruptedException e) {
	 *		e.printStackTrace();
	 *	} catch (ExecutionException e) {
	 *		e.printStackTrace();
	 *	} catch (TimeoutException e) {
	 *		future.cancel(false);
	 *		e.printStackTrace();
	 *	}
	 *
	 *	if(createOk) {
	 *		int myCollectionId = myCollection.getId();
	 *		// do some other operations as you prefer
	 *	} else {
	 *		// something went wrong
	 *	}
	 * </pre>
	 * 
	 * 
	 * @param saploCollection - the {@link SaploCollection} to create
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 */
	public SaploFuture<Boolean> createAsync(final SaploCollection saploCollection) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					create(saploCollection);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Get a {@link SaploCollection} object populated from the API.
	 * 
	 * @param collection- the {@link SaploCollection} object with just an id.
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void get(SaploCollection saploCollection) throws JSONException, SaploClientException {

		if(saploCollection.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection.id");

		JSONObject params = new JSONObject();
		params.put("collection_id", saploCollection.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.get", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonColl = (JSONObject)client.parseResponse(response);

		SaploCollection.convertFromJSONToCollection(jsonColl, saploCollection);
	}

	/**
	 * Asynchronously get a {@link SaploCollection} object populated from the API.
	 * For an example usage, see {@link #createAsync(SaploCollection)}
	 * 
	 * @param collection- the {@link SaploCollection} object with just an id.
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> getAsync(final SaploCollection saploCollection) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					get(saploCollection);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * A convenient way of getting SaploCollections with only the required parameters
	 * 
	 * @param collectionId
	 * @return saploCollection
	 * 
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploCollection get(int collectionId) throws JSONException, SaploClientException {
		SaploCollection col = new SaploCollection();
		col.setId(collectionId);
		
		get(col);
		
		return col;
	}
	
	/**
	 * Update an existing collection.
	 * 
	 * @param saploCollection - the {@link SaploCollection} object to update
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void update(SaploCollection saploCollection) throws JSONException, SaploClientException {

		if(saploCollection.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection.id");

		JSONObject params = new JSONObject();
		params.put("collection_id", saploCollection.getId());
		if(!ClientUtil.NULL_STRING.equals(saploCollection.getName()))
			params.put("name", saploCollection.getName());
		if(!ClientUtil.NULL_STRING.equals(saploCollection.getDescription()))
			params.put("description", saploCollection.getDescription());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.update", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonColl = (JSONObject)client.parseResponse(response);

		SaploCollection.convertFromJSONToCollection(jsonColl, saploCollection);
	}

	/**
	 * Asynchronously update an existing collection.
	 * For an example usage, see {@link #createAsync(SaploCollection)}
	 * 
	 * @param saploCollection - the {@link SaploCollection} object to be updated
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> updateAsync(final SaploCollection saploCollection) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					update(saploCollection);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Delete a given collection.
	 * 
	 * @param saploCollection - the {@link SaploCollection} object to be deleted
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void delete(SaploCollection saploCollection) throws JSONException, SaploClientException {

		if(saploCollection.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection.id");

		JSONObject params = new JSONObject();
		params.put("collection_id", saploCollection.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.delete", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonColl = (JSONObject)client.parseResponse(response);

		SaploCollection.convertFromJSONToCollection(jsonColl, saploCollection);
	}

	/**
	 * Asynchronously delete an existing collection.
	 * For an example usage, see {@link #createAsync(SaploCollection)}
	 * 
	 * @param saploCollection - the {@link SaploCollection} object to be deleted
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> deleteAsync(final SaploCollection saploCollection) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					delete(saploCollection);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * A convenient way of deleting SaploCollections with only the required parameters
	 * 
	 * @param collectionId
	 * @return saploCollection - the deleted SaploCollection
	 * 
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public SaploCollection delete(int collectionId) throws JSONException, SaploClientException {
		SaploCollection col = new SaploCollection();
		col.setId(collectionId);
		
		delete(col);
		
		return col;
	}

	/**
	 * List all collections the current user has access to.
	 * 
	 * @return collectionList - a {@link List} containing all the user {@link SaploCollection}s
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public List<SaploCollection> list() throws JSONException, SaploClientException {
		List<SaploCollection> colList = new ArrayList<SaploCollection>();
		JSONObject params = new JSONObject();

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.list", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawListResult = (JSONObject)client.parseResponse(response);

		JSONArray collections = rawListResult.getJSONArray("collections");
		for(int i = 0; i < collections.length(); i++) {
			JSONObject jsonColl = collections.getJSONObject(i);

			SaploCollection saploCollection = SaploCollection.convertFromJSONToCollection(jsonColl);
			colList.add(saploCollection);
		}

		return colList;
	}

	/**
	 * Asynchronously list all collections the current user has access to.
	 * For an example usage, see {@link #createAsync(SaploCollection)}
	 * 
	 * @return {@link SaploFuture}<{@link List}<{@link SaploCollection}>> containing all the user {@link SaploCollection}s
	 * @throws SaploClientException
	 */
	public SaploFuture<List<SaploCollection>> listAsync() {
		return new SaploFuture<List<SaploCollection>>(es.submit(new Callable<List<SaploCollection>>() {
			public List<SaploCollection> call() throws SaploClientException {
				List<SaploCollection> collectionList = null;
				try {
					collectionList = list();
				} catch (JSONException e) {
					return null;
				}
				return collectionList;
			}
		}));
	}

	/**
	 * Reset a given collection.
	 * Warning! This method removes all {@link SaploText}s in the collection 
	 * and all existing results. SaploText id counter in the collection will be reset.
	 * 
	 * @param saploCollection - the {@link SaploCollection} to be reset
	 * @throws JSONException if failure
	 * @throws SaploClientException 
	 */
	public void reset(SaploCollection saploCollection) throws JSONException, SaploClientException {
		if(saploCollection.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection.id");

		JSONObject params = new JSONObject();
		params.put("collection_id", saploCollection.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "collection.reset", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonColl = (JSONObject)client.parseResponse(response);

		SaploCollection.convertFromJSONToCollection(jsonColl, saploCollection);
	}

	/**
	 * Asynchronously reset a given collection.
	 * For an example usage, see {@link #createAsync(SaploCollection)}
	 * 
	 * @param saploCollection - the {@link SaploCollection} object to be reset
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> resetAsync(final SaploCollection saploCollection) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					reset(saploCollection);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}
}
