package com.picsauditing.util;

import java.util.List;

import org.json.simple.JSONArray;

import com.picsauditing.jpa.entities.Jsonable;

public class JSONUtilities {

	@SuppressWarnings("unchecked")
	static public JSONArray convertFromList(List<Jsonable> list) {
		JSONArray jsonArray = new JSONArray();
		for (Jsonable obj : list)
			jsonArray.add(obj.getJSON(false));

		return jsonArray;
	}
}
