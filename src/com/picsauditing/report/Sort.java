package com.picsauditing.report;

import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.Field;

public class Sort extends ReportElement implements JSONable {

	private boolean ascending = true;

	public Sort() {
	}

	public Sort(String fieldName) {
		super(fieldName);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		if (!ascending)
			json.put("direction", "DESC");

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		super.fromJSON(json);
		
		ascending = true;
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC"))
			ascending = false;
	}

	// We might want to consider moving this to QueryField
	public String toSQL(Map<String, Field> availableFields) {
		String fieldSQL = availableFields.get(fieldName).getDatabaseColumnName();
		return fieldSQL;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public String toString() {
		return super.toString() + (ascending ? " ASC" : " DESC");
	}
}
