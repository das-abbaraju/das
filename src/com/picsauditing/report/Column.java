package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column extends ReportElement implements JSONable {

	private QueryMethod method = null;

	public Column() {
	}

	public Column(String fieldName) {
		super(fieldName);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		if (method != null) {
			json.put("method", method.toString());

			if (method.getType() != ExtFieldType.Auto) {
				String alteredType = method.getType().toString();

				JSONObject fieldJson = (JSONObject) json.get("field");
				fieldJson.put("type", alteredType.toLowerCase());
				fieldJson.put("filterType", alteredType);
			}
		}

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		super.fromJSON(json);
		
		parseMethodName(json);
	}

	private void parseMethodName(JSONObject json) {
		String methodName = (String) json.get("method");
		if (!Strings.isEmpty(methodName)) {
			method = QueryMethod.valueOf(methodName);
		}
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