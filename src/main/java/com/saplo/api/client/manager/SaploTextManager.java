/**
 * 
 */
package com.saplo.api.client.manager;

import static com.saplo.api.client.ResponseCodes.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.saplo.api.client.SaploClient;
import com.saplo.api.client.SaploClientException;
import com.saplo.api.client.entity.JSONRPCRequestObject;
import com.saplo.api.client.entity.JSONRPCResponseObject;
import com.saplo.api.client.entity.SaploCollection;
import com.saplo.api.client.entity.SaploFuture;
import com.saplo.api.client.entity.SaploGroup;
import com.saplo.api.client.entity.SaploTag;
import com.saplo.api.client.entity.SaploText;
import com.saplo.api.client.entity.SaploText.RelatedBy;
import com.saplo.api.client.util.ClientUtil;
import com.saplo.api.client.util.ThreadSafeSimpleDateFormat;

/**
 * A manager class for operations on {@link SaploText} objects
 * 
 * @author progre55
 *
 */
public class SaploTextManager {

	private SaploClient client;
	private ExecutorService es;
	private ThreadSafeSimpleDateFormat sf = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * A simple constructor 
	 * 
	 * @param clientToUse - the client to use to communicate to the Saplo-API server
	 */
	public SaploTextManager(SaploClient clientToUse) {
		this.client = clientToUse;
		es = client.getAsyncExecutor();
	}

	/**
	 * Add a text to a collection.
	 * 
	 * @param saploText - the text to be added, should contain a 
	 * {@link SaploText#getCollection()} object of type {@link SaploCollection}
	 * 
	 * @throws SaploClientException 
	 */
	public void create(SaploText saploText) throws SaploClientException {

		verifyCollection(saploText);
		if(ClientUtil.NULL_STRING.equals(saploText.getBody()))
			throw new SaploClientException(MSG_CLIENT_FIELD, CODE_CLIENT_FIELD, "text.body");

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			params.put("body", saploText.getBody());
			if(!ClientUtil.NULL_STRING.equals(saploText.getHeadline()))
				params.put("headline", saploText.getHeadline());
			if(saploText.getPublishDate() != null)
				params.put("publish_date", sf.format(saploText.getPublishDate()));
			else
				params.put("publish_date", sf.format(new Date()));
			if(saploText.getUrl() != null)
				params.put("url", saploText.getUrl().toString());
			if(!ClientUtil.NULL_STRING.equals(saploText.getAuthors()))
				params.put("authors", saploText.getAuthors());
			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());

			params.put("force", saploText.isForce());

		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.create", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonText = (JSONObject)client.parseResponse(response);

		SaploText.convertFromJSONToText(jsonText, saploText);
	}

	/**
	 * Asynchronously create text.
	 * This method returns a {@link SaploFuture}<{@link Boolean}> object, 
	 * which is <code>true</code> in case of success and <code>false</code> in case of failure.
	 * 
	 * Here is an example usage:
	 * <pre>
	 *	SaploText myText = new SaploText(myCollection, myTextBody);
	 *	SaploFuture<Boolean> future = textMgr.createAsync(myText);
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
	 *		int myTextId = myText.getId();
	 *		// do some other operations as you prefer
	 *	} else {
	 *		// something went wrong
	 *	}
	 * </pre>
	 * 
	 * 
	 * @param saploText - the {@link SaploText} to create
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> createAsync(final SaploText saploText) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				create(saploText);
				return true;
			}
		}));
	}

	/**
	 * A convenient method for adding texts with only required parameters.
	 * 
	 * @param collectionId
	 * @param bodyText
	 * @return saploText
	 * 
	 * @throws SaploClientException
	 */
	public SaploText create(int collectionId, String bodyText) throws SaploClientException {
		SaploCollection col = new SaploCollection();
		col.setId(collectionId);

		SaploText saploText = new SaploText(col, bodyText);

		create(saploText);

		return saploText;
	}

	/**
	 * Get a text given its id and collection_id from the API
	 * 
	 * @param saploText - a {@link SaploText} object with a mandatory id and collection parameters
	 * 
	 * @throws SaploClientException 
	 */
	public void get(SaploText saploText) throws SaploClientException {

		verifyCollection(saploText);
		verifyId(saploText);

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			if(saploText.getId() > 0)
				params.put("text_id", saploText.getId());
			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.get", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonText = (JSONObject)client.parseResponse(response);

		SaploText.convertFromJSONToText(jsonText, saploText);
	}

	/**
	 * Asynchronously get a text.
	 * For an example usage, see {@link #createAsync(SaploText)}
	 * 
	 * @param saploText - a {@link SaploText} object with a mandatory id and collection parameters
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> getAsync(final SaploText saploText) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				get(saploText);
				return true;
			}
		}));
	}

	/**
	 * A convenient way of getting text with only the required parameters
	 * 
	 * @param collectionId
	 * @param textId
	 * @return saploText
	 * 
	 * @throws SaploClientException
	 */
	public SaploText get(int collectionId, int textId) throws SaploClientException {
		SaploCollection col = new SaploCollection();
		col.setId(collectionId);

		SaploText text = new SaploText();
		text.setCollection(col);
		text.setId(textId);

		get(text);

		return text;
	}

	/**
	 * Update an existing text.
	 * 
	 * @param saploText - the {@link SaploText} to be updated
	 * 
	 * @throws SaploClientException 
	 */
	public void update(SaploText saploText) throws SaploClientException {

		verifyCollection(saploText);
		verifyId(saploText);

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			if(saploText.getId() > 0)
				params.put("text_id", saploText.getId());
			if(!ClientUtil.NULL_STRING.equals(saploText.getHeadline()))
				params.put("headline", saploText.getHeadline());

			if(!ClientUtil.NULL_STRING.equals(saploText.getBody()))
				params.put("body", saploText.getBody());

			if(null != saploText.getPublishDate())
				params.put("publish_date", sf.format(saploText.getPublishDate()));

			if(null != saploText.getUrl())
				params.put("url", saploText.getUrl().toString());

			if(!ClientUtil.NULL_STRING.equals(saploText.getAuthors()))
				params.put("authors", saploText.getAuthors());

			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());

			params.put("force", saploText.isForce());
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.update", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject jsonText = (JSONObject)client.parseResponse(response);

		SaploText.convertFromJSONToText(jsonText, saploText);
	}

	/**
	 * Asynchronously update a given text.
	 * For an example usage, see {@link #createAsync(SaploText)}
	 * 
	 * @param saploText - the {@link SaploText} to be updated
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> updateAsync(final SaploText saploText) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				update(saploText);
				return true;
			}
		}));
	}

	/**
	 * Delete a text from the {@link SaploCollection} it belongs to.
	 * 
	 * @param saploText - the {@link SaploText} to be deleted, 
	 * should have id and collection attributes
	 * @return true if success
	 * 
	 * @throws SaploClientException 
	 */
	public boolean delete(SaploText saploText) throws SaploClientException {

		verifyCollection(saploText);
		verifyId(saploText);

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			if(saploText.getId() > 0)
				params.put("text_id", saploText.getId());
			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.delete", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject result = (JSONObject)client.parseResponse(response);

		return result.optBoolean("success", false);

	}

	/**
	 * Asynchronously delete a given text.
	 * For an example usage, see {@link #createAsync(SaploText)}
	 * 
	 * @param saploText - the {@link SaploText} to be deleted
	 * @return a {@link SaploFuture}<{@link Boolean}> with success or fail
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> deleteAsync(final SaploText saploText) {
		return new SaploFuture<Boolean>(es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				return delete(saploText);
			}
		}));
	}

	/** 
	 * A convenient way of deleting SaploText by textId
	 *  
	 * @param textId
	 * @return success?
	 * 
	 * @throws SaploClientException
	 */
	public boolean delete(int collectionId, int textId) throws SaploClientException {

		SaploCollection collection = new SaploCollection();
		collection.setId(collectionId);

		SaploText text = new SaploText();
		text.setId(textId);
		text.setCollection(collection);


		return delete(text);
	}

	/**
	 * Get all entity tags that exist in the text. 
	 * Categorized as person, organization, location, url, unknown.
	 * 
	 * @param saploText - the text to extract the {@link SaploTag}s from
	 * @param wait - how long to wait for the API to return (seconds)
	 * @param skipCategorization
	 * @return tagList - a {@link List} containing all the tags extracted
	 * 
	 * @throws SaploClientException 
	 */
	public List<SaploTag> tags(SaploText saploText, int wait, boolean skipCategorization) throws SaploClientException {

		List<SaploTag> tagList = new ArrayList<SaploTag>();

		verifyCollection(saploText);
		verifyId(saploText);

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			if(saploText.getId() > 0)
				params.put("text_id", saploText.getId());
			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());

			if(wait >= 0)
				params.put("wait", wait);
			params.put("skip_categorization", skipCategorization);
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.tags", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawResult = (JSONObject)client.parseResponse(response);

		try {
			JSONArray tags = rawResult.getJSONArray("tags");
			for(int i = 0; i < tags.length(); i++) {
				JSONObject tagJson = tags.getJSONObject(i);
				SaploTag saploTag = SaploTag.convertFromJSONToTag(tagJson);
				tagList.add(saploTag);
			}
		} catch(JSONException je) {
			throw new SaploClientException(CODE_MALFORMED_RESPONSE, je);
		}

		return tagList;
	}

	/**
	 * Asynchronously get all entity tags that exist in the text.
	 * For an example usage, see {@link #createAsync(SaploText)}
	 * 
	 * @param saploText - the text to extract the {@link SaploTag}s from
	 * @param wait - how long to wait for the API to return (seconds)
	 * @param skipCategorization
	 * @return {@link SaploFuture}<{@link List}<{@link SaploTag}>> containing all the tags extracted
	 * @throws SaploClientException
	 */
	public SaploFuture<List<SaploTag>> tagsAsync(final SaploText saploText, final int wait, final boolean skipCategorization) {
		return new SaploFuture<List<SaploTag>>( es.submit(new Callable<List<SaploTag>>() {
			public List<SaploTag> call() throws SaploClientException {
				return tags(saploText, wait, skipCategorization);
			}
		}));
	}

	/**
	 * Get all entity tags that exist in the text. 
	 * Categorized as person, organization, location, url, unknown.
	 * 
	 * @param saploText - the text to extract the {@link SaploTag}s from
	 * @return tagList - a {@link List} containing all the tags extracted
	 * 
	 * @throws SaploClientException 
	 */
	public List<SaploTag> tags(SaploText saploText) throws SaploClientException {
		return tags(saploText, ClientUtil.NULL_INT, false);
	}

	/**
	 * Asynchronously get all entity tags that exist in the text.
	 *
	 * Here is an example usage:
	 * <pre>
	 *	SaploText myText = new SaploText(myCollection, myTextBody);
	 *	SaploFuture<List<SaploTag>> future = textMgr.tagsAsync(myText;
	 *
	 *	// do some other stuff here
	 *
	 *	List<SaploTag> tags;
	 *	try {
	 *		tags = future.get(3, TimeUnit.SECONDS);
	 *
	 *		// do whatever you want with the <code>tags</code>
	 *
	 *	} catch (InterruptedException e) {
	 *		e.printStackTrace();
	 *	} catch (ExecutionException e) {
	 *		e.printStackTrace();
	 *	} catch (TimeoutException e) {
	 *		future.cancel(false);
	 *		e.printStackTrace();
	 *	}
	 *
	 * </pre>
	 * 
	 * @param text - the text to extract the {@link SaploTag}s from
	 * @return {@link SaploFuture}<{@link List}<{@link SaploTag}>> containing all the tags extracted
	 */
	public SaploFuture<List<SaploTag>> tagsAsync(final SaploText saploText) {
		return new SaploFuture<List<SaploTag>>( es.submit(new Callable<List<SaploTag>>() {
			public List<SaploTag> call() throws SaploClientException {
				return tags(saploText);
			}
		}));
	}

	/**
	 * Retrieve tags by just providing collectionId and textId.
	 * 
	 * @param collectionId
	 * @param textId
	 * @return list
	 * 
	 * @throws SaploClientException
	 */
	public List<SaploTag> tags(int collectionId, int textId) throws SaploClientException {
		SaploCollection col = new SaploCollection();
		col.setId(collectionId);

		SaploText text = new SaploText();
		text.setCollection(col);
		text.setId(textId);

		return tags(text); 
	}

	/**
	 * Search a collection/collections for related texts to a given text.
	 *  
	 * @param saploText - the {@link SaploText} to compare to
	 * @param relatedBy - How the texts should be related.
	 * {@link RelatedBy#context} - search for texts based on the same semantic meaning.
	 * {@link RelatedBy#statistic} - search for texts based on statistical relations. 
	 * E.g. number of words, similar words, common words etc. 
	 * @param collectionScope - Search the given collections to find related texts.
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @param limit - the maximum number of related texts in the result. 
	 * There is no guarantee that the number of results will be equal the limit. Max 50.
	 * @param minThreshold - the minimum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @param maxThreshold - the maximum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @return relatedTextsList - a {@link List} containing related texts to the given text
	 * 
	 * @throws SaploClientException 
	 */
	public void relatedTexts(SaploText saploText, RelatedBy relatedBy, 
			SaploCollection[] collectionScope, int wait, int limit, 
			double minThreshold, double maxThreshold) throws SaploClientException {

		verifyCollection(saploText);
		verifyId(saploText);

		List<SaploText> relatedTextsList = new ArrayList<SaploText>();

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			if(saploText.getId() > 0)
				params.put("text_id", saploText.getId());
			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());
			if(relatedBy != null)
				params.put("related_by", relatedBy);

			JSONArray collectionIds = new JSONArray();
			if(collectionScope != null && collectionScope.length > 0) {
				for(int i = 0; i < collectionScope.length; i++) {
					collectionIds.put(collectionScope[i].getId());
				}
			} else {
				collectionIds.put(saploText.getCollection().getId());
			}
			params.put("collection_scope", collectionIds);

			if(wait >= 0)
				params.put("wait", wait);
			if(limit > 0)
				params.put("limit", limit);
			if(minThreshold >= 0 && minThreshold <= 1)
				params.put("min_threshold", minThreshold);
			if(maxThreshold >= 0 && minThreshold <= 1)
				params.put("max_threshold", maxThreshold);
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}


		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.relatedTexts", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawResult = (JSONObject)client.parseResponse(response);

		try {
			JSONArray texts = rawResult.getJSONArray("related_texts");
			for(int i = 0; i < texts.length(); i++) {
				JSONObject textJson = texts.getJSONObject(i);
				SaploText relText = SaploText.convertFromJSONToText(textJson);
				relText.setRelatedToText(saploText);
				relatedTextsList.add(relText);
			}
		} catch(JSONException je) {
			throw new SaploClientException(CODE_MALFORMED_RESPONSE, je);
		}

		saploText.setRelatedTexts(relatedTextsList);
		//		return relatedTextsList;
	}

	/**
	 * Asynchronously search a collection/collections for related texts to a given text.
	 * 
	 * Here is an example usage:
	 * <pre>
	 *	SaploText myText = new SaploText(myCollection, myTextBody);
	 *	SaploFuture<Boolean> future = textMgr.relatedTextsAsync(myText, relatedBy, colScope, 10, 10, 0, 1);
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
	 *		List<SaploText> relatedTexts = myText.getRelatedTexts();
	 *
	 *		// do some other operations as you prefer
	 *	} else {
	 *		// something went wrong
	 *	}
	 * </pre>
	 * 
	 * @param saploText - the {@link SaploText} to compare to
	 * @param relatedBy - How the texts should be related.
	 * {@link RelatedBy#context} - search for texts based on the same semantic meaning.
	 * {@link RelatedBy#statistic} - search for texts based on statistical relations. 
	 * E.g. number of words, similar words, common words etc. 
	 * @param collectionScope - Search the given collections to find related texts.
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @param limit - the maximum number of related texts in the result. 
	 * There is no guarantee that the number of results will be equal the limit. Max 50.
	 * @param minThreshold - the minimum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @param maxThreshold - the maximum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @return SaploFuture<relatedTextsList> - a {@link List} containing related texts to the given text
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> relatedTextsAsync(final SaploText saploText, final RelatedBy relatedBy, 
			final SaploCollection[] collectionScope, final int wait, final int limit, 
			final double minThreshold, final double maxThreshold) {
		return new SaploFuture<Boolean>( es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				relatedTexts(saploText, relatedBy, collectionScope, wait, limit, minThreshold, maxThreshold);
				return true;
			}
		}));
	}

	/**
	 * Search the current collection for related texts to a given text
	 * with default values
	 * 
	 * @param saploText - the {@link SaploText} object to compare to
	 * @return relatedTextsList - a {@link List} containing related texts to the given text
	 * 
	 * @throws SaploClientException 
	 */
	public void relatedTexts(SaploText saploText) throws SaploClientException {
		relatedTexts(saploText, null, null, -1, -1, -1, -1);
	}

	/**
	 * Asynchronously search a collection/collections for related texts to a given 
	 * text with default values. <br>
	 * For an example usage, see {@link #relatedTextsAsync(SaploText, 
	 * RelatedBy, SaploCollection[], int, int, double, double)}
	 * 
	 * @param saploText - the {@link SaploText} to compare to
	 * @return SaploFuture<relatedTextsList> - a {@link List} containing related texts to the given text
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> relatedTextsAsync(final SaploText saploText) {
		return new SaploFuture<Boolean>( es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				relatedTexts(saploText);
				return true;
			}
		}));
	}

	/**
	 * Search for groups that are related to a given text.
	 *  
	 * @param saploText - the {@link SaploText} object to compare to
	 * @param groupScope - the {@link SaploGroup}s the text should be compared to
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @param minThreshold - the minimum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @param maxThreshold - the maximum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @param limit - set max number of results.
	 * @return relatedGroupsList - a {@link List} containing related texts to the given group(s)
	 * 
	 * @throws SaploClientException 
	 */
	public void relatedGroups(SaploText saploText, 
			SaploGroup[] groupScope, int wait, double minThreshold, 
			double maxThreshold, int limit) throws SaploClientException {

		verifyCollection(saploText);
		verifyId(saploText);

		List<SaploGroup> relatedGroupsList = new ArrayList<SaploGroup>();

		JSONObject params = new JSONObject();
		try {
			params.put("collection_id", saploText.getCollection().getId());
			if(saploText.getId() > 0)
				params.put("text_id", saploText.getId());
			if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
				params.put("ext_text_id", saploText.getExtId());

			if(groupScope != null && groupScope.length > 0) {
				JSONArray groupIds = new JSONArray();
				for(int i = 0; i < groupScope.length; i++) {
					groupIds.put(groupScope[i].getId());
				}
				params.put("group_scope", groupIds);
			}

			if(wait >= 0)
				params.put("wait", wait);
			if(minThreshold >= 0 && minThreshold <= 1)
				params.put("min_threshold", minThreshold);
			if(maxThreshold >= 0 && minThreshold <= 1)
				params.put("max_threshold", maxThreshold);
			if(limit > 0)
				params.put("limit", limit);
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.relatedGroups", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawResult = (JSONObject)client.parseResponse(response);

		try {
			JSONArray groups = rawResult.getJSONArray("related_groups");
			for(int i = 0; i < groups.length(); i++) {
				JSONObject groupJson = groups.getJSONObject(i);
				SaploGroup relGroup = SaploGroup.convertFromJSONToGroup(groupJson);
				relGroup.setRelatedToText(saploText);
				relatedGroupsList.add(relGroup);
			}
		} catch(JSONException je) {
			throw new SaploClientException(CODE_MALFORMED_RESPONSE, je);
		}

		saploText.setRelatedGroups(relatedGroupsList);
		//		return relatedGroupsList;
	}

	/**
	 * Asynchronously search for groups that are related to a given text.
	 * <br>
	 * For an example usage, see {@link #relatedTextsAsync(SaploText, 
	 * RelatedBy, SaploCollection[], int, int, double, double)}
	 *  
	 * @param saploText - the {@link SaploText} object to compare to
	 * @param groupScope - the {@link SaploGroup}s the text should be compared to
	 * @param wait - maximum time to wait for the result to be calculated.
	 * @param minThreshold - the minimum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @param maxThreshold - the maximum similarity threshold, between 0 and 1 (1 = 100% similar)
	 * @param limit - set max number of results.
	 * @return SaploFuture<relatedGroupsList> - a {@link List} containing related texts to the given group(s)
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> relatedGroupsAsync(final SaploText saploText, 
			final SaploGroup[] groupScope, final int wait, final double minThreshold, 
			final double maxThreshold, final int limit) {
		return new SaploFuture<Boolean>( es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				relatedGroups(saploText, groupScope, wait, minThreshold, maxThreshold, limit);
				return true;
			}
		}));
	}

	/**
	 * Search for groups that are related to a given text, with default values.
	 * 
	 * @param saploText - the {@link SaploText} object to compare to
	 * @return relatedGroupsList - a {@link List} containing related texts to the given group(s)
	 * 
	 * @throws SaploClientException 
	 */
	public void relatedGroups(SaploText saploText) throws SaploClientException {
		relatedGroups(saploText, null, -1, -1, -1, -1);
	}

	/**
	 * Asynchronously search for groups that are related to a given text.
	 * <br>
	 * For an example usage, see {@link #relatedTextsAsync(SaploText, 
	 * RelatedBy, SaploCollection[], int, int, double, double)}
	 *  
	 * @param saploText - the {@link SaploText} object to compare to
	 * @return SaploFuture<relatedGroupsList> - a {@link List} containing related texts to the given group(s)
	 * @throws SaploClientException
	 */
	public SaploFuture<Boolean> relatedGroupsAsync(final SaploText saploText) {
		return new SaploFuture<Boolean>( es.submit(new Callable<Boolean>() {
			public Boolean call() throws SaploClientException {
				relatedGroups(saploText);
				return true;
			}
		}));
	}

	/**
	 * Give feedback by adding a tag to a text.
	 * 
	 * @param saploText
	 * @param saploTag
	 * @return tag - newly added tag returned by the API
	 * 
	 * @throws SaploClientException
	 */
	public SaploTag addTag(SaploText saploText, SaploTag saploTag) throws SaploClientException {
		verifyCollection(saploText);
		verifyId(saploText);

		JSONObject params = new JSONObject();
		try {
		params.put("collection_id", saploText.getCollection().getId());
		if(saploText.getId() > 0)
			params.put("text_id", saploText.getId());
		if(!ClientUtil.NULL_STRING.equals(saploText.getExtId()))
			params.put("ext_text_id", saploText.getExtId());

		if(!ClientUtil.NULL_STRING.equals(saploTag.getTagWord())) {
			params.put("tag", saploTag.getTagWord());
		}
		params.put("category", saploTag.getCategory().toString().toLowerCase());
		params.put("relevance", saploTag.getRelevance());
		} catch(JSONException je) {
			throw new SaploClientException(CODE_JSON_EXCEPTION, je);
		}

		JSONRPCRequestObject request = new JSONRPCRequestObject(client.getNextId(), "text.addTag", params);

		JSONRPCResponseObject response = client.sendAndReceive(request);

		JSONObject rawResult = (JSONObject)client.parseResponse(response);

		return SaploTag.convertFromJSONToTag(rawResult);

	}

	/**
	 * Create a new text for a given collection id with a given text_id
	 * 
	 * @param collectionId
	 * @param textId
	 * @return
	 */
	public static SaploText getTextObject(int collectionId, int textId) {
		SaploCollection col = new SaploCollection();
		col.setId(collectionId);

		SaploText text = new SaploText();
		text.setCollection(col);
		text.setId(textId);

		return text;
	}

	/*
	 * ensure the given text has collection_id
	 */
	private static void verifyCollection(SaploText saploText) throws SaploClientException {
		if(saploText.getCollection() == null || saploText.getCollection().getId() <= 0)
			throw new SaploClientException(MSG_CLIENT_FIELD, CODE_CLIENT_FIELD, "text.collection", "text.collection.id");
	}

	/*
	 * ensure the given text has at least one of the text ids
	 */
	private static void verifyId(SaploText saploText) throws SaploClientException {
		if(saploText.getId() <= 0 && ClientUtil.NULL_STRING.equals(saploText.getExtId()))
			throw new SaploClientException(MSG_CLIENT_FIELD, CODE_CLIENT_FIELD, "text.id OR text.ext_id");
	}

}
