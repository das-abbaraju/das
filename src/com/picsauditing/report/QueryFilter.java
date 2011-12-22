package com.picsauditing.report;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.JSONable;

public class QueryFilter implements JSONable {
	private SortableField field;
	private boolean not = false;
	private QueryFilterOperator operator;
	private SortableField field2;
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
		
		Object fieldObj = json.get("field");
		if (fieldObj != null) {
			this.field = new SortableField();

			if (fieldObj instanceof JSONObject) {
				this.field.fromJSON((JSONObject) fieldObj);
			} else {
				field.field = (String) fieldObj;
			}
		}
		this.operator = QueryFilterOperator.valueOf(json.get("operator").toString());

		this.not = false;
		String not = (String) json.get("not");
		if (not != null)
			this.not = true;
		
		Object field2Obj = json.get("field2");
		if (field2Obj != null) {
			this.field2 = new SortableField();

			if (field2Obj instanceof JSONObject) {
				this.field2.fromJSON((JSONObject) field2Obj);
			} else {
				field2.field = (String) field2Obj;
			}
		}

		this.value = (String) json.get("value");
		this.value = Utilities.escapeQuotes(value);
		this.value2 = (String) json.get("value2");
		this.value2 = Utilities.escapeQuotes(value2);
	}

	public String toExpression(Map<String, QueryField> availableFields) {
		QueryField queryField = availableFields.get(field.field);
		String columnSQL = field.toSQL(availableFields);
		if (field.field.equals("accountName"))
			columnSQL = "a.nameIndex";
		if (queryField.type.equals(FieldType.Date)) {
			QueryDateParameter parameter = new QueryDateParameter(value);
			value = DateBean.toDBFormat(parameter.getTime());
			QueryDateParameter parameter2 = new QueryDateParameter(value2);
			value2 = DateBean.toDBFormat(parameter2.getTime());
		}

		String expression = columnSQL + " " + operator.getOperand() + " ";
		String wrappedValue = null;

		if (StringUtils.isEmpty(value) && field2.field != null) {
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

	public SortableField getField() {
		return field;
	}

	public void setField(SortableField field) {
		this.field = field;
	}

	public SortableField getField2() {
		return field2;
	}

	public void setField2(SortableField field2) {
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
