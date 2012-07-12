package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFunction;
import com.picsauditing.util.Strings;

public class Column implements JSONable {

	private String fieldName;
	// TODO: Change all "Function" to "Method"
	private QueryFunction function = null;
	private String option;
	private Field field;

	public Column() {
	}

	public Column(String fieldName) {
		this.fieldName = fieldName;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", fieldName);
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

		this.fieldName = (String) json.get("name");
		String functionName = (String) json.get("method");
		if (!Strings.isEmpty(functionName)) {
			this.function = QueryFunction.valueOf(functionName);
			this.option = (String) json.get("option");
		}
		this.field = (Field) json.get("field");
	}

	public String getFieldName() {
		return Strings.escapeQuotes(fieldName);
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldNameWithoutFunction() {
		if (function == null)
			return fieldName;

		int startOfFunction = fieldName.lastIndexOf(function.toString());
		if (startOfFunction < 0)
			return fieldName;

		return fieldName.substring(0, startOfFunction);
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

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}