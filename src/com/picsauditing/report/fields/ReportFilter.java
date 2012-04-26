package com.picsauditing.report.fields;

import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.Strings;

public class ReportFilter implements JSONable {
	private String column;
	private QueryFilterOperator operator;
	private String column2;
	private String value;
	private QueryField field;

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("column", column);
		if (operator != null)
			json.put("operator", operator.toString());
		if (column2 != null)
			json.put("column2", column2);
		if (value != null)
			json.put("value", value);
		if (field != null)
			json.put("field", field.toJSONObject());
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		column = (String) json.get("column");
		if (column == null)
			return;

		parseOperator(json);

		this.column2 = (String) json.get("column2");
		this.value = (String) json.get("value");
		this.field = (QueryField) json.get("field");
	}

	private void parseOperator(JSONObject json) {
		String object = (String)json.get("operator");
		if (Strings.isEmpty(object)) {
			operator = QueryFilterOperator.Equals;
			return;
		}

		this.operator = QueryFilterOperator.valueOf(object.toString());
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public QueryFilterOperator getOperator() {
		return operator;
	}

	public void setOperator(QueryFilterOperator operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColumn2() {
		return column2;
	}

	public void setColumn2(String column2) {
		this.column2 = column2;
	}
	
	public boolean isValid() {
		if (value == null)
			return false;
		
		return true;
	}

	public QueryField getField() {
		return field;
	}

	public void setField(QueryField field) {
		this.field = field;
	}
}
