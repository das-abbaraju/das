package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;
import com.picsauditing.util.excel.ExcelSheet;

public class SqlBuilder {
	private Definition definition = new Definition();
	private SelectSQL sql;
	private Map<String, Field> availableFields;

	public SelectSQL initializeSql(AbstractModel model, Definition definition, Permissions permissions) throws ReportValidationException {
		this.definition = definition;
		
		sql = new SelectSQL();

		setFrom(model);

		availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		addFieldsAndGroupBy(definition.getColumns());
		addRuntimeFilters(permissions);
		addOrderByClauses(model);

		addJoins(model.getRootTable());

		sql.addWhere("1 " + model.getWhereClause(permissions));
		
		return sql;
	}

	private void setFrom(AbstractModel model) {
		String from = model.getRootTable().getTableName();
		String alias = model.getRootTable().getAlias();
		if (!Strings.isEmpty(alias))
			from += " AS " + alias;

		sql.setFromTable(from);
	}

	private void addJoins(AbstractTable table) {
		if (table == null || table.getJoins() == null)
			return;

		for (AbstractTable joinTable : table.getJoins()) {

			if (joinTable.isJoinNeeded(definition)) {
				String joinExpression = "";

				if (!joinTable.isInnerJoin())
					joinExpression += "LEFT ";

				joinExpression += "JOIN " + joinTable.getTableName();
				if (!Strings.isEmpty(joinTable.getAlias()))
					joinExpression += " AS " + joinTable.getAlias();

				joinExpression += " ON " + joinTable.getOnClause();
				sql.addJoin(joinExpression);
				addJoins(joinTable);
			}
		}
	}

	private void addFieldsAndGroupBy(List<Column> columns) {
		boolean usesGroupBy = usesGroupBy();

		// Make sure column has a field(?)
		for (Column column : columns) {
			Set<String> dependentFields = new HashSet<String>();
			
			String fieldNameWithoutMethod = column.getFieldNameWithoutMethod();
			String fieldName = column.getFieldName();
			if (fieldName.equals(fieldNameWithoutMethod) && column.getMethod() != null) {
				fieldName = fieldName + column.getMethod().toString();
				column.setFieldName(fieldName);
			}

			Field field = getAvailableFieldCopy(column);

			if (field == null)
				continue;
			
			field.setName(fieldName);

			if (column.getMethod() == null || !column.getMethod().isAggregate()) {
				// For example: Don't add in accountID automatically if
				// contractorName uses an aggregation like COUNT
				dependentFields.addAll(field.getDependentFields());
			}

			String columnSql = columnToSql(column, field);
			if (usesGroupBy && !isAggregate(column)) {
				sql.addGroupBy(columnSql);
			}

			sql.addField(columnSql + " AS `" + fieldName + "`");
			column.setField(field);

			addDependentFields(dependentFields, field);
		}
	}
	
	private Field getAvailableFieldCopy(Column column) {
		String fieldNameWithoutMethod = column.getFieldNameWithoutMethod();
		return getAvailableFieldCopy(fieldNameWithoutMethod);
	}

	private Field getAvailableFieldCopy(String fieldName) {
		Field field = availableFields.get(fieldName.toUpperCase());

		if (field == null)
			return null;
		
		return field.clone();
	}
	
	private void addDependentFields(Set<String> dependentFields, Field field) {
		for (String fieldName : dependentFields) {
			if (isFieldIncluded(fieldName))
				continue;

			Column column = new Column(fieldName);
			String columnSql = columnToSql(column, field);
			sql.addField(columnSql + " AS `" + fieldName + "`");
		}
	}

	private boolean isFieldIncluded(String fieldName) {
		for (Column column : definition.getColumns()) {
			if (column.getFieldNameWithoutMethod().equals(fieldName))
				return true;
		}
		return false;
	}

	private boolean usesGroupBy() {
		for (Column column : definition.getColumns()) {
			String fieldNameWithoutMethod = column.getFieldNameWithoutMethod();
			if (fieldNameWithoutMethod == null)
				continue;
			
			Field field = availableFields.get(fieldNameWithoutMethod.toUpperCase());
			if (field != null) {
				if (isAggregate(column)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAggregate(Column column) {
		if (column == null)
			return false;

		if (column.getMethod() == null)
			return false;

		return column.getMethod().isAggregate();
	}

	private String columnToSql(Column column, Field field) {
		if (field == null)
			return "";

		String fieldSql = field.getDatabaseColumnName();
		if (column.getMethod() == null)
			return fieldSql;
		
		if (column.getMethod().isAggregate()) {
			field.setUrl(null);
		}

		switch (column.getMethod()) {
		case Average:
			return "AVG(" + fieldSql + ")";
		case Count:
			return "COUNT(" + fieldSql + ")";
		case CountDistinct:
			return "COUNT(DISTINCT " + fieldSql + ")";
		case Date:
			return "DATE(" + fieldSql + ")";
		case GroupConcat:
			return "GROUP_CONCAT(" + fieldSql + ")";
		case Hour:
			return "HOUR(" + fieldSql + ")";
		case Left:
			return "LEFT(" + fieldSql + ")";
		case LowerCase:
			return "LOWER(" + fieldSql + ")";
		case Max:
			return "MAX(" + fieldSql + ")";
		case Min:
			return "MIN(" + fieldSql + ")";
		case Month:
			return "MONTH(" + fieldSql + ")";
		case Round:
			return "ROUND(" + fieldSql + ")";
		case Sum:
			return "SUM(" + fieldSql + ")";
		case UpperCase:
			return "UPPER(" + fieldSql + ")";
		case WeekDay:
			return "DATE_FORMAT(" + fieldSql + ",'%W')";
		case Year:
			return "YEAR(" + fieldSql + ")";
		case YearMonth:
			return "DATE_FORMAT(" + fieldSql + ",'%Y-%m')";
		}

		return fieldSql;
	}

	private void addRuntimeFilters(Permissions permissions) throws ReportValidationException {
		if (definition.getFilters().isEmpty())
			return;

		List<Filter> whereFilters = new ArrayList<Filter>();
		List<Filter> havingFilters = new ArrayList<Filter>();

		for (Filter filter : definition.getFilters()) {
			if (!filter.isValid())
				continue;
			
			Column column = getColumnFromFieldName(filter.getFieldName(), definition.getColumns());
			if (column == null) {
				Field field = getAvailableFieldCopy(filter.getFieldName());
				if (field != null) {
					whereFilters.add(filter);
					filter.setField(field.clone());
				}
				continue;
			}
			
			if (isAggregate(column)) {
				havingFilters.add(filter);
			} else {
				whereFilters.add(filter);
			}
			
			filter.setField(getAvailableFieldCopy(column));
		}

		String where = definition.getFilterExpression();
		if (where == null || Strings.isEmpty(where)) {
			where = "";
			for (int i = 0; i < whereFilters.size(); i++) {
				where += "{" + i + "} AND ";
			}
			where = StringUtils.removeEnd(where, " AND ");
		}

		int whereIndex = 0;
		for (Filter filter : whereFilters) {
			String filterExp = toFilterSql(filter, filter.getField());
			where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
			whereIndex++;
		}
		
		if (where.contains("{")) {
			// TODO Create a new Exception call ReportFilterExpression extends 
			throw new ReportValidationException("DynamicReports.FilterExpressionInvalid");
		}
		sql.addWhere(where);

		for (Filter filter : havingFilters) {
			String filterExp = toFilterSql(filter, filter.getField());
			sql.addHaving(filterExp);
		}
	}

	private String toFilterSql(Filter filter, Field field) {
		if (!filter.isValid())
			return "true";

		Column column = getColumnFromFieldName(filter.getFieldName(), definition.getColumns());

		if (column == null) {
			column = new Column(filter.getFieldName());
		}

		String columnSql = toColumnSql(column, field);

		if (filter.getOperator().equals(QueryFilterOperator.Empty)) {
			return columnSql + " IS NULL OR " + columnSql + " = ''";
		} else if (filter.getOperator().equals(QueryFilterOperator.NotEmpty)) {
			return columnSql + " IS NOT NULL OR " + columnSql + " != ''";
		}

		String operand = filter.getOperator().getOperand();
		String valueSql = toValueSql(filter, column, field);

		return columnSql + " " + operand + " " + valueSql;
	}

	private String toColumnSql(Column column, Field field) {
		if (column.getFieldName().equals("accountName"))
			field.setDatabaseColumnName("a.nameIndex");

		String columnSQL = columnToSql(column, field);

		return columnSQL;
	}

	private String toValueSql(Filter filter, Column column, Field field) {
		ExtFieldType fieldType = field.getType();

		String filterValue = Strings.escapeQuotes(filter.getValue());
		
		if (fieldType.equals(ExtFieldType.Date) && column.getMethod() == null) {
			QueryDateParameter parameter = new QueryDateParameter(filterValue);

			filterValue = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()), "");
		}

		switch (filter.getOperator()) {
		case NotBeginsWith:
		case BeginsWith:
			return "'" + filterValue + "%'";
		case NotEndsWith:
		case EndsWith:
			return "'%" + filterValue + "'";
		case NotContains:
		case Contains:
			return "'%" + filterValue + "%'";
		case NotIn:
		case In:
			filterValue = addQuotesToValues(filterValue);
			return "(" + filterValue + ")";
		case NotEmpty:
		case Empty:
			// TODO
		}

		if (fieldType.equals(ExtFieldType.Boolean))
			return filterValue;
		else
			return addQuotesToValues(filterValue);
	}

	private String addQuotesToValues(String unquotedValuesString) {
		String[] unquotedValues = unquotedValuesString.split(",");
		List<String> quotedList = new ArrayList<String>();

		for (String unquotedValue : unquotedValues){
			quotedList.add("'" + unquotedValue.trim() + "'");
		}

		return StringUtils.join(quotedList.toArray(),",");
	}

	private void addOrderByClauses(AbstractModel model) {
		if (definition.getSorts().isEmpty()) {
			if (usesGroupBy())
				return;

			sql.addOrderBy(model.getDefaultSort());
			return;
		}

		for (Sort sort : definition.getSorts()) {
			String fieldName;
			Column column = getColumnFromFieldName(sort.getFieldName(), definition.getColumns());
			if (column == null) {
				Field field = getAvailableFieldCopy(sort.getFieldName());
				if (field != null && field.getDatabaseColumnName() != null)
					fieldName = field.getDatabaseColumnName();
				else
					continue;
				sort.setField(field);
			} else {
				fieldName = column.getFieldName();
				sort.setField(getAvailableFieldCopy(column));
			}

			if (!sort.isAscending())
				fieldName += " DESC";

			sql.addOrderBy(fieldName);
		}
	}
	
	private Column getColumnFromFieldName(String fieldName, List<Column> columns) {
		if (fieldName == null)
			return null;

		for (Column column : columns) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}

		return null;
	}
}
