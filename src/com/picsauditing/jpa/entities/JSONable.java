package com.picsauditing.jpa.entities;

import org.json.simple.JSONObject;

public interface JSONable {
	public JSONObject toJSON(boolean full);

	public void fromJSON(JSONObject o);
}
