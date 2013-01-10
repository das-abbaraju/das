package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_filter")
public class Filter extends ReportElement {

	private static final Logger logger = LoggerFactory.getLogger(Filter.class);

	private QueryFilterOperator operator = QueryFilterOperator.Equals;
	private List<String> values = new ArrayList<String>();

	public static final String FIELD_COMPARE = "fieldCompare";
	private String columnCompare;
	private Field fieldForComparison;

	@Enumerated(EnumType.STRING)
	public QueryFilterOperator getOperator() {
		return operator;
	}

	public void setOperator(QueryFilterOperator operator) {
		this.operator = operator;
	}

	@Transient
	// TODO add in a String value field
	public List<String> getValues() {
		return values;
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
			throw new ReportValidationException("missing operator for field " + name);

		if (operator.isSingleValue()) {
			return buildFilterSingleValue();
		} else {
			return "(" + Strings.implodeForDB(values, ",") + ")";
		}
	}

	private String buildFilterSingleValue() {
		DisplayType fieldType = getActualFieldTypeForFilter();

		if (fieldForComparison != null) {
			return fieldForComparison.getDatabaseColumnName();
		}

		String filterValue = getValues().get(0);

		if (fieldType.equals(DisplayType.LeftAlign)) {
			QueryDateParameter parameter = new QueryDateParameter(filterValue);
			String dateValue = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()), Strings.EMPTY_STRING);
			return "'" + dateValue + "'";
		}

		if (fieldType.equals(DisplayType.CheckMark)) {
			if (filterValue.equals("1"))
				return true + Strings.EMPTY_STRING;
			if (filterValue.equalsIgnoreCase("true"))
				return true + Strings.EMPTY_STRING;
			if (filterValue.equalsIgnoreCase("Y"))
				return true + Strings.EMPTY_STRING;
			if (filterValue.equalsIgnoreCase("Yes"))
				return true + Strings.EMPTY_STRING;
			return false + Strings.EMPTY_STRING;
		}

//		if (fieldType.equals(DisplayType.RightAlign)) {
//			return Float.parseFloat(filterValue) + Strings.EMPTY_STRING;
//		}

		if (fieldType.equals(DisplayType.RightAlign)) {
			if (NumberUtils.isDigits(filterValue)) {
				return Integer.parseInt(filterValue) + Strings.EMPTY_STRING;
			}
			
			return Float.parseFloat(filterValue) + Strings.EMPTY_STRING;
			
//			filterValue = Integer.parseInt(filterValue) + Strings.EMPTY_STRING;
			// Make sure we incorporate the filter strategy when using function
			// dates
			// return "DATE_SUB(CURDATE(), INTERVAL " + filterValue + " DAY)";
//			return filterValue;
		}

		if (fieldType.equals(DisplayType.LeftAlign)) {
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

	@Transient
	private DisplayType getActualFieldTypeForFilter() {
		DisplayType fieldType = field.getType().getDisplayType();
		if (hasMethodWithDifferentFieldType()) {
			fieldType = sqlFunction.getDisplayType();
		}
		return fieldType;
	}

	private boolean hasMethodWithDifferentFieldType() {
		if (sqlFunction == null || sqlFunction.getDisplayType() == null)
			return false;

		return true;
	}

	@Transient
	public boolean isValid() {
		if (field == null)
			return false;

		if (!operator.isValueUsed())
			return true;

		if (values.isEmpty() && fieldForComparison == null)
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

	@Column(name = "columnCompare")
	public String getColumnCompare() {
		return columnCompare;
	}

	public void setColumnCompare(String name) {
		this.columnCompare = name;
	}

	@Transient
	public Field getFieldForComparison() {
		return fieldForComparison;
	}

	public void setFieldForComparison(Field fieldForComparison) {
		this.fieldForComparison = fieldForComparison;
		setColumnCompare(fieldForComparison.getName()); 
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
		String display = values.toString();
		if (fieldForComparison != null)
			display = fieldForComparison.toString();

		return super.toString() + " " + operator + " " + display;
	}
}