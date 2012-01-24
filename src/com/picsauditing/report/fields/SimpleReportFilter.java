package com.picsauditing.report.fields;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.JSONable;

public class SimpleReportFilter implements JSONable {
	private SimpleReportField field;
	private boolean not = false;
	private QueryFilterOperator operator;
	private SimpleReportField field2;
	private String value;

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("field", field);
		json.put("operator", operator.toString());
		if (not)
			json.put("not", "NOT");
		json.put("field2", field2);
		json.put("value", value);
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		parseField(json);
		if (field == null)
			return;

		parseNot(json);
		parseOperator(json);

		parseValue(json);
	}

	private void parseField(JSONObject json) {
		Object fieldObj = json.get("field");
		if (fieldObj == null)
			return;

		field = new SimpleReportField();

		if (fieldObj instanceof JSONObject) {
			field.fromJSON((JSONObject) fieldObj);
		} else {
			field.setField((String) fieldObj);
		}
	}

	private void parseNot(JSONObject json) {
		this.not = false;
		String not = (String) json.get("not");
		if (not != null)
			this.not = true;
	}

	private void parseOperator(JSONObject json) {
		Object object = json.get("operator");
		if (object == null) {
			operator = QueryFilterOperator.Equals;
			return;
		}

		this.operator = QueryFilterOperator.valueOf(object.toString());
	}

	private void parseValue(JSONObject json) {
		Object field2Obj = json.get("field2");
		if (field2Obj != null) {
			this.field2 = new SimpleReportField();

			if (field2Obj instanceof JSONObject) {
				this.field2.fromJSON((JSONObject) field2Obj);
			} else {
				field2.setField((String) field2Obj);
			}
		}

		this.value = (String) json.get("value");
		this.value = Utilities.escapeQuotes(value);
	}

	public String toExpression(Map<String, QueryField> availableFields) {
		QueryField queryField = availableFields.get(field.getField());
		String columnSQL = field.toSQL(availableFields);
		if (field.getField().equals("accountName"))
			columnSQL = "a.nameIndex";
		if (queryField.getType().equals(FieldType.Date)) {
			QueryDateParameter parameter = new QueryDateParameter(value);
			value = DateBean.toDBFormat(parameter.getTime());
		}

		String expression = columnSQL + " " + operator.getOperand() + " ";
		String wrappedValue = null;

		if (StringUtils.isEmpty(value) && field2.getField() != null) {
			String columnSQL2 = field2.toSQL(availableFields);
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

	public SimpleReportField getField() {
		return field;
	}

	public void setField(SimpleReportField field) {
		this.field = field;
	}

	public SimpleReportField getField2() {
		return field2;
	}

	public void setField2(SimpleReportField field2) {
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
