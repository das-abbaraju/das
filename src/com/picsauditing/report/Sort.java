package com.picsauditing.report;

import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

public class Sort implements JSONable {

	private String fieldName;
	private boolean ascending = true;
	private Field field;

	public Sort() {
	}

	public Sort(String fieldName) {
		this.fieldName = fieldName;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", fieldName);
		if (!ascending)
			json.put("direction", "DESC");
		if (field != null)
			json.put("field", field.toJSONObject());
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		this.fieldName = (String) json.get("name");

		this.ascending = true;
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC"))
			this.ascending = false;

		this.field = (Field) json.get("field");
	}

	// We might want to consider moving this to QueryField
	public String toSQL(Map<String, Field> availableFields) {
		String fieldSQL = availableFields.get(fieldName).getDatabaseColumnName();
		return fieldSQL;
	}

	public String getFieldName() {
		return Strings.escapeQuotes(fieldName);
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}
