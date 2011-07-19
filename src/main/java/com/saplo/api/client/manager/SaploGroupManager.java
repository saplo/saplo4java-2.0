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

import com.saplo.api.client.ClientError;
import com.saplo.api.client.ResponseCodes;
import com.saplo.api.client.SaploClient;
import com.saplo.api.client.SaploClientException;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.entity.SaploCollection;
import com.saplo.api.client.entity.SaploFuture;
import com.saplo.api.client.entity.SaploGroup;
import com.saplo.api.client.entity.SaploText;
import com.saplo.api.client.util.ClientUtil;

/**
 * A manager class for operations on {@link SaploGroup} objects
 * 
 * @author progre55
 */
public class SaploGroupManager {

	//	private static Logger logger = Logger.getLogger(SaploGroupManager.class);

	private SaploClient client;
	private ExecutorService es;
	private static final int thread_count = 5;

	/**
	 * The default and only constructor.
	 * 
	 * @param clientToUse - a {@link SaploClient} object to be used with this manager.
	 */
	public SaploGroupManager(SaploClient clientToUse) {
		this.client = clientToUse;
		es = Executors.newFixedThreadPool(thread_count);
	}

	/**
	 * Create a new text group on the API server
	 * 
	 * @param saploGroup - the new {@link SaploGroup} object to be created
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void create(SaploGroup saploGroup) throws JSONException, SaploClientException {
		if(ClientUtil.NULL_STRING.equals(saploGroup.getName()))
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.name");
		if(saploGroup.getLanguage() == null)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.language");

		JSONObject params = new JSONObject();
		params.put("name", saploGroup.getName());
		if(!ClientUtil.NULL_STRING.equals(saploGroup.getDescription()))
			params.put("description", saploGroup.getDescription());
		params.put("language", saploGroup.getLanguage().toString());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.create", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonGroup = (JSONObject)client.parseResponse(response);

		SaploGroup.convertFromJSONToGroup(jsonGroup, saploGroup);
	}

	/**
	 * Asynchronously create a new text group on the API server.
	 * This method returns a {@link SaploFuture}<{@link Boolean}> object, 
	 * which is <code>true</code> in case of success and <code>false</code> in case of failure.
	 * 
	 * Here is an example usage:
	 * <pre>
	 *	SaploGroup myGroup = new SaploGroup("my example group", Language.en);
	 *	Future<Boolean> future = groupMgr.createAsync(myGroup);
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
	 *		int myGroupId = myGroup.getId();
	 *
	 *		// do some other operations as you prefer
	 *
	 *	} else {
	 *		// something went wrong, so do smth about it
	 *	}
	 * </pre>
	 * 
	 * 
	 * @param saploGroup - the new {@link SaploGroup} object to be created
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> createAsync(final SaploGroup saploGroup) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					create(saploGroup);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Update an existing group's name or description.
	 * 
	 * @param saploGroup - the {@link SaploGroup} to update
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void update(SaploGroup saploGroup) throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());
		if(!ClientUtil.NULL_STRING.equals(saploGroup.getName()))
			params.put("name", saploGroup.getName());
		if(!ClientUtil.NULL_STRING.equals(saploGroup.getDescription()))
			params.put("description", saploGroup.getDescription());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.update", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonGroup = (JSONObject)client.parseResponse(response);

		SaploGroup.convertFromJSONToGroup(jsonGroup, saploGroup);
	}

	/**
	 * Asynchronously update an existing group's name or description.
	 * For an example usage, see {@link #createAsync(SaploGroup)}
	 * 
	 * @param saploGroup - the {@link SaploGroup} to update
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> updateAsync(final SaploGroup saploGroup) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					update(saploGroup);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Reset the given group.
	 * WARNING: This will remove all texts linked to that group and remove all results for the group
	 * 
	 * @param saploGroup - the group to reset
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public void reset(SaploGroup saploGroup) throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.reset", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonGroup = (JSONObject)client.parseResponse(response);

		SaploGroup.convertFromJSONToGroup(jsonGroup, saploGroup);

	}

	/**
	 * Asynchronously reset a group.
	 * For an example usage on async, see {@link #createAsync(SaploGroup)}
	 * 
	 * @param saploGroup - the {@link SaploGroup} to reset
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> resetAsync(final SaploGroup saploGroup) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					reset(saploGroup);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Delete a given group
	 * WARNING: This will remove the group and all its associated results.
	 * 
	 * @param saploGroup - the group to delete
	 * @return success/fail
	 * 
	 * @throws JSONException
	 * @throws SaploClientException
	 */
	public boolean delete(SaploGroup saploGroup) throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.delete", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject result = (JSONObject)client.parseResponse(response);

