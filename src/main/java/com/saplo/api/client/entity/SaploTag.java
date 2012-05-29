/**
 * 
 */
package com.saplo.api.client.entity;

import org.json.JSONObject;

import com.saplo.api.client.util.ClientUtil;

/**
 * A Saplo SaploTag representation
 * 
 * @author progre55
 */
public class SaploTag {
	
	public enum TagCategory {
		PERSON, ORGANIZATION, LOCATION, UNKNOWN, URL
	}
	
	private String tagWord;
	private TagCategory category;
	private double relevance;
	
	/**
	 * An empty constructor
	 */
	public SaploTag() {
		tagWord = ClientUtil.NULL_STRING;
		category = TagCategory.UNKNOWN;
		relevance = 0;
	}

	/**
	 * @return the tagWord
	 */
	public String getTagWord() {
		return tagWord;
	}

	/**
	 * @param tagWord the tagWord to set
	 */
	public void setTagWord(String tagWord) {
		this.tagWord = tagWord;
	}

	/**
	 * @return the category
	 */
	public TagCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(TagCategory category) {
		this.category = category;
	}

	/**
	 * @return the relevance
	 */
	public double getRelevance() {
		return relevance;
	}

	/**
	 * @param relevance the relevance to set
	 */
	public void setRelevance(double relevance) {
		this.relevance = relevance;
	}
	
	/**
	 * Convert a given {@link JSONObject} object to a {@link SaploTag} object
	 * 
	 * @param json - the {@link JSONObject} to convert
	 * @return tag - the {@link SaploTag} representation of the json object
	 */
	public static SaploTag convertFromJSONToTag(JSONObject json) {
		SaploTag saploTag = new SaploTag();
		
		if(json.has("tag"))
			saploTag.setTagWord(json.optString("tag"));
		if(json.has("category"))
			saploTag.setCategory(SaploTag.TagCategory.valueOf((json.optString("category").toUpperCase())));
		if(json.has("relevance"))
			saploTag.setRelevance(json.optDouble("relevance"));

		return saploTag;
	}
}
