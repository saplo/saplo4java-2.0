/**
 * 
 */
package com.saplo.api.client.entity;

import org.json.JSONObject;

import com.saplo.api.client.util.ClientUtil;

/**
 * @author progre55
 *
 */
public class SaploCollection {

	public enum Language {en, sv}
	
	public enum Permission {read, write, none}
	
	private int id = ClientUtil.NULL_INT;
	private String name = ClientUtil.NULL_STRING; // required
	private String description = ClientUtil.NULL_STRING;;
	private Language language; // required
	private Permission permission;
	private int nextId = ClientUtil.NULL_INT;
	
	/**
	 * An empty constructor. 
	 * Make sure you set the {@link #setName(String)} 
	 * and {@link #setLanguage(Language)} parameters, as they are required.
	 *  
	 */
	public SaploCollection() {
		this.language = null;
	}
	
	/**
	 * A constructor with the collection Id
	 * 
	 * @param id
	 */
	public SaploCollection(int id) {
		this.id = id;
	}
	
	/**
	 * A constructor with required parameters
	 * 
	 * @param collectionName
	 * @param lang
	 */
	public SaploCollection(String collectionName, Language lang) {
		this.name = collectionName;
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
	 * @return the permission
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	
	/**
	 * @return the nextId
	 */
	public int getNextId() {
		return nextId;
	}

	/**
	 * @param nextId the nextId to set
	 */
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}

	/**
	 * Update a given {@link SaploCollection} object with the given {@link JSONObject} object
	 * 
	 * @param json - the {@link JSONObject} to parse
	 * @param saploCollection - the {@link SaploCollection} object to write the conversion results to
	 */
	public static void convertFromJSONToCollection(JSONObject json, SaploCollection saploCollection) {
		
		if(json.has("collection_id"))
			saploCollection.setId(json.optInt("collection_id"));
		if(json.has("name"))
			saploCollection.setName(json.optString("name"));
		if(json.has("language"))
			saploCollection.setLanguage(SaploCollection.Language.valueOf(json.optString("language")));
		if(json.has("description"))
			saploCollection.setDescription(json.optString("description"));
		if(json.has("permission"))
			saploCollection.setPermission(SaploCollection.Permission.valueOf(json.optString("permission")));
		if(json.has("next_id"))
			saploCollection.setNextId(json.optInt("next_id"));
	}
	
	/**
	 * Convert a given {@link JSONObject} object to a {@link SaploCollection} object
	 * 
	 * @param json - the {@link JSONObject} to convert
	 * @return collection - the {@link SaploCollection} representation of the json object
	 */
	public static SaploCollection convertFromJSONToCollection(JSONObject json)  {
		SaploCollection saploCollection = new SaploCollection();
		convertFromJSONToCollection(json, saploCollection);
		return saploCollection;
	}
}
