package com.picsauditing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;

public class JSONUtilities {

	public static final String EMPTY_JSON = "{}";
	public static final String EMPTY_JSON_ARRAY = "[]";

	@SuppressWarnings("unchecked")
	static public JSONArray convertFromList(List<? extends JSONable> list) {
		JSONArray jsonArray = new JSONArray();
		for (JSONable obj : list) {
			jsonArray.add(obj.toJSON(false));
		}

		return jsonArray;
	}

	static public int convertToInteger(JSONObject json, String key) {
		Object obj = json.get(key);
		if (obj == null) {
			return 0;
		}
		return Integer.parseInt(obj.toString());
	}

	static public boolean convertToBoolean(JSONObject json, String key) {
		Object obj = json.get(key);
		if (obj == null) {
			return false;
		}
		return (Boolean) obj;
	}

	public static boolean mayBeJSON(String string) {
		if (Strings.isEmpty(string)) {
			return false;
		} else if (string.startsWith("{") && string.endsWith("}")) {
			return true;
		} else if (string.startsWith("[") && string.endsWith("]")) {
			return true;
		}

		return false;
	}

	public static String prettyPrint(String string) {
		string = string.replace("{", "\t{");
		string = string.replace("}", "}\n");
		string = string.replace("[", "\t[");
		string = string.replace("]", "]\n");
		string = string.replace(",\t{", ",\n{");
		string = string.replace(",\t[", ",\n[");

		return string;
	}

	public static boolean isEmpty(JSONObject json) {
		if (json == null) {
			return true;
		}

		if (StringUtils.equals(json.toString(), EMPTY_JSON)) {
			return true;
		}

		return false;
	}

	public static boolean isNotEmpty(JSONObject json) {
		return !isEmpty(json);
	}

	public static JSONObject parseJsonFromInput(BufferedReader bufferedReader) throws IOException {
		JSONObject jsonObject = new JSONObject();
		if (bufferedReader == null) {
			return jsonObject;
		}

		StringBuilder jsonString = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			jsonString.append(line);
		}

		if (Strings.isEmpty(jsonString.toString())) {
			return jsonObject;
		}

		return (JSONObject) JSONValue.parse(jsonString.toString());
	}

}
