package com.picsauditing.report;

import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryMethod;
import com.picsauditing.util.Strings;

public class ReportElement implements JSONable {

	private static final Logger logger = LoggerFactory.getLogger(ReportElement.class);

	public static String METHOD_SEPARATOR = "__";

	protected String fieldName;
	protected Field field;

	private String originalFieldName;
	protected QueryMethod method;

	public ReportElement() {
	}

	public ReportElement(String fieldName) {
		setFieldName(fieldName);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", fieldName);
		if (field == null) {
			Field fakeField = new Field(fieldName, "", FilterType.String);
			json.put("field", fakeField.toJSONObject());
		} else {
			json.put("field", field.toJSONObject());
		}
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		setFieldName((String) json.get("name"));

		// this.field = (Field) json.get("field");
		// We are going to ignore the field and set this each time from
		// availableFields in SqlBuilder
	}

	public String getFieldName() {
		return Strings.escapeQuotes(fieldName);
	}

	public void setMethodToFieldName() {
		int startOfMethod = fieldName.lastIndexOf(METHOD_SEPARATOR);
		if (startOfMethod >= 0 || method == null)
			return;

		this.fieldName = fieldName + METHOD_SEPARATOR + method;
		parseFieldNameMethod();
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		parseFieldNameMethod();
	}

	private void parseFieldNameMethod() {
		method = null;
		originalFieldName = fieldName;
		if (fieldName == null)
			return;

		int startOfMethod = fieldName.lastIndexOf(METHOD_SEPARATOR);
		if (startOfMethod < 0)
			return;

		originalFieldName = fieldName.substring(0, startOfMethod);
		String methodName = fieldName.substring(startOfMethod + 2);
		method = QueryMethod.valueOf(methodName);
	}

	public String getFieldNameWithoutMethod() {
		return originalFieldName;
	}

	public QueryMethod getMethod() {
		return method;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
		this.field.setName(fieldName);
	}

	public boolean isHasAggregateMethod() {
		if (method == null)
			return false;
		
		return method.isAggregate();
	}

	public String getSql() {
		String fieldSql = field.getDatabaseColumnName();
		if (method == null)
			return fieldSql;

		if (method.isAggregate()) {
			field.setUrl(null);
		}

		switch (method) {
		case Average:
			return "AVG(" + fieldSql + ")";
		case Count:
			return "COUNT(" + fieldSql + ")";
		case CountDistinct:
			return "COUNT(DISTINCT " + fieldSql + ")";
		case Date:
			return "DATE(" + fieldSql + ")";
		case GroupConcat:
			return "GROUP_CONCAT(" + fieldSql + ")";
		case Hour:
			return "HOUR(" + fieldSql + ")";
		case Left:
			return "LEFT(" + fieldSql + ")";
		case LowerCase:
			return "LOWER(" + fieldSql + ")";
		case Max:
			return "MAX(" + fieldSql + ")";
		case Min:
			return "MIN(" + fieldSql + ")";
		case Month:
			return "MONTH(" + fieldSql + ")";
		case Round:
			return "ROUND(" + fieldSql + ")";
		case Sum:
			return "SUM(" + fieldSql + ")";
		case UpperCase:
			return "UPPER(" + fieldSql + ")";
		case WeekDay:
			return "DATE_FORMAT(" + fieldSql + ",'%W')";
		case Year:
			return "YEAR(" + fieldSql + ")";
		case YearMonth:
			return "DATE_FORMAT(" + fieldSql + ",'%Y-%m')";
		}

		return fieldSql;
	}

	public void addFieldCopy(Map<String, Field> availableFields) {
		Field field = availableFields.get(originalFieldName.toUpperCase());

		if (field == null) {
			logger.warn("Failed to find [&1] in availableFields", originalFieldName);
			return;
		}

		setField(field.clone());
		this.field.setName(fieldName);
	}

	public String toString() {
		return fieldName;
	}
}