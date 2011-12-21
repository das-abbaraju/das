package com.picsauditing.report;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.JSONable;

public class QueryFilter implements JSONable {
	private String field;
	private boolean not = false;
	private QueryFilterOperator operator;
	private String field2;
	private String value;
	private String value2;

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("field", field);
		json.put("operator", operator.toString());
		if (not)
			json.put("not", "NOT");
		json.put("field2", field2);
		json.put("value", value);
		json.put("value2", value2);
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		this.field = (String) json.get("field");
		this.operator = QueryFilterOperator.valueOf(json.get("operator").toString());

		this.not = false;
		String not = (String) json.get("not");
		if (not != null)
			this.not = true;
		this.field2 = (String) json.get("field2");
		this.field2 = Utilities.escapeQuotes(field2);
		this.value = (String) json.get("value");
		this.value = Utilities.escapeQuotes(value);
		this.value2 = (String) json.get("value2");
		this.value2 = Utilities.escapeQuotes(value2);
	}

	public String toExpression(Map<String, QueryField> availableFields) {
		QueryField queryField = availableFields.get(field);
		String columnSQL = queryField.sql;
		if (field.equals("accountName"))
			columnSQL = "a.nameIndex";
		if (queryField.type.equals(FieldType.Date)) {
			String expression = columnSQL + " " + operator.getOperand() + " '";
			QueryDateParameter parameter = new QueryDateParameter(value);
			return expression + DateBean.toDBFormat(parameter.getTime()) + "'";
		}

		// TODO: Current logic can compare intervals and now as timestamps. It does not cover days, months, or years.

		String expression = columnSQL + " " + operator.getOperand() + " ";
		String wrappedValue = null;

		if (StringUtils.isEmpty(value) && field2 != null) {
			QueryField queryField2 = availableFields.get(field2);
			String columnSQL2 = queryField2.sql;
			wrappedValue = columnSQL2;
		}

		if (!StringUtils.isEmpty(value)) {
			switch (operator) {
			case BeginsWith:
				wrappedValue = "'" + value + "%'";
				break;
			case EndsWith:
				wrappedValue = "'%" + value + "'";
				break;
			case Contains:
				wrappedValue = "'%" + value + "%'";
				break;
			case Between:
				wrappedValue = "'" + value + "' AND '" + value2 + "'";
				break;
			case In:
			case InReport:
				// this only supports numbers, no strings or dates
				wrappedValue = "(" + value + ")";
				break;
			case Empty:
				break;
			default:
				wrappedValue = "'" + value + "'";
				break;
			}
		}

		expression += wrappedValue;

		return expression;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public QueryFilterOperator getOperator() {
		return operator;
	}

	public void setOperator(QueryFilterOperator operator) {
		this.operator = operator;
	}

	public boolean isNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
