package com.picsauditing.report.fields;

import java.util.Map;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fieldtypes.ExtFieldType;
import com.picsauditing.util.Strings;

public class SimpleReportField implements JSONable {
	private String field;
	private QueryFunction function = null;
	private String option;
	private boolean ascending = true;
	// We are thinking about adding the render field to support custom renderers per report
	private String renderer = null;
	private QueryField queryField;

	public QueryField getQueryField() {
		return queryField;
	}

	public void setQueryField(QueryField queryField) {
		this.field = queryField.getDataIndex();
		this.queryField = queryField;
	}

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

	// We might want to consider moving this to QueryField
	public String toSQL(Map<String, QueryField> availableFields) {
		String fieldSQL = availableFields.get(field).getSql();
		if (function == null)
			return fieldSQL;
		switch (function) {
		case Average:
			return "AVG(" + fieldSQL + ")";
		case Count:
			if (fieldSQL == null)
				return "COUNT(*)";
			else
				return "COUNT(" + fieldSQL + ")";
		case CountDistinct:
			return "COUNT(DISTINCT " + fieldSQL + ")";
		case Date:
			return "DATE(" + fieldSQL + ")";
		case Format:
			availableFields.get(field).setType(ExtFieldType.String);
			return "DATE_FORMAT(" + fieldSQL + ", '" + option + "')";
		case Lower:
			return "LOWER(" + fieldSQL + ")";
		case Max:
			return "MAX(" + fieldSQL + ")";
		case Min:
			return "MIN(" + fieldSQL + ")";
		case Month:
			return "MONTH(" + fieldSQL + ")";
		case Round:
			return "ROUND(" + fieldSQL + ")";
		case Sum:
			return "SUM(" + fieldSQL + ")";
		case Upper:
			return "UPPER(" + fieldSQL + ")";
		case Year:
			return "YEAR(" + fieldSQL + ")";
		}
		return fieldSQL;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
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

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public String getRenderer() {
		return renderer;
	}

	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}

}