		return result.getBoolean("success");
	}

	/**
	 * Asynchronously delete a group.
	 * For an example usage on async, see {@link #createAsync(SaploGroup)}
	 * 
	 * @param saploGroup - the {@link SaploGroup} to delete
	 * @return success/fail
	 * @throws SaploClientException
	 */	
	public SaploFuture<Boolean> deleteAsync(final SaploGroup saploGroup) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					return delete(saploGroup);
				} catch (JSONException e) {
					return false;
				}
			}
		}));
	}

	/**
	 * List all the groups that belong to the user.
	 * 
	 * @return a {@link List} containing all the users {@link SaploGroup}s.
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public List<SaploGroup> list() throws JSONException, SaploClientException {
		List<SaploGroup> groupList = new ArrayList<SaploGroup>();

		JSONObject params = new JSONObject();
		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.list", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawJson = (JSONObject)client.parseResponse(response);

		JSONArray groups = rawJson.getJSONArray("groups");
		for(int i = 0; i < groups.length(); i++) {
			JSONObject jsonGroup = groups.getJSONObject(i);
			SaploGroup saploGroup = SaploGroup.convertFromJSONToGroup(jsonGroup);
			groupList.add(saploGroup);
		}

		return groupList;
	}

	/**
	 * Asynchronously list all the groups that belong to the user.
	 * For an example usage, see {@link #createAsync(SaploGroup)}
	 * 
	 * @return {@link SaploFuture}<{@link List}<{@link SaploGroup}>> containing all the user {@link SaploGroup}s
	 * @throws
	 */
	public SaploFuture<List<SaploGroup>> listAsync() {
		return new SaploFuture<List<SaploGroup>>(es.submit(new Callable<List<SaploGroup>>() {
			public List<SaploGroup> call() throws SaploClientException {
				List<SaploGroup> groupList = null;
				try {
					groupList = list();
				} catch (JSONException e) {
					return null;
				}
				return groupList;
			}
		}));
	}

	/**
	 * Get a list of all texts ({@link SaploText}) that exist in a group.
	 * 
	 * @param saploGroup - The group whose text list we want. {@link SaploGroup#getId()} is mandatory.
	 * @return textList - a {@link List} populated with {@link SaploText} objects 
	 * (only {@link SaploCollection#getId()} and {@link SaploText#getId()} params)
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public List<SaploText> listTexts(SaploGroup saploGroup) throws JSONException, SaploClientException {
		List<SaploText> textList = new ArrayList<SaploText>();

		if(saploGroup.getId() < 1)
			throw new ClientError("Missing required parameters: group.group_id");

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.listTexts", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawJson = (JSONObject)client.parseResponse(response);

		JSONArray texts = rawJson.getJSONArray("texts");
		for(int i = 0; i < texts.length(); i++) {
			JSONObject jsonText = texts.getJSONObject(i);
			SaploText saploText = SaploText.convertFromJSONToText(jsonText);
			textList.add(saploText);
		}

		return textList;
	}

	/**
	 * Asynchronously get a list of all texts ({@link SaploText}) that exist in a group.
	 * For an example usage, see {@link #createAsync(SaploGroup)}
	 * 
	 * @param group - The group whose text list we want. {@link SaploGroup#getId()} is mandatory.
	 * @return {@link SaploFuture}<{@link List}<{@link SaploText}>> populated with {@link SaploText} objects 
	 * (only {@link SaploCollection#getId()} and {@link SaploText#getId()} params)
	 * @throws
	 */
	public SaploFuture<List<SaploText>> listTextsAsync(final SaploGroup saploGroup) {
		return new SaploFuture<List<SaploText>>(es.submit(new Callable<List<SaploText>>() {
			public List<SaploText> call() throws SaploClientException {
				List<SaploText> textsList = null;
				try {
					textsList = listTexts(saploGroup);
				} catch (JSONException e) {
					return null;
				}
				return textsList;
			}
		}));
	}

	/**
	 * Add a text to a given group.
	 * 
	 * @param saploGroup - which {@link SaploGroup} to add the text to
	 * @param saploText - the {@link SaploText} to add
	 * @return true - on success
	 * 
	 * @throws JSONException on failure
	 * @throws SaploClientException 
	 */
	public boolean addText(SaploGroup saploGroup, SaploText saploText) throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");
		if(saploText.getCollection() == null || saploText.getCollection().getId() <= 0)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "text.collection", "text.collection.id");
		if(saploText.getId() <= 0)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "text.id");

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());
		params.put("collection_id", saploText.getCollection().getId());
		params.put("text_id", saploText.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.addText", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject result = (JSONObject)client.parseResponse(response);

		return result.getBoolean("success");
	}

	/**
	 * Asynchronously add a text to a given group.
	 * For an example usage, see {@link #createAsync(SaploGroup)}
	 * 
	 * @param saploGroup - which {@link SaploGroup} to add the text to
	 * @param saploText - the {@link SaploText} to add
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> addTextAsync(final SaploGroup saploGroup, final SaploText saploText) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					addText(saploGroup, saploText);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Delete a text from a given group.
	 * 
	 * @param saploGroup - which {@link SaploGroup} to delete the text from
	 * @param saploText - the {@link SaploText} to delete
	 * @return true - on success
	 * 
	 * @throws JSONException on failure
	 * @throws SaploClientException 
	 */
	public boolean deleteText(SaploGroup saploGroup, SaploText saploText) throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");
		if(saploText.getCollection() == null || saploText.getCollection().getId() <= 0)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "text.collection", "text.collection.id");
		if(saploText.getId() <= 0)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "text.id");

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());
		params.put("collection_id", saploText.getCollection().getId());
		params.put("text_id", saploText.getId());

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.deleteText", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		return (Boolean)client.parseResponse(response);
	}

	/**
	 * Asynchronously delete text from a given group.
	 * For an example usage, see {@link #createAsync(SaploGroup)}
	 * 
	 * @param saploGroup - which {@link SaploGroup} to delete text from
	 * @param saploText - the {@link SaploText} to delete
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> deleteTextAsync(final SaploGroup saploGroup, final SaploText saploText) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					deleteText(saploGroup, saploText);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Get related groups for a given {@link SaploGroup} object.
	 * Then the related group list can be retrieved by {@link SaploGroup#getRelatedGroups()}
	 * 
	 * @param saploGroup - the {@link SaploGroup} object to search related groups against
	 * @param groupScope - the {@link SaploGroup}s the given group should be compared to.
	 * By default, all the user groups are searched.
	 * @param wait - maximum time to wait for the result to be calculated.
	 * 
	 * @throws JSONException
	 * @throws SaploClientException 
	 */
	public void relatedGroups(SaploGroup saploGroup, SaploGroup[] groupScope, int wait) 
	throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");

		List<SaploGroup> relatedGroupsList = new ArrayList<SaploGroup>();

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());
		if(groupScope != null && groupScope.length > 0) {
			int groupIds[] = new int[groupScope.length];
			for(int i = 0; i < groupScope.length; i++) {
				groupIds[i] = groupScope[i].getId();
			}
			params.put("group_scope", groupIds);
		}
		if(wait >= 0)
			params.put("wait", wait);

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.relatedGroups", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawResult = (JSONObject)client.parseResponse(response);

		JSONArray groups = rawResult.getJSONArray("related_groups");
		for(int i = 0; i < groups.length(); i++) {
			JSONObject groupJson = groups.getJSONObject(i);
			SaploGroup relGroup = SaploGroup.convertFromJSONToGroup(groupJson);
			relGroup.setRelatedToGroup(saploGroup);
			relatedGroupsList.add(relGroup);
		}
		saploGroup.setRelatedGroups(relatedGroupsList);
	}

	/**
	 * Asynchronously get related groups for a given {@link SaploGroup} object.
	 * Then the related group list can be retrieved by {@link SaploGroup#getRelatedGroups()}
	 * 
	 * Here is an example usage:
	 * <pre>
	 *	SaploGroup myGroup = new SaploGroup("my example group", Language.en);
	 *	SaploFuture<Boolean> future = groupMgr.relatedGroupsAsync(myGroup, relatedBy, groupScope, 10);
	 *
	 *	// do some other stuff here
	 *
	 *	boolean processOk = false;
	 *	try {
	 *		processOk = future.get(3, TimeUnit.SECONDS);
	 *	} catch (InterruptedException e) {
	 *		e.printStackTrace();
	 *	} catch (ExecutionException e) {
	 *		e.printStackTrace();
	 *	} catch (TimeoutException e) {
	 *		future.cancel(false);
	 *		e.printStackTrace();
	 *	}
	 *
	 *	if(processOk) {
	 *		List<SaploGroup> relatedGroups = myGroup.getRelatedGroups();
	 *
	 *		// do some other operations as you prefer
	 *
	 *	} else {
	 *		// something went wrong
	 *	}
	 * </pre>
	 * 
	 * @param saploGroup - the {@link SaploGroup} object to search related groups against
	 * @param groupScope - the {@link SaploGroup}s the given group should be compared to.
	 * By default, all the user groups are searched.
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @return SaploFuture<relatedGroupsList> - a {@link List} containing related groups to the given group
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> relatedGroupsAsync(final SaploGroup saploGroup, final SaploGroup[] groupScope, final int wait) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					relatedGroups(saploGroup, groupScope, wait);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}

	/**
	 * Search for texts that are related to the given group.
	 * Then, the related text list can be retrieved by {@link SaploGroup#getRelatedTexts()}
	 * 
	 * @param saploGroup - the {@link SaploGroup} object to search related texts against
	 * @param collection - Search the given collections to find related texts.
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @param limit - the maximum number of related texts in the result. 
	 * @throws SaploClientException 
	 */
	public void relatedTexts(SaploGroup saploGroup, SaploCollection collection, int wait, int limit) throws JSONException, SaploClientException {
		if(saploGroup.getId() < 1)
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "group.id");

		List<SaploText> relatedTextsList = new ArrayList<SaploText>();

		JSONObject params = new JSONObject();
		params.put("group_id", saploGroup.getId());
		if(collection != null) {
			params.put("collection_scope", collection.getId());
		} else {
			throw new SaploClientException(ResponseCodes.MSG_CLIENT_FIELD, 
					ResponseCodes.CODE_CLIENT_FIELD, "collection_scope");
		}
		if(wait >= 0)
			params.put("wait", wait);
		if(limit > 0)
			params.put("limit", limit);

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "group.relatedTexts", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawResult = (JSONObject)client.parseResponse(response);

		JSONArray texts = rawResult.getJSONArray("related_texts");

		for(int i = 0; i < texts.length(); i++) {
			JSONObject textJson = texts.getJSONObject(i);
			SaploText relText = SaploText.convertFromJSONToText(textJson);
			relText.setRelatedToGroup(saploGroup);
			relatedTextsList.add(relText);
		}

		saploGroup.setRelatedTexts(relatedTextsList);
	}

	/**
	 * Asynchronously search for texts that are related to the given group.
	 * Then, the related text list can be retrieved by {@link SaploGroup#getRelatedTexts()}
	 * 
	 * @param saploGroup - the {@link SaploGroup} object to search related texts against
	 * @param collection - Search the given collections to find related texts.
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @param limit - the maximum number of related texts in the result. 
	 * @return SaploFuture<relatedTextsList> - a {@link List} containing related texts to the given group
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> relatedTextsAsync(final SaploGroup saploGroup, final SaploCollection collection, final int wait, final int limit) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				try {
					relatedTexts(saploGroup, collection, wait, limit);
				} catch (JSONException e) {
					return false;
				}
				return true;
			}
		}));
	}
}
