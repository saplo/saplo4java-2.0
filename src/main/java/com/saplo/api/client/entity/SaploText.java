/**
 * 
 */
package com.saplo.api.client.entity;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.saplo.api.client.util.ClientUtil;
import com.saplo.api.client.util.ThreadSafeSimpleDateFormat;

/**
 * A SaploText entity, the same as Article in SaploAPI v1.
 * 
 * @author progre55
 */
public class SaploText {
	
	/**
	 * {@link #semantic} - Searching for {@link SaploText}s based on the same semantic meaning.
	 * {@link #statistic} - Searching for {@link SaploText}s based on statistical relations 
	 * e.g. number of words, similar words, common words, etc.
	 * {@link #automatic} - The engine determines the best way.
	 *  
	 * @author progre55
	 */
	public enum RelatedBy {
		semantic, statistic, automatic
	}
	
	private static ThreadSafeSimpleDateFormat sf = new ThreadSafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static ThreadSafeSimpleDateFormat sf2 = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final int MAX_HEADLINE_LENGTH = 250;
	public static final int MAX_BODY_LENGTH = 100000;
	
	private int id;
	private SaploCollection saploCollection; // required
	private String headline;
	private String body; // required
	private Date publishDate;
	private URI url;
	private String authors;
	private String extId;
	private boolean force;
	private SaploText relatedToText;
	private SaploGroup relatedToGroup;
	private double relatedRelevance;

	private List<SaploText> relatedTexts;
	private List<SaploGroup> relatedGroups;
	
	/**
	 * An empty constructor. 
	 * Make sure to set the {@link #setCollection(SaploCollection)} 
	 * and {@link #setBody(String)} parameters, as they are required.
	 *  
	 */
	public SaploText() {
		id = ClientUtil.NULL_INT;
		saploCollection = null;
		body = ClientUtil.NULL_STRING;
		headline = ClientUtil.NULL_STRING;
		publishDate = null;
		url = null;
		authors = ClientUtil.NULL_STRING;
		extId = ClientUtil.NULL_STRING;
	}
	
	/**
	 * A constructor with the required parameters.
	 * If the body provided is larger than {@value #MAX_BODY_LENGTH} chars, 
	 * then it is truncated to {@value #MAX_BODY_LENGTH} chars
	 * 
	 * @param saploCollection - the {@link SaploCollection} object for this text 
	 * @param body - the body text of this text :)
	 */
	public SaploText(SaploCollection saploCollection, String body) {
		this.saploCollection = saploCollection;
		setBody(body);
		id = ClientUtil.NULL_INT;
		publishDate = null;
		url = null;
		authors = ClientUtil.NULL_STRING;
		extId = ClientUtil.NULL_STRING;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the saploCollection
	 */
	public SaploCollection getCollection() {
		return saploCollection;
	}

	/**
	 * @param saploCollection the saploCollection to set
	 */
	public void setCollection(SaploCollection saploCollection) {
		this.saploCollection = saploCollection;
	}

	/**
	 * @return the headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * @param headline the text headline to set
	 * If the headline provided is longer than {@value #MAX_HEADLINE_LENGTH} chars,
	 * then it is truncated to {@value #MAX_HEADLINE_LENGTH} chars
	 */
	public void setHeadline(String headline) {
		if(headline != null && headline.length() > MAX_HEADLINE_LENGTH)
			this.headline = headline.substring(0, MAX_HEADLINE_LENGTH);
		else
			this.headline = headline;
	}
	
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the text body to set
	 * If the body provided is larger than {@value #MAX_BODY_LENGTH} chars, 
	 * then it is truncated to {@value #MAX_BODY_LENGTH} chars
	 */
	public void setBody(String body) {
		if(body !=null && body.length() > MAX_BODY_LENGTH)
			this.body = body.substring(0, MAX_BODY_LENGTH);
		else
			this.body = body;
	}

	/**
	 * @return the publishDate
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @param publishDate - string publish date formatted "yyyy-MM-dd HH:mm:ss" 
	 */
	public void setPublishDate(String publishDate) {
		try {
			this.publishDate = sf2.parse(publishDate);
		} catch (ParseException e) {
			// do nothing
		}
	}
	
	/**
	 * @return the url
	 */
	public URI getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(URI url) {
		this.url = url;
	}
	
	/**
	 * @param url - the String url to set
	 */
	public void setUrl(String url) {
		try {
			this.url = new URI(url);
		} catch (URISyntaxException e) {
			// do nothing
		}
	}

	/**
	 * @return the authors
	 */
	public String getAuthors() {
		return authors;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	
	/**
	 * @return the extId
	 */
	public String getExtId() {
		return extId;
	}

	/**
	 * @param extId the extId to set
	 */
	public void setExtId(String extId) {
		this.extId = extId;
	}

	/**
	 * @return the force
	 */
	public boolean isForce() {
		return force;
	}

	/**
	 * @param force the force to set
	 */
	public void setForce(boolean force) {
		this.force = force;
	}

	/**
	 * @return the relatedToText
	 */
	public SaploText getRelatedToText() {
		return relatedToText;
	}

	/**
	 * @param relatedToText the relatedToText to set
	 */
	public void setRelatedToText(SaploText relatedToText) {
		this.relatedToText = relatedToText;
	}

	/**
	 * @return the relatedRelevance
	 */
	public double getRelatedRelevance() {
		return relatedRelevance;
	}

	/**
	 * @param relatedRelevance the relatedRelevance to set
	 */
	public void setRelatedRelevance(double relatedRelevance) {
		this.relatedRelevance = relatedRelevance;
	}
	
	/**
	 * @return the relatedTexts
	 */
	public List<SaploText> getRelatedTexts() {
		return relatedTexts;
	}

	/**
	 * @param relatedTexts the relatedTexts to set
	 */
	public void setRelatedTexts(List<SaploText> relatedTexts) {
		this.relatedTexts = relatedTexts;
	}
	
	/**
	 * @return the relatedGroups
	 */
	public List<SaploGroup> getRelatedGroups() {
		return relatedGroups;
	}
	
	/**
	 * @return the relatedToGroup
	 */
	public SaploGroup getRelatedToGroup() {
		return relatedToGroup;
	}

	/**
	 * @param relatedToGroup the relatedToGroup to set
	 */
	public void setRelatedToGroup(SaploGroup relatedToGroup) {
		this.relatedToGroup = relatedToGroup;
	}

	/**
	 * @param relatedGroups the relatedGroups to set
	 */
	public void setRelatedGroups(List<SaploGroup> relatedGroups) {
		this.relatedGroups = relatedGroups;
	}

	/**
	 * Update a given {@link SaploText} object with the given {@link JSONObject} object
	 * 
	 * @param json - the {@link JSONObject} to parse
	 * @param saploText - the {@link SaploText} object to write the convertion results to
	 */
	public static void convertFromJSONToText(JSONObject json, SaploText saploText) {
		
		saploText.setId(json.optInt("text_id", -1));
		if(json.has("headline"))
			saploText.setHeadline(json.optString("headline"));
		if(json.has("body"))
			saploText.setBody(json.optString("body"));
		if(json.has("publish_date"))
			try {
				saploText.setPublishDate(sf.parse(json.optString("publish_date")));
			} catch (ParseException e) {
				//
			}
		if(json.has("url"))
			try {
				saploText.setUrl(new URI(json.optString("url")));
			} catch (URISyntaxException e) {
				//
			}
		if(json.has("authors"))
			saploText.setAuthors(json.optString("authors"));
		if(json.has("collection_id")) {
			SaploCollection saploCollection = new SaploCollection();
			saploCollection.setId(json.optInt("collection_id"));
			saploText.setCollection(saploCollection);
		}
		if(json.has("ext_text_id"))
			saploText.setExtId(json.optString("ext_text_id"));
		if(json.has("relevance"))
			saploText.setRelatedRelevance(json.optDouble("relevance"));
	}
	
	/**
	 * Convert a given {@link JSONObject} object into a {@link SaploText} object
	 *  
	 * @param json - the {@link JSONObject} to convert
	 * @return text - the {@link SaploText} representation of the json object
	 */
	public static SaploText convertFromJSONToText(JSONObject json) {
		SaploText saploText = new SaploText();
		convertFromJSONToText(json, saploText);
		return saploText;
	}
}
