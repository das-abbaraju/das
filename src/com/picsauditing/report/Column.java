package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column implements JSONable {

	private String fieldName;
	private QueryMethod method = null;
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
		if (method != null) {
			json.put("method", method.toString());
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
		String methodName = (String) json.get("method");
		if (!Strings.isEmpty(methodName)) {
			this.method = QueryMethod.valueOf(methodName);
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

	public String getFieldNameWithoutMethod() {
		if (method == null)
			return fieldName;

		int startOfMethod = fieldName.lastIndexOf(method.toString());
		if (startOfMethod < 0)
			return fieldName;

		return fieldName.substring(0, startOfMethod);
	}

	public QueryMethod getMethod() {
		return method;
	}

	public void setMethod(QueryMethod method) {
		this.method = method;
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