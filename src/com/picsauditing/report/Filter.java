package com.picsauditing.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.Strings;

public class Filter extends ReportElement implements JSONable {

	private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);

	private QueryFilterOperator operator = QueryFilterOperator.Equals;
	List<String> values = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		if (operator != null) {
			json.put("operator", operator.toString());
		}
		if (values.size() == 1) {
			// Until the front end changes the JavaScript, we need this for
			// backwards compatibility
			json.put("value", values.get(0));
		}
		JSONArray valueArray = new JSONArray();
		valueArray.addAll(values);
		json.put("values", valueArray);

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		super.fromJSON(json);

		parseOperator(json);

		JSONArray valuesJsonArray = (JSONArray) json.get("values");
		if (valuesJsonArray != null && valuesJsonArray.size() > 0) {
			for (Object value : valuesJsonArray) {
				this.values.add(value.toString());
			}
		} else {
			String value = (String) json.get("value");
			if (!Strings.isEmpty(value)) {
				logger.warn("Still using filter.value instead of filter.values");
				// Until the front end changes the JavaScript and all the
				// reports
				// are converted, we need this for backwards compatibility
				if (value.contains(",")) {
					String[] valueSplit = value.split(",");
					this.values.addAll(Arrays.asList(valueSplit));
				} else {
					this.values.add(value);
				}
			}
		}
	}

	private void parseOperator(JSONObject json) {
		String object = (String) json.get("operator");
		if (Strings.isEmpty(object)) {
			operator = QueryFilterOperator.Equals;
			return;
		}

		this.operator = QueryFilterOperator.valueOf(object.toString());
	}

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

	public QueryFilterOperator getOperator() {
		return operator;
	}

	public void setOperator(QueryFilterOperator operator) {
		this.operator = operator;
	}

	public List<String> getValues() {
		return values;
	}

	public String getSql() {
		if (fieldName.equals("accountName")) {
			field.setDatabaseColumnName("a.nameIndex");
		}

		return super.getSql();
	}

	public String getSqlForFilter() throws ReportValidationException {
		if (!isValid())
			return "true";

		String columnSql = getSql();

		boolean isEmpty = operator.equals(QueryFilterOperator.Empty);
		boolean isNotEmpty = operator.equals(QueryFilterOperator.NotEmpty);

		if (isEmpty) {
			return columnSql + " IS NULL OR " + columnSql + " = ''";
		} else if (isNotEmpty) {
			return columnSql + " IS NOT NULL OR " + columnSql + " != ''";
		}

		String operand = operator.getOperand();
		String valueSql = toValueSql();

		return columnSql + " " + operand + " " + valueSql;
	}

	private String toValueSql() throws ReportValidationException {
		if (operator == null)
			throw new ReportValidationException("missing operator for field " + fieldName);

		if (operator.isSingleValue()) {
			return buildFilterSingleValue();
		} else {
			return "(" + Strings.implodeForDB(values, ",") + ")";
		}
	}

	private String buildFilterSingleValue() {
		ExtFieldType fieldType = getActualFieldTypeForFilter();

		String filterValue = getValues().get(0);

		if (fieldType.equals(ExtFieldType.Date)) {
			QueryDateParameter parameter = new QueryDateParameter(filterValue);
			return StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()), "");
		}

		if (fieldType.equals(ExtFieldType.Boolean)) {
			if (filterValue.equals("1"))
				return true + "";
			if (filterValue.equalsIgnoreCase("true"))
				return true + "";
			if (filterValue.equalsIgnoreCase("Y"))
				return true + "";
			if (filterValue.equalsIgnoreCase("Yes"))
				return true + "";
			return false + "";
		}

		if (fieldType.equals(ExtFieldType.Float)) {
			return Float.parseFloat(filterValue) + "";
		}

		if (fieldType.equals(ExtFieldType.Int)) {
			filterValue = Integer.parseInt(filterValue) + "";

			if (field.getFilterType() == FilterType.DaysAgo) {
				return "DATE_SUB(CURDATE(), INTERVAL " + filterValue + " DAY)";
			}
			return filterValue;
		}

		if (fieldType.equals(ExtFieldType.String)) {
			filterValue = Strings.escapeQuotes(filterValue);
			
			switch (operator) {
			case NotBeginsWith:
			case BeginsWith:
				return "'" + filterValue + "%'";
			case NotEndsWith:
			case EndsWith:
				return "'%" + filterValue + "'";
			case NotContains:
			case Contains:
				return "'%" + filterValue + "%'";
			case NotEmpty:
			case Empty:
				return "";
			default:
				return "'" + filterValue + "'";
			}
		}

		throw new RuntimeException(fieldType + " has no filter caluculation defined yet");
	}

	private ExtFieldType getActualFieldTypeForFilter() {
		ExtFieldType fieldType = field.getType();
		if (hasMethodWithDifferentFieldType()) {
			fieldType = method.getType();
		}
		
		if (fieldType == ExtFieldType.Auto) {
			fieldType = ExtFieldType.String;
		}
		field.setType(fieldType);
		return fieldType;
	}

	private boolean hasMethodWithDifferentFieldType() {
		if (method == null || method.getType() == null)
			return false;
		if (method.isTypeAuto())
			return false;

		return true;
	}

	public boolean isValid() {
		if (operator == QueryFilterOperator.Empty)
			return true;
		if (operator == QueryFilterOperator.NotEmpty)
			return true;
		if (values.isEmpty())
			return false;

		// TODO This should be fleshed out some more to validate all the
		// different filter types to make sure they are all properly defined.

		return true;
	}

	public boolean isHasTranslations() {
		if (field != null) {
			FilterType filterType = field.getFilterType();

			return filterType.isAutocomplete() || filterType.isEnum();
		}

		return false;
	}

	public String toString() {
		return super.toString() + " " + operator + " " + values;
	}
}
