package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column implements JSONable {

	private String fieldName;
	private QueryMethod method = null;

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

		// TODO check if this is being used on the front end
		if (method != null) {
			json.put("method", method.toString());
		}

		if (field != null) {
			JSONObject fieldJson = field.toJSONObject();

			if (method != null && method.getType() != ExtFieldType.Auto) {
				String alteredType = method.getType().toString();

				fieldJson.put("type", alteredType.toLowerCase());
				fieldJson.put("filterType", alteredType);
			}

			json.put("field", fieldJson);
		}

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		fieldName = (String) json.get("name");
		String methodName = (String) json.get("method");
		if (!Strings.isEmpty(methodName)) {
			method = QueryMethod.valueOf(methodName);
		}

		field = (Field) json.get("field");
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

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}