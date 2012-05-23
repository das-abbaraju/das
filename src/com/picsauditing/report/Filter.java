package com.picsauditing.report;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.Strings;

public class Filter implements JSONable {

	private String fieldName;
	private QueryFilterOperator operator;
	private String value;
	private String valueNames;
	private Field field;

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		json.put("name", fieldName);

		if (operator != null)
			json.put("operator", operator.toString());
		if (value != null)
			json.put("value", value);
		if (field != null)
			json.put("field", field.toJSONObject());

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		fieldName = (String) json.get("name");
		if (fieldName == null)
			return;

		parseOperator(json);

		this.value = (String) json.get("value");
		this.field = (Field) json.get("field");
	}

	private void parseOperator(JSONObject json) {
		String object = (String)json.get("operator");
		if (Strings.isEmpty(object)) {
			operator = QueryFilterOperator.Equals;
			return;
		}

		this.operator = QueryFilterOperator.valueOf(object.toString());
	}

	/*DateTime(ExtFieldType.Date), AccountName(ExtFieldType.String), AccountType, AccountLevel, Trades, Country, StateProvince */

	@SuppressWarnings("unchecked")
	public static JSONArray getAccountStatusList() {
		AccountStatus[] list = AccountStatus.values();

		JSONArray json = new JSONArray();
		for (AccountStatus accountStatus : list) {
			json.add(accountStatus.toString());
		}

		return json;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getAuditStatusList() {
		AuditStatus[] list = AuditStatus.values();

		JSONArray json = new JSONArray();
		for (AuditStatus auditStatus : list) {
			json.add(auditStatus.toString());
		}

		return json;
	}

	@SuppressWarnings("unchecked")
	public static JSONArray getLowMedHighList() {
		LowMedHigh[] list = LowMedHigh.values();

		JSONArray json = new JSONArray();
		for (LowMedHigh lowMedHigh : list) {
			json.add(lowMedHigh.toString());
		}

		return json;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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

	public boolean isValid() {
		if (value == null)
			return false;

		return true;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public void setValueNames(String valueNames) {
		this.valueNames = valueNames;
	}

	public String getValueNames() {
		return valueNames;
	}

	public boolean isHasTranslations() {
		return field.getFilterType().isList();
	}
}
