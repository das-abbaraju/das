package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column extends ReportElement implements JSONable {

	private int width = 200;
	private PivotDimension pivotDimension = null;
	private PivotCellMethod pivotCellMethod = null;

	public Column() {
	}

	public Column(String fieldName) {
		super(fieldName);
	}

	public Column(JSONObject json) {
		fromJSON(json);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);
		json.put("id", id);
		if (field == null) {
			json.put("name", id);
			json.put("is_sortable", false);
		} else {
			json.put("name", field.getText());
			json.put("type", getType());
			json.put("description", field.getHelp());
			json.put("is_sortable", field.isSortable());
			if (!Strings.isEmpty(field.getUrl()))
				json.put("url", field.getUrl());
		}

		json.put("width", width);
		if (method != null) {
			json.put("method", method.toString());
		}
		return json;
	}

	private String getType() {
		// boolean, flag, number, string
		return field.getType().toString();
	}

	public void fromJSON(JSONObject json) {
		super.fromJSON(json);
		parseMethodName(json);
	}

	private void parseMethodName(JSONObject json) {
		String methodName = (String) json.get("method");
		if (!Strings.isEmpty(methodName)) {
			method = QueryMethod.valueOf(methodName);
		}
	}

	public PivotDimension getPivotDimension() {
		return pivotDimension;
	}

	public PivotCellMethod getPivotCellMethod() {
		return pivotCellMethod;
	}
}
