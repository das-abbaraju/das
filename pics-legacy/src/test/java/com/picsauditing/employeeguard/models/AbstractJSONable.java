package com.picsauditing.employeeguard.models;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class AbstractJSONable {

	public JSONObject toJSON() throws ParseException {
		return (JSONObject) new JSONParser().parse(new Gson().toJson(this));
	}

}
