package com.picsauditing.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.util.Strings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SessionCookie {
	private static ObjectMapper mapper = new ObjectMapper();

	private int userID;
	private int appUserID;
	private int profileID;
	private Date cookieCreationTime;
	private Map<String, Object> embeddedData = new HashMap<String, Object>();
	private String validationHash;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getAppUserID() {
		return appUserID;
	}

	public void setAppUserID(int appUserID) {
		this.appUserID = appUserID;
	}

	public int getProfileID() {
		return profileID;
	}

	public void setProfileID(int profileID) {
		this.profileID = profileID;
	}

	public Date getCookieCreationTime() {
		return cookieCreationTime;
	}

	public void setCookieCreationTime(Date cookieCreationTime) {
		this.cookieCreationTime = cookieCreationTime;
	}

	public Map<String, Object> getEmbeddedData() {
		return embeddedData;
	}

	public void setEmbeddedData(Map<String, Object> embeddedData) {
		this.embeddedData = embeddedData;
	}

	@SuppressWarnings("unchecked")
	public void setEmbeddedData(String embeddedJsonData) {
		try {
			if (!Strings.isEmpty(embeddedJsonData)) {
				this.embeddedData = mapper.readValue(embeddedJsonData, Map.class);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putData(String dataKey, Object data) {
		if (embeddedData != null) {
			embeddedData.put(dataKey, data);
		}
	}

	public Object getData(String dataKey) {
		if (embeddedData == null) {
			return null;
		}
		return embeddedData.get(dataKey);
	}

	public String getValidationHash() {
		return validationHash;
	}

	public void setValidationHash(String validationHash) {
		this.validationHash = validationHash;
	}

	public String toString() {
		try {
			StringBuffer thisAsString = new StringBuffer()
					.append(userID).append('|')
					.append(cookieCreationTime.getTime()).append('|');
			if (embeddedData != null && embeddedData.size() > 0) {
				thisAsString.append(mapper.writeValueAsString(embeddedData));
			}

			thisAsString.append('|').append(appUserID);

			if (validationHash != null) {
				thisAsString.append('|').append(validationHash);
			}
			return thisAsString.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
