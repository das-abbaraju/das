package com.picsauditing.report;

import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.Strings;

public class SortableField implements JSONable {
	public String field;
	public QueryFunction function = null;
	public String option;
	public boolean ascending = true;

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("field", field);
		if (function != null) {
			json.put("function", function.toString());
			if (!Strings.isEmpty(option))
				json.put("option", option);
		}
		if (!ascending)
			json.put("direction", "DESC");
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		this.field = (String) json.get("field");
		Object functionObj = json.get("function");
		if (functionObj != null) {
			this.function = QueryFunction.valueOf(functionObj.toString());
			this.option = (String) json.get("option");
		}

		this.ascending = true;
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC"))
			this.ascending = false;
	}

	public String toSQL(Map<String, QueryField> availableFields) {
		String fieldSQL = availableFields.get(field).sql;
		if (function == null)
			return fieldSQL;
		switch (function) {
		case Format:
			return "DATE_FORMAT(" + fieldSQL + ", '" + option + "')";
		case Sum:
			return "SUM(" + fieldSQL + ")";
		case Count:
			if (fieldSQL == null)
				return "COUNT(*)";
			else
				return "COUNT(" + fieldSQL + ")";
		}
		return fieldSQL;
	}

	private String includeDirection(String sql) {
		if (!ascending)
			sql += " DESC";
		return sql;
	}
}
