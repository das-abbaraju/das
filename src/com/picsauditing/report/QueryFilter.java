package com.picsauditing.report;

import java.util.Map;

import org.json.simple.JSONObject;

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
		json.put("operator", operator);
		if (not)
			json.put("not", "NOT");
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
		this.value = (String) json.get("value");
		this.value = Utilities.escapeQuotes(value);
		this.value2 = (String) json.get("value2");
		this.value2 = Utilities.escapeQuotes(value2);
	}

	public String toExpression(Map<String, QueryField> availableFields) {
		String columnSQL = availableFields.get(field).sql;
		if (field.equals("accountName"))
			columnSQL = "a.nameIndex";
		
		// TODO: Apply field to field comparisons
		// TODO: for dates, think about NOW() and CURDATE(), and intervals from there (i.e. 30 days after, 2 weeks before)
		// TODO: for boolean, think about 'true' and 'false'
		// TODO: for users, think about user group vs user individuals
		
		String expression = columnSQL + " " + operator.getOperand() + " ";
		switch (operator) {
		case BeginsWith:
			expression += "'" + value + "%'";
			break;
		case EndsWith:
			expression += "'%" + value + "'";
			break;
		case Contains:
			expression += "'%" + value + "%'";
			break;
		case Between:
			expression += "'" + value + "' AND '" + value2 + "'";
			break;
		case In:
			// this only supports numbers, no strings or dates
			expression += "(" + value + ")";
			break;
		case InReport:
			expression += "({REPORT:" + value + "})";
			break;
		case Empty:
			break;
		default:
			expression += "'" + value + "'";
			break;
		}

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
