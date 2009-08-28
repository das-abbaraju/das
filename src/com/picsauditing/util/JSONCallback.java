package com.picsauditing.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONCallback {
	final public String STATUS = "status";
	final public String TOTALPROPERTY = "rowCount";
	final public String ROOT = "root";
	
	private Integer recordCount = 0;
	private String callbackFunction = null;
	private JSONArray result;
	private boolean status = true;
	
	public JSONCallback(boolean status) {
		this.status = status;
	}
	
	public JSONCallback(JSONArray result) {
		this.result = result;
		recordCount = result.size();
	}
	
	public String toString() {
		JSONObject json = new JSONObject();
		json.put(STATUS, status);
		json.put(TOTALPROPERTY, recordCount);
		json.put(ROOT, result);
		if (callbackFunction == null)
			return json.toString();
		
		return callbackFunction + "(" + json.toString() + ");";
	}
	
	public void setCallbackFunction(String callbackFunction) {
		this.callbackFunction = callbackFunction;
	}
	
}
