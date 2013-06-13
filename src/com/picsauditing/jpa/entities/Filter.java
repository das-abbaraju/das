package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.report.fields.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.util.Strings;

@Entity
@Table(name = "report_filter")
public class Filter extends ReportElement {

	public static final String FILTER_VALUE_DELIMITER = ", ";
	private QueryFilterOperator operator;
	private String value = "";
	private String columnCompare;
	@Deprecated
	private Field fieldForComparison;

	@Deprecated
	public static final String FIELD_COMPARE = "fieldCompare";
	private static final Set<String> INDEXABLE_FIELDS = new HashSet<String>() {{
		add("AccountName");
	}};
	private static final Logger logger = LoggerFactory.getLogger(Filter.class);

	@Enumerated(EnumType.STRING)
    @ReportField
	public QueryFilterOperator getOperator() {
		return operator;
	}

	public void setOperator(QueryFilterOperator operator) {
		this.operator = operator;
	}

    @ReportField
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Transient
	public List<String> getValues() {
		if (StringUtils.isEmpty(StringUtils.trim(value))) {
			return new ArrayList<String>();
		}

		return Arrays.asList(value.split(FILTER_VALUE_DELIMITER));
	}

	public void setValues(List<String> values) {
		value = Strings.implode(values, FILTER_VALUE_DELIMITER);
	}

	@Transient
	public String getSql() {
		if (name.equalsIgnoreCase("accountName")) {
			field.setDatabaseColumnName("Account.nameIndex");
		} else if (name.equalsIgnoreCase("AccountUserUser")) {
			field.setDatabaseColumnName("AccountUser.userID");
		}

		return super.getSql();
	}

	@Transient
	public String getSqlForFilter() throws ReportValidationException {
		if (!isValid()) {
			return "true";
		}

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
		if (operator == null) {
			throw new ReportValidationException("missing operator for field " + name);
		}

		if (operator.isSingleValue()) {
			return buildFilterSingleValue();
		} else {
			return "(" + Strings.implodeForDB(getValues()) + ")";
		}
	}

	private String buildFilterSingleValue() {
		FilterType filterType = getFilterType();

		if (fieldForComparison != null) {
			return fieldForComparison.getDatabaseColumnName();
		}

		String filterValue = getValues().get(0);

		if (filterType == FilterType.Date) {
			QueryDateParameter parameter = new QueryDateParameter(filterValue);
			String dateValue = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()), Strings.EMPTY_STRING);
			return "'" + dateValue + "'";
		}

		// Todo: Consider converting FilterTypes to classes that extend Filter
		if (filterType == FilterType.Boolean) {
			if (filterValue.equals("1")) {
				return true + Strings.EMPTY_STRING;
			}
			if (filterValue.equalsIgnoreCase("true")) {
				return true + Strings.EMPTY_STRING;
			}
			if (filterValue.equalsIgnoreCase("Y")) {
				return true + Strings.EMPTY_STRING;
			}
			if (filterValue.equalsIgnoreCase("Yes")) {
				return true + Strings.EMPTY_STRING;
			}

			return false + Strings.EMPTY_STRING;
		}

		// todo: combine the logic below where possible
		if (filterType == FilterType.AccountID) {
			return Integer.parseInt(filterValue) + Strings.EMPTY_STRING;
		}

		if (filterType == FilterType.UserID) {
			return Integer.parseInt(filterValue) + Strings.EMPTY_STRING;
		}

		if (filterType == FilterType.Number) {
			try {
				return Integer.parseInt(filterValue) + Strings.EMPTY_STRING;
			} catch (Exception e) {
				return Float.parseFloat(filterValue) + Strings.EMPTY_STRING;
			}
		}

		if (filterType == FilterType.String) {
			filterValue = indexValueIfNecessary(filterValue, field);
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

		if (filterType == FilterType.Autocomplete) {
			return "'" + Strings.escapeQuotes(filterValue) + "'";
		}

		throw new RuntimeException(field.getType().getFilterType() + " has no filter calculation defined yet");
	}

	private String indexValueIfNecessary(String filterValue, Field field) {
		// FIXME: Until we have better field ids, we need to rely on a fragile string check.
		if (INDEXABLE_FIELDS.contains(field.getName())){
			return Strings.indexName(filterValue);
		}
		return filterValue;
	}

	@Transient
	private FilterType getFilterType() {
		if (sqlFunction != null) {
			return sqlFunction.getFilterType(field);
		}
		return field.getType().getFilterType();
	}

	// TODO: Filter should not be validating itself
	@Transient
	public boolean isValid() {
		if (field == null) {
			return false;
		}

		if (!operator.isValueCurrentlySupported()) {
			return true;
		}

		// TODO replace this with StringUtils.isEmpty(value)
		if (getValues().isEmpty() && fieldForComparison == null) {
			return false;
		}

		// TODO This should be fleshed out some more to validate all the
		// different filter types to make sure they are all properly defined.

		return true;
	}

	public void updateCurrentUser(Permissions permissions) {
		if (operator == QueryFilterOperator.CurrentAccount) {
			setValue(permissions.getAccountIdString());
		}

		if (operator == QueryFilterOperator.CurrentUser) {
			setValue(permissions.getUserIdString());
		}
	}

	@Column(name = "columnCompare")
	public String getColumnCompare() {
		return columnCompare;
	}

	public void setColumnCompare(String columnCompare) {
		this.columnCompare = columnCompare;

		// This is to load correctly from the DB until we remove fieldForComparison
		if (fieldForComparison == null) {
			fieldForComparison = new Field(columnCompare);
		}
	}

	// FIXME this is mainly used to check if it's null
	// i.e. whether this filter has a column compare or not
	@Deprecated
	@Transient
	public Field getFieldForComparison() {
		return fieldForComparison;
	}

	// TODO set column compare directly instead of setting the fieldForComparison
	@Deprecated
	public void setFieldForComparison(Field fieldForComparison) {
		this.fieldForComparison = fieldForComparison;

		if (fieldForComparison != null) {
			setColumnCompare(fieldForComparison.getName());
		}
	}

	@Override
	public void addFieldCopy(Map<String, Field> availableFields) {
		super.addFieldCopy(availableFields);

		if (columnCompare == null) {
			fieldForComparison = null;
			return;
		}

		String fieldName = fieldForComparison.getName();
		Field field = availableFields.get(fieldName.toUpperCase());

		if (field == null) {
			logger.warn("Failed to find " + fieldName + " in availableFields");
			return;
		}

		fieldForComparison = field.clone();
		fieldForComparison.setName(fieldName);
	}

	public String toString() {
		String display = getValues().toString();
		if (fieldForComparison != null) {
			display = fieldForComparison.toString();
		}

		return super.toString() + " " + operator + " " + display;
	}
}