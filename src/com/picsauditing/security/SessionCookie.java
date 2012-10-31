package com.picsauditing.security;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.picsauditing.util.Strings;


public class SessionCookie {
	private static ObjectMapper mapper = new ObjectMapper();

	private int userID;
	private Date cookieCreationTime;
	private Map<String, Object> embeddedData = new HashMap<String, Object>();
	private String validationHash;

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
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
			StringBuffer thisAsString = new StringBuffer().append(userID).append('|')
					.append(cookieCreationTime.getTime()).append('|');
			if (embeddedData != null && embeddedData.size() > 0) {
				thisAsString.append(mapper.writeValueAsString(embeddedData));
			}
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
