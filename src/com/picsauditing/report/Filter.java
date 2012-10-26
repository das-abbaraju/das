package com.picsauditing.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.Strings;

public class Filter extends ReportElement implements JSONable {
	
	private static final String JSON_FIELD_FOR_COMPARISON_KEY = "fieldCompare";

	private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);

	private QueryFilterOperator operator = QueryFilterOperator.Equals;
	List<String> values = new ArrayList<String>();
	
	private boolean advancedFilter;
	private Field fieldForComparison;

	// TODO: Needs to be modified to serialize to JSON with the new advanced filter
	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = super.toJSON(full);

		json.put("operator", operator.toString());

		if (operator.isValueUsed()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(values);
			json.put("values", valueArray);

			// Until we phase out the old code, we need this for backwards compatibility
			if (values.size() == 1) {
				json.put("value", values.get(0));
			} else {
				json.put("value", StringUtils.join(values, ", "));
			}
		}
		
		json.put(JSON_FIELD_FOR_COMPARISON_KEY, advancedFilter);
		
		return json;
	}

	// TODO: Needs to be deserialized from JSON with the new advanced filter option
	public void fromJSON(JSONObject json) {
		if (json == null)
			return;

		super.fromJSON(json);

		parseOperator(json);

		parseValues(json);		
		
		parseAdvancedFilter(json);
	}

	private void parseOperator(JSONObject json) {
		String object = (String) json.get("operator");
		if (Strings.isEmpty(object)) {
			operator = QueryFilterOperator.Equals;
			return;
		}

		this.operator = QueryFilterOperator.valueOf(object.toString());
	}

	private void parseValues(JSONObject json) {
		JSONArray valuesJsonArray = null;

		try {
			valuesJsonArray = (JSONArray) json.get("values");
		} catch (ClassCastException cce) {
			logger.warn("A filter's values field is not a JSONArray", cce);
		} catch (Exception e) {
			logger.warn("Old format report that doesn't have 'values' in filter for fieldName = {0}", fieldName);
		}

		if (valuesJsonArray != null && valuesJsonArray.size() > 0) {
			for (Object value : valuesJsonArray) {
				this.values.add(value.toString().trim());
			}
		} else {
			String value = (String) json.get("value");

			if (Strings.isEmpty(value))
				return;

			logger.warn("Still using filter.value instead of filter.values");

			if (value.contains(",")) {
				logger.warn("Old style filter value found with commas separating multiple values. Until we phase out the old code, we need this for backwards compatibility");
				String[] valueSplit = value.split(", ");
				if (valueSplit.length == 1 && value.contains(","))
					valueSplit = value.split(",");
				this.values.addAll(Arrays.asList(valueSplit));
			} else {
				this.values.add(value);
			}
		}
	}
	
	private void parseAdvancedFilter(JSONObject json) {
		Object value = json.get(JSON_FIELD_FOR_COMPARISON_KEY);
		if (value == null)
			advancedFilter = false;
		else 
			advancedFilter = (Boolean) value;
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
	
	public boolean isAdvancedFilter() {
		return advancedFilter;
	}
	
	public void setAdvancedFilter(boolean advancedFilter) {
		this.advancedFilter = advancedFilter; 
	}

	public String getSql() {
		if (fieldName.equalsIgnoreCase("accountName")) {
			field.setDatabaseColumnName("Account.nameIndex");
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
		DisplayType fieldType = getActualFieldTypeForFilter();

		if (isAdvancedFilter()) {
			return fieldForComparison.getDatabaseColumnName();
		}
		
		String filterValue = getValues().get(0);

		if (fieldType.equals(DisplayType.Date)) {
			QueryDateParameter parameter = new QueryDateParameter(filterValue);
			String dateValue = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()), "");
			return "'" + dateValue + "'";
		}

		if (fieldType.equals(DisplayType.Boolean)) {
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

		if (fieldType.equals(DisplayType.Float)) {
			return Float.parseFloat(filterValue) + "";
		}

		if (fieldType.equals(DisplayType.Integer)) {
			filterValue = Integer.parseInt(filterValue) + "";
			// Make sure we incorporate the filter strategy when using function dates
			// return "DATE_SUB(CURDATE(), INTERVAL " + filterValue + " DAY)";
			return filterValue;
		}

		if (fieldType.equals(DisplayType.String)) {
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

	private DisplayType getActualFieldTypeForFilter() {
		DisplayType fieldType = field.getType().getDisplayType();
		if (hasMethodWithDifferentFieldType()) {
			fieldType = method.getDisplayType();
		}
		
		return fieldType;
	}

	private boolean hasMethodWithDifferentFieldType() {
		if (method == null || method.getDisplayType() == null)
			return false;

		return true;
	}

	public boolean isValid() {
		if (field == null)
			return false;
		
		if (!operator.isValueUsed())
			return true;

		if (values.isEmpty())
			return false;
		
		if (isAdvancedFilter() && fieldForComparison == null)
			return false;

		// TODO This should be fleshed out some more to validate all the
		// different filter types to make sure they are all properly defined.

		return true;
	}

	public void updateCurrentUser(Permissions permissions) {
		if (operator == QueryFilterOperator.CurrentAccount) {
			values.clear();
			values.add(permissions.getAccountIdString());
		}

		if (operator == QueryFilterOperator.CurrentUser) {
			values.clear();
			values.add(permissions.getUserIdString());
		}
	}
	
	@Override
	public void addFieldCopy(Map<String, Field> availableFields) {
		super.addFieldCopy(availableFields);
		
		if (!advancedFilter || CollectionUtils.isEmpty(values) || values.size() > 1) {
			fieldForComparison = null;
			return;
		}
		
		String fieldName = values.get(0);		
		Field field = availableFields.get(fieldName.toUpperCase());

		if (field == null) {
			logger.warn("Failed to find " + fieldName + " in availableFields");
			return;
		}

		fieldForComparison = field.clone();
		fieldForComparison.setName(fieldName);
	}

	public String toString() {
		return super.toString() + " " + operator + " " + values;
	}
}
