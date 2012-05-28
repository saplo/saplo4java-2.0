/**
 * 
 */
package com.saplo.api.client.entity;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.saplo.api.client.entity.SaploCollection.Language;
import com.saplo.api.client.util.ClientUtil;
import com.saplo.api.client.util.ThreadSafeSimpleDateFormat;

/**
 * @author progre55
 *
 */
public class SaploGroup {

	private static ThreadSafeSimpleDateFormat sf = new ThreadSafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private int id = ClientUtil.NULL_INT;
	private String name = ClientUtil.NULL_STRING; // required
	private String description = ClientUtil.NULL_STRING;
	private Language language = null; // required
	private Date dateCreated;
	private Date dateUpdated;
	private SaploText relatedToText;
	private SaploGroup relatedToGroup;
	private double relatedRelevance;
	
	private List<SaploGroup> relatedGroups;
	private List<SaploText> relatedTexts;

	/**
	 * An empty constructor.
	 * Don't forget to set {@link #name} and {@link #language} 
	 * as they are required.
	 */
	public SaploGroup() {	
	}
	
	/**
	 * A constructor with required fields
	 * @param name - the group name to set
	 * @param lang - of type {@link SaploCollection.Language}
	 */
	public SaploGroup(String name, Language lang) {
		this.name = name;
		this.language = lang;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the language
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the dateUpdated
	 */
	public Date getDateUpdated() {
		return dateUpdated;
	}

	/**
	 * @param dateUpdated the dateUpdated to set
	 */
	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
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
	 * @return the relatedGroups
	 */
	public List<SaploGroup> getRelatedGroups() {
		return relatedGroups;
	}

	/**
	 * @param relatedGroups the relatedGroups to set
	 */
	public void setRelatedGroups(List<SaploGroup> relatedGroups) {
		this.relatedGroups = relatedGroups;
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
	 * Update a given {@link SaploGroup} object with the given {@link JSONObject} object
	 * 
	 * @param json - the {@link JSONObject} to parse
	 * @param saploGroup - the {@link SaploGroup} object to write the convertion results to
	 */
	public static void convertFromJSONToGroup(JSONObject json, SaploGroup saploGroup) {

		saploGroup.setId(json.optInt("group_id"));
		saploGroup.setName(json.optString("name"));
		saploGroup.setDescription(json.optString("description"));
		if(json.has("language"))
			saploGroup.setLanguage(SaploCollection.Language.valueOf(json.optString("language")));
		try {
			if(json.has("date_created"))
				saploGroup.setDateCreated(sf.parse(json.optString("date_created")));
			if(json.has("date_updated"))
				saploGroup.setDateUpdated(sf.parse(json.optString("date_updated")));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(json.has("relevance"))
			saploGroup.setRelatedRelevance(json.optDouble("relevance"));
	}
	
	/**
	 * Convert a given {@link JSONObject} object to a {@link SaploGroup} object
	 * 
	 * @param json - the {@link JSONObject} to parse
	 * @return group - the {@link SaploGroup} representation of the json object
	 */
	public static SaploGroup convertFromJSONToGroup(JSONObject json) {
		SaploGroup saploGroup = new SaploGroup();
		convertFromJSONToGroup(json, saploGroup);
		return saploGroup;
	}
}
