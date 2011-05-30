/**
 * 
 */
package com.saplo.api.client.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Information about the user account
 * 
 * @author progre55
 */
public class SaploAccount {
	
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	private int id;
	private Date expirationDate;
	private ApiCalls apiCalls;
	private int collectionsLimit;
	private int collectionsLeft;
	private int groupLimit;
	private int groupLeft;
	
	public SaploAccount() {
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
	 * @return the expirationDate
	 */
	public Date getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @return the apiCalls
	 */
	public ApiCalls getApiCalls() {
		return apiCalls;
	}

	/**
	 * @param apiCalls the apiCalls to set
	 */
	public void setApiCalls(ApiCalls apiCalls) {
		this.apiCalls = apiCalls;
	}

	/**
	 * @return the collectionsLimit
	 */
	public int getCollectionsLimit() {
		return collectionsLimit;
	}

	/**
	 * @param collectionsLimit the collectionsLimit to set
	 */
	public void setCollectionsLimit(int collectionsLimit) {
		this.collectionsLimit = collectionsLimit;
	}

	/**
	 * @return the collectionsLeft
	 */
	public int getCollectionsLeft() {
		return collectionsLeft;
	}

	/**
	 * @param collectionsLeft the collectionsLeft to set
	 */
	public void setCollectionsLeft(int collectionsLeft) {
		this.collectionsLeft = collectionsLeft;
	}

	/**
	 * @return the groupLimit
	 */
	public int getGroupLimit() {
		return groupLimit;
	}

	/**
	 * @param groupLimit the groupLimit to set
	 */
	public void setGroupLimit(int groupLimit) {
		this.groupLimit = groupLimit;
	}

	/**
	 * @return the groupLeft
	 */
	public int getGroupLeft() {
		return groupLeft;
	}

	/**
	 * @param groupLeft the groupLeft to set
	 */
	public void setGroupLeft(int groupLeft) {
		this.groupLeft = groupLeft;
	}

	/**
	 * Update a given {@link SaploAccount} object with the given {@link JSONObject} object
	 * 
	 * @param json - the {@link JSONObject} to parse
	 * @param saploAccount - the {@link SaploAccount} object to write the convertion results to
	 * 
	 * @throws JSONException
	 */
	public static void convertFromJSONToAccount(JSONObject json, SaploAccount saploAccount) throws JSONException {
		
		if(json.has("account_id"))
			saploAccount.setId(json.getInt("account_id"));
		if(json.has("expiration_date"))
			try {
				saploAccount.setExpirationDate(sf.parse(json.getString("expiration_date")));
			} catch (ParseException e) {
				//
			}
		if(json.has("api_calls")) {
			JSONObject apiCallsJson = json.getJSONObject("api_calls");
			ApiCalls apiCalls = new ApiCalls();
			if(apiCallsJson.has("left_month"))
				apiCalls.setLeftMonth(apiCallsJson.getInt("left_month"));
			if(apiCallsJson.has("left_hour"))
				apiCalls.setLeftHour(apiCallsJson.getInt("left_hour"));
			if(apiCallsJson.has("limit_month"))
				apiCalls.setLimitMonth(apiCallsJson.getInt("limit_month"));
			if(apiCallsJson.has("limit_hour"))
				apiCalls.setLimitHour(apiCallsJson.getInt("limit_hour"));
			if(apiCallsJson.has("reset_hour"))
				try {
					apiCalls.setResetHour(sf.parse(json.getString("reset_hour")));
				} catch (ParseException e) {
					//
				}
				if(apiCallsJson.has("reset_month"))
					try {
						apiCalls.setResetMonth(sf.parse(json.getString("reset_month")));
					} catch (ParseException e) {
						//
					}
				
			saploAccount.setApiCalls(apiCalls);
		}
		if(json.has("collections")) {
			if(json.getJSONObject("collections").has("limit"))
				saploAccount.setCollectionsLimit(json.getJSONObject("collections").getInt("limit"));
			if(json.getJSONObject("collections").has("left"))
				saploAccount.setCollectionsLeft(json.getJSONObject("collections").getInt("left"));
		}
		if(json.has("groups")) {
			if(json.getJSONObject("groups").has("limit"))
				saploAccount.setGroupLimit(json.getJSONObject("groups").getInt("limit"));
			if(json.getJSONObject("groups").has("left"))
				saploAccount.setGroupLeft(json.getJSONObject("groups").getInt("left"));
		}
	}
	
	/**
	 * Convert a given {@link JSONObject} object to a {@link SaploAccount} object
	 * 
	 * @param json - the {@link JSONObject} to convert
	 * @return account - the {@link SaploAccount} representation of the json object
	 * 
	 * @throws JSONException
	 */
	public static SaploAccount convertFromJSONToAccount(JSONObject json) throws JSONException {
		SaploAccount saploAccount = new SaploAccount();
		convertFromJSONToAccount(json, saploAccount);
		return saploAccount;
	}

	
	/**
	 * A class to represent ApiCalls and hold info about the account API calls
	 * 
	 * @author progre55
	 */
	static class ApiCalls {
		private int limitMonth;
		private int leftMonth;
		private Date resetMonth;

		private int limitHour;
		private int leftHour;
		private Date resetHour;

		public ApiCalls() {
			
		}

		/**
		 * @return the limitMonth
		 */
		public int getLimitMonth() {
			return limitMonth;
		}

		/**
		 * @param limitMonth the limitMonth to set
		 */
		public void setLimitMonth(int limitMonth) {
			this.limitMonth = limitMonth;
		}

		/**
		 * @return the leftMonth
		 */
		public int getLeftMonth() {
			return leftMonth;
		}

		/**
		 * @param leftMonth the leftMonth to set
		 */
		public void setLeftMonth(int leftMonth) {
			this.leftMonth = leftMonth;
		}

		/**
		 * @return the resetMonth
		 */
		public Date getResetMonth() {
			return resetMonth;
		}

		/**
		 * @param resetMonth the resetMonth to set
		 */
		public void setResetMonth(Date resetMonth) {
			this.resetMonth = resetMonth;
		}

		/**
		 * @return the limitHour
		 */
		public int getLimitHour() {
			return limitHour;
		}

		/**
		 * @param limitHour the limitHour to set
		 */
		public void setLimitHour(int limitHour) {
			this.limitHour = limitHour;
		}

		/**
		 * @return the leftHour
		 */
		public int getLeftHour() {
			return leftHour;
		}

		/**
		 * @param leftHour the leftHour to set
		 */
		public void setLeftHour(int leftHour) {
			this.leftHour = leftHour;
		}

		/**
		 * @return the resetHour
		 */
		public Date getResetHour() {
			return resetHour;
		}

		/**
		 * @param resetHour the resetHour to set
		 */
		public void setResetHour(Date resetHour) {
			this.resetHour = resetHour;
		}
	}
}
