package com.picsauditing.jpa.entities;

import org.json.simple.JSONObject;

public interface Jsonable {
	public JSONObject getJSON(boolean full);

	public void setJSON(JSONObject o);
}
