package com.picsauditing.report.fields;

import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;

public class SimpleReportSort implements JSONable {
	private String column;
	private boolean ascending = true;

	public SimpleReportSort() {
	}

	public SimpleReportSort(String column) {
		this.column = column;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("column", column);
		if (!ascending)
			json.put("direction", "DESC");
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		this.column = (String) json.get("column");

		this.ascending = true;
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC"))
			this.ascending = false;
	}

	// We might want to consider moving this to QueryField
	public String toSQL(Map<String, QueryField> availableFields) {
		String fieldSQL = availableFields.get(column).getSql();
		return fieldSQL;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

}
