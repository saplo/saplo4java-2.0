/**
 * 
 */
package com.saplo.api.client.entity;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONObject;

import com.saplo.api.client.util.ThreadSafeSimpleDateFormat;

/**
 * Information about the user account
 * 
 * @author progre55
 */
public class SaploAccount {
	
	private static ThreadSafeSimpleDateFormat sf = new ThreadSafeSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
	protected void setId(int id) {
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
	protected void setExpirationDate(Date expirationDate) {
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
	protected void setApiCalls(ApiCalls apiCalls) {
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
	protected void setCollectionsLimit(int collectionsLimit) {
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
	protected void setCollectionsLeft(int collectionsLeft) {
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
	protected void setGroupLimit(int groupLimit) {
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
	protected void setGroupLeft(int groupLeft) {
		this.groupLeft = groupLeft;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SaploAccount ["
				+ "id=" + id 
				+ ", collectionsLeft=" + collectionsLeft
				+ ", collectionsLimit=" + collectionsLimit
				+ ", " + (expirationDate != null ? "expirationDate=" 
						+ sf.format(expirationDate) + ", " : "") + "groupLeft=" + groupLeft
				+ ", groupLimit=" + groupLimit 
				+ ", " + (apiCalls != null ? "apiCalls={" + apiCalls.toString() + "}" : "apiCalls=null") + "]";
	}

	/**
	 * Update a given {@link SaploAccount} object with the given {@link JSONObject} object
	 * 
	 * @param json - the {@link JSONObject} to parse
	 * @param saploAccount - the {@link SaploAccount} object to write the convertion results to
	 */
	public static void convertFromJSONToAccount(JSONObject json, SaploAccount saploAccount) {
		
		if(json.has("account_id"))
			saploAccount.setId(json.optInt("account_id"));
		if(json.has("expiration_date"))
			try {
				saploAccount.setExpirationDate(sf.parse(json.optString("expiration_date")));
			} catch (ParseException e) {
				//
			}
		if(json.has("api_calls")) {
			JSONObject apiCallsJson = json.optJSONObject("api_calls");
			ApiCalls apiCalls = new ApiCalls();
			if(apiCallsJson.has("left_month"))
				apiCalls.setLeftMonth(apiCallsJson.optInt("left_month"));
			if(apiCallsJson.has("left_hour"))
				apiCalls.setLeftHour(apiCallsJson.optInt("left_hour"));
			if(apiCallsJson.has("limit_month"))
				apiCalls.setLimitMonth(apiCallsJson.optInt("limit_month"));
			if(apiCallsJson.has("limit_hour"))
				apiCalls.setLimitHour(apiCallsJson.optInt("limit_hour"));
			if(apiCallsJson.has("reset_hour"))
				try {
					apiCalls.setResetHour(sf.parse(apiCallsJson.optString("reset_hour")));
				} catch (ParseException e) {
					//
				}
				if(apiCallsJson.has("reset_month"))
					try {
						apiCalls.setResetMonth(sf.parse(apiCallsJson.optString("reset_month")));
					} catch (ParseException e) {
						//
					}
				
			saploAccount.setApiCalls(apiCalls);
		}
		if(json.has("collections")) {
			if(json.optJSONObject("collections").has("limit"))
				saploAccount.setCollectionsLimit(json.optJSONObject("collections").optInt("limit"));
			if(json.optJSONObject("collections").has("left"))
				saploAccount.setCollectionsLeft(json.optJSONObject("collections").optInt("left"));
		}
		if(json.has("groups")) {
			if(json.optJSONObject("groups").has("limit"))
				saploAccount.setGroupLimit(json.optJSONObject("groups").optInt("limit"));
			if(json.optJSONObject("groups").has("left"))
				saploAccount.setGroupLeft(json.optJSONObject("groups").optInt("left"));
		}
	}
	
	/**
	 * Convert a given {@link JSONObject} object to a {@link SaploAccount} object
	 * 
	 * @param json - the {@link JSONObject} to convert
	 * @return account - the {@link SaploAccount} representation of the json object
	 */
	public static SaploAccount convertFromJSONToAccount(JSONObject json) {
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
		protected void setLimitMonth(int limitMonth) {
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
		protected void setLeftMonth(int leftMonth) {
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
		protected void setResetMonth(Date resetMonth) {
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
		protected void setLimitHour(int limitHour) {
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
		protected void setLeftHour(int leftHour) {
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
		protected void setResetHour(Date resetHour) {
			this.resetHour = resetHour;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ApiCalls [leftHour="
					+ leftHour
					+ ", leftMonth="
					+ leftMonth
					+ ", limitHour="
					+ limitHour
					+ ", limitMonth="
					+ limitMonth
					+ ", "
					+ (resetHour != null ? "resetHour=" + sf.format(resetHour) + ", " : "")
					+ (resetMonth != null ? "resetMonth=" + sf.format(resetMonth) : "")
					+ "]";
		}
		
	}
}
