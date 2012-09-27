package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column extends ReportElement implements JSONable {

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
		if (method != null) {
			json.put("method", method.toString());
		}
		return json;
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
