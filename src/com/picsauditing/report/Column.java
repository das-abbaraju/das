package com.picsauditing.report;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.PivotCellMethod;
import com.picsauditing.report.fields.PivotDimension;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class Column extends ReportElement implements JSONable {

	private static final Logger logger = LoggerFactory.getLogger(Column.class);
	
	private PivotDimension pivotDimension = null;
	private PivotCellMethod pivotCellMethod = null;
	
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
			try {
				method = QueryMethod.valueOf(methodName);
			} catch (Exception e) {
				logger.error("Using QueryMethod of " + methodName + " that doesn't exist");
			}
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
	
	public PivotDimension getPivotDimension() {
		return pivotDimension;
	}
	
	public PivotCellMethod getPivotCellMethod() {
		return pivotCellMethod;
	}
}
