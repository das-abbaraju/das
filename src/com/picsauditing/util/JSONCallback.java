package com.picsauditing.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONCallback {
	final public String CALLBACK = "callback";
	final public String STATUS = "status";
	final public String TOTALPROPERTY = "rowCount";
	
	private Integer recordCount = 0;
	private String callbackFunction = null;
	private JSONArray result;
	private String status = "success";
	
	public JSONCallback(String status) {
		this.status = status;
	}
	
	public JSONCallback(JSONArray result) {
		this.result = result;
		recordCount = result.size();
	}
	
	public String toString() {
		JSONObject json = new JSONObject();
		json.put(TOTALPROPERTY, recordCount);
		json.put("root", result);
		json.put(STATUS, status);
		if (callbackFunction == null)
			return json.toString();
		
		return callbackFunction + "(" + json.toString() + ");";
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setCallbackFunction() {
		setCallbackFunction(CALLBACK);
	}
	
	public void setCallbackFunction(String callback) {
		callbackFunction = callback;
	}
}
