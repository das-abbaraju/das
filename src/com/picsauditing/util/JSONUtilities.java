package com.picsauditing.util;

import java.util.List;

import org.json.simple.JSONArray;

import com.picsauditing.jpa.entities.JSONable;

public class JSONUtilities {

	@SuppressWarnings("unchecked")
	static public JSONArray convertFromList(List<JSONable> list) {
		JSONArray jsonArray = new JSONArray();
		for (JSONable obj : list)
			jsonArray.add(obj.toJSON(false));

		return jsonArray;
	}
}
