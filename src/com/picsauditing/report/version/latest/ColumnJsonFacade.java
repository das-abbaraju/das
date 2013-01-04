package com.picsauditing.report.version.latest;

import org.json.simple.JSONObject;

import com.picsauditing.report.Column;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.util.Strings;

public class ColumnJsonFacade {
	
	@SuppressWarnings("unchecked")
	static public JSONObject toJSON(Column column) {
		JSONObject json = new JSONObject();
		json.put("id", column.get);
		if (field == null) {
			json.put("name", id);
			json.put("is_sortable", false);
		} else {
			json.put("name", field.getText());
			json.put("type", getType());
			json.put("description", field.getHelp());
			json.put("is_sortable", field.isSortable());
			if (!Strings.isEmpty(field.getUrl()))
				json.put("url", field.getUrl());
		}

		json.put("width", width);
		if (method != null) {
			json.put("sql_function", method.toString());
		}
		return json;
	}

	private String getType() {
		// boolean, flag, number, string
		return field.getType().toString();
	}

	public void fromJSON(JSONObject json) {
		super.fromJSON(json);
		parseMethodName(json);
	}

	private void parseMethodName(JSONObject json) {
		String methodName = (String) json.get("method");
		if (!Strings.isEmpty(methodName)) {
			method = SqlFunction.valueOf(methodName);
		}
	}


}
