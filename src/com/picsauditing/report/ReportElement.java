package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.util.Strings;

public class ReportElement implements JSONable {

	protected String fieldName;
	protected Field field;

	public ReportElement() {
	}

	public ReportElement(String fieldName) {
		this.fieldName = fieldName;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", fieldName);
		if (field == null) {
			Field fakeField = new Field(fieldName, "", FilterType.String);
			json.put("field", fakeField);
		} else {
			json.put("field", field.toJSONObject());
		}
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		fieldName = (String) json.get("name");

		// this.field = (Field) json.get("field");
		// We are going to ignore the field and set this each time from
		// availableFields in SqlBuilder
	}

	public String getFieldName() {
		return Strings.escapeQuotes(fieldName);
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String toString() {
		return fieldName;
	}
}