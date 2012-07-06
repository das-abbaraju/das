package com.picsauditing.report;

import static com.picsauditing.report.access.ReportUtil.getColumnFromFieldName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.model.ReportDynamicModel;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;
import com.picsauditing.util.excel.ExcelSheet;

public class SqlBuilder {

	// TODO remove definition, get from Report passed in instead
	private Definition definition = new Definition();
	private SelectSQL sql;

	public SelectSQL buildSql(Report report, Permissions permissions, int pageNumber) {
		return buildSql(report, permissions, pageNumber, false);
	}

	public SelectSQL buildSql(Report report, Permissions permissions, int pageNumber, boolean forDownload) {
		AbstractModel model = report.getModel();
		sql = initializeSql(model);

		sql.addWhere(model.getWhereClause(permissions));

		if (!forDownload) {
			int rowsPerPage = report.getRowsPerPage();

			if (pageNumber > 1) {
				sql.setStartRow((pageNumber - 1) * rowsPerPage);
			}

			sql.setLimit(rowsPerPage);
			sql.setSQL_CALC_FOUND_ROWS(true);
		}

		return sql;
	}

	// TODO change this to pass in a report
	public SelectSQL initializeSql(AbstractModel model) {
		sql = new SelectSQL();

		setFrom(model);

		Map<String, Field> availableFields = ReportDynamicModel.buildAvailableFields(model.getPrimaryTable());

		addFieldsAndGroupBy(availableFields, definition.getColumns());
		addRuntimeFilters(availableFields);
		addOrderByClauses(model, availableFields);

		addJoins(model.getPrimaryTable());

		return sql;
	}

	@Anonymous
	public ExcelSheet extractColumnsToExcel(ExcelSheet excelSheet) {
		for (String field : sql.getFields()) {
			String alias = SelectSQL.getAlias(field);
			excelSheet.addColumn(new ExcelColumn(alias, alias));
		}

		return excelSheet;
	}

	private void setFrom(AbstractModel model) {
		String from = model.getPrimaryTable().getTableName();
		String alias = model.getPrimaryTable().getAlias();
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

				joinExpression += " ON " + joinTable.getWhereClause();
				sql.addJoin(joinExpression);
				addJoins(joinTable);
			}
		}
	}

	private void addFieldsAndGroupBy(Map<String, Field> availableFields, List<Column> columns) {
		Set<String> dependentFields = new HashSet<String>();
		boolean usesGroupBy = usesGroupBy(availableFields);

		// Make sure column has a field(?)
		for (Column column : columns) {
			Field field = availableFields.get(column.getFieldName().toUpperCase());

			if (field != null) {
				if (column.getFunction() == null || !column.getFunction().isAggregate()) {
					// For example: Don't add in accountID automatically if
					// contractorName uses an aggregation like COUNT
					dependentFields.addAll(field.getDependentFields());
				}

				String columnSql = columnToSql(column, availableFields);
				if (usesGroupBy && !isAggregate(column)) {
					sql.addGroupBy(columnSql);
				}

				sql.addField(columnSql + " AS `" + column.getFieldName() + "`");
				column.setField(field);
			}
		}

		addDependentFields(dependentFields, availableFields);
	}

	private void addDependentFields(Set<String> dependentFields, Map<String, Field> availableFields) {
		for (String fieldName : dependentFields) {
			if (isFieldIncluded(fieldName))
				continue;

			Column column = new Column(fieldName);
			String columnSql = columnToSql(column, availableFields);
			sql.addField(columnSql + " AS `" + fieldName + "`");
		}
	}

	private boolean isFieldIncluded(String fieldName) {
		for (Column column : definition.getColumns()) {
			if (column.getFieldName().equals(fieldName))
				return true;
		}
		return false;
	}

	private boolean usesGroupBy(Map<String, Field> availableFields) {
		for (Column column : definition.getColumns()) {
			Field field = availableFields.get(column.getFieldName().toUpperCase());
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

		if (column.getFunction() == null)
			return false;

		return column.getFunction().isAggregate();
	}

	private String columnToSql(Column column, Map<String, Field> availableFields) {
		Field field = availableFields.get(column.getFieldName().toUpperCase());
		if (field == null)
			return "";

		String fieldSql = field.getDatabaseColumnName();
		if (column.getFunction() == null)
			return fieldSql;

		switch (column.getFunction()) {
		case Average:
			return "AVG(" + fieldSql + ")";
		case Count:
			return "COUNT(" + fieldSql + ")";
		case CountDistinct:
			return "COUNT(DISTINCT " + fieldSql + ")";
		case Date:
			return "DATE(" + fieldSql + ")";
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
		case Year:
			return "YEAR(" + fieldSql + ")";
		}

		return fieldSql;
	}

	private void addRuntimeFilters(Map<String, Field> availableFields) {
		if (definition.getFilters().isEmpty())
			return;

		Set<Filter> whereFilters = new HashSet<Filter>();
		Set<Filter> havingFilters = new HashSet<Filter>();

		for (Filter filter : definition.getFilters()) {
			// TODO we might want to verify the filter is properly defined
			// before including it
			// if (filter.isFullyDefined()) { }
			Column column = getColumnFromFieldName(filter.getFieldName(), definition.getColumns());
			if (isAggregate(column)) {
				havingFilters.add(filter);
			} else {
				whereFilters.add(filter);
			}

			Field field = availableFields.get(filter.getFieldName().toUpperCase());
			filter.setField(field);
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
			Column column = getColumnFromFieldName(filter.getFieldName(), definition.getColumns());
			if (!isAggregate(column)) {
				String filterExp = toFilterSql(filter, availableFields);
				where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
				whereIndex++;
			}
		}
		sql.addWhere(where);

		for (Filter filter : havingFilters) {
			Column column = getColumnFromFieldName(filter.getFieldName(), definition.getColumns());
			if (isAggregate(column)) {
				String filterExp = toFilterSql(filter, availableFields);
				sql.addHaving(filterExp);
			}
		}
	}

	private String toFilterSql(Filter filter, Map<String, Field> availableFields) {
		if (!filter.isValid())
			return "true";

		Column column = getColumnFromFieldName(filter.getFieldName(), definition.getColumns());

		if (column == null) {
			column = new Column(filter.getFieldName());
		}

		String columnSql = toColumnSql(column, availableFields);

		if (filter.getOperator().equals(QueryFilterOperator.Empty)) {
			return columnSql + " IS NULL OR " + columnSql + " = ''";
		} else if (filter.getOperator().equals(QueryFilterOperator.NotEmpty)) {
			return columnSql + " IS NOT NULL OR " + columnSql + " != ''";
		}

		String operand = filter.getOperator().getOperand();
		String valueSql = toValueSql(filter, column, availableFields);

		return columnSql + " " + operand + " " + valueSql;
	}

	private String toColumnSql(Column column, Map<String, Field> availableFields) {
		String columnSQL = columnToSql(column, availableFields);

		if (column.getFieldName().equals("accountName"))
			columnSQL = "a.nameIndex";

		return columnSQL;
	}

	private String toValueSql(Filter filter, Column column, Map<String, Field> availableFields) {
		String filterValue = Strings.escapeQuotes(filter.getValue());

		// date filter
		Field field = availableFields.get(column.getFieldName().toUpperCase());
		ExtFieldType fieldType = field.getType();
		if (fieldType.equals(ExtFieldType.Date) && column.getFunction() == null) {
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

		return "'" + filterValue + "'";
	}

	private String addQuotesToValues(String unquotedValuesString) {
		String[] unquotedValues = unquotedValuesString.split(",");
		List<String> quotedList = new ArrayList<String>();

		for (String unquotedValue : unquotedValues){
			quotedList.add("'" + unquotedValue.trim() + "'");
		}

		return StringUtils.join(quotedList.toArray(),",");
	}

	private void addOrderByClauses(AbstractModel model, Map<String, Field> availableFields) {
		if (definition.getSorts().isEmpty()) {
			if (usesGroupBy(availableFields))
				return;

			sql.addOrderBy(model.getDefaultSort());
			return;
		}

		for (Sort sort : definition.getSorts()) {
			String fieldName = sort.getFieldName();

			Column column = getColumnFromFieldName(fieldName, definition.getColumns());
			if (column == null) {
				Field field = availableFields.get(fieldName.toUpperCase());
				if (field != null && field.getDatabaseColumnName() != null)
					fieldName = field.getDatabaseColumnName();
			}

			if (!sort.isAscending())
				fieldName += " DESC";

			sql.addOrderBy(fieldName);
			Field field = availableFields.get(sort.getFieldName().toUpperCase());
//			sort.setField(getFieldFromFieldName(sort.getFieldName()));
			sort.setField(field);
		}
	}

	// Setters

	@Deprecated
	public Definition getDefinition() {
		return definition;
	}

	@Deprecated
	public void setDefinition(Definition definition) {
		this.definition = definition;
	}
}
