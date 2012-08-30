package com.picsauditing.report;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column extends ReportElement implements JSONable {

	public Column() {
	}

	public Column(String fieldName) {
		super(fieldName);
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

	/**
	 * This is a data conversion method to cleanup existing column names with Methods
	 */
	public void setMethod(QueryMethod method) {
		if (fieldName.contains(METHOD_SEPARATOR))
			return;
		int locationOfMethod = fieldName.lastIndexOf(method.toString());
		if (locationOfMethod == -1)
			return;
		super.setFieldName(fieldName.substring(0, locationOfMethod) + METHOD_SEPARATOR + method);
	}
	
}
