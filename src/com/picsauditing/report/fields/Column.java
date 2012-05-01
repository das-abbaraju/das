package com.picsauditing.report.fields;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.Strings;

public class Column implements JSONable {
	private String name;
	private QueryFunction function = null;
	private String option;
	private QueryField field;

	public Column() {
	}

	public Column(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", name);
		if (function != null) {
			json.put("method", function.toString());
			if (!Strings.isEmpty(option))
				json.put("option", option);
		}
		if (field != null)
			json.put("field", field.toJSONObject());
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		this.name = (String) json.get("name");
		String methodName = (String) json.get("method");
		if (!Strings.isEmpty(methodName)) {
			this.function = QueryFunction.valueOf(methodName);
			this.option = (String) json.get("option");
		}
		this.field = (QueryField) json.get("field");
	}

	public String getName() {
		return name;
	}

	public String getAvailableFieldName() {
		if (function == null)
			return name;
		int startOfFunction = name.lastIndexOf(function.toString());
		if (startOfFunction < 0)
			return name;
		return name.substring(0, startOfFunction);
	}

	public void setName(String field) {
		this.name = field;
	}

	public QueryFunction getFunction() {
		return function;
	}

	public void setFunction(QueryFunction function) {
		this.function = function;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public QueryField getField() {
		return field;
	}

	public void setField(QueryField field) {
		this.field = field;
	}
}