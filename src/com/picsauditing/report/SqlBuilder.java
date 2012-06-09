package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.BaseModel;
import com.picsauditing.report.tables.BaseTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;
import com.picsauditing.util.excel.ExcelSheet;

public class SqlBuilder {

//	private BaseModel baseModel;
	private Map<String, Field> availableFields = new TreeMap<String, Field>();
	private Definition definition = new Definition();
	private SelectSQL sql;

	public SelectSQL buildSql(Report report, Permissions permissions, int pageNumber) throws Exception {
		return buildSql(report, permissions, pageNumber, false);
	}

	public SelectSQL buildSql(Report report, Permissions permissions, int pageNumber, boolean forDownload) throws Exception {
		BaseModel baseModel = report.getBaseModel();
		sql = initializeSql(baseModel);

		sql.addWhere(baseModel.getWhereClause(permissions));

		if (!forDownload) {
			int rowsPerPage = report.getRowsPerPage();

			if (pageNumber > 1)
				sql.setStartRow((pageNumber - 1) * rowsPerPage);

			sql.setLimit(rowsPerPage);
			sql.setSQL_CALC_FOUND_ROWS(true);
		}

		return sql;
	}

	// TODO change this to pass in a report
	public SelectSQL initializeSql(BaseModel baseModel) {
		sql = new SelectSQL();

		setFrom(baseModel);

		availableFields.clear();
		addAvailableFields(baseModel.getPrimaryTable());

		addFieldsAndGroupBy();
		addRuntimeFilters();
		addOrderByClauses(baseModel);

		addJoins(baseModel.getPrimaryTable());

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

	private void setFrom(BaseModel baseModel) {
		String from = baseModel.getPrimaryTable().getTableName();
		String alias = baseModel.getPrimaryTable().getAlias();
		if (!Strings.isEmpty(alias))
			from += " AS " + alias;

		sql.setFromTable(from);
	}

	private void addAvailableFields(BaseTable table) {
		// We may be able to use the ModelBase.getAvailableFields...
		availableFields.putAll(table.getAvailableFields());
		for (BaseTable joinTable : table.getJoins()) {
			addAvailableFields(joinTable);
		}
	}

	private void addJoins(BaseTable table) {
		for (BaseTable joinTable : table.getJoins()) {
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

	private void addFieldsAndGroupBy() {
		Set<String> dependentFields = new HashSet<String>();
		boolean usesGroupBy = usesGroupBy();
		for (Column column : definition.getColumns()) {
			Field field = getFieldFromFieldName(column.getFieldName());
			if (field != null) {
				if (column.getFunction() == null || !column.getFunction().isAggregate()) {
					// For example: Don't add in accountID automatically if
					// contractorName uses an aggregation like COUNT
					dependentFields.addAll(field.getDependentFields());
				}

				String columnSql = columnToSql(column);
				if (usesGroupBy && !isAggregate(column.getFieldName())) {
					sql.addGroupBy(columnSql);
				}

				sql.addField(columnSql + " AS `" + column.getFieldName() + "`");
				column.setField(field);
			}
		}

		// Add an dependent fields that aren't already included
		Iterator<String> iterator = dependentFields.iterator();
		while (iterator.hasNext()) {
			String fieldName = (String) iterator.next();
			if (isFieldIncluded(fieldName)) {
				iterator.remove();
			}
		}

		for (String fieldName : dependentFields) {
			Column column = new Column(fieldName);
			String columnSql = columnToSql(column);
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

	private boolean usesGroupBy() {
		for (Column column : definition.getColumns()) {
			if (getFieldFromFieldName(column.getFieldName()) != null) {
				if (isAggregate(column.getFieldName())) {
					return true;
				}
			}
		}
		return false;
	}

	private Field getFieldFromFieldName(String fieldName) {
		return availableFields.get(fieldName.toUpperCase());
	}

	private boolean isAggregate(String columnName) {
		if (columnName == null)
			return false;

		Column column = convertColumn(columnName);
		if (column == null)
			return false;

		if (column.getFunction() == null)
			return false;

		return column.getFunction().isAggregate();
	}

	private String columnToSql(Column column) {
		Field field = getFieldFromFieldName(column.getFieldName());
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

	private void addRuntimeFilters() {
		if (definition.getFilters().isEmpty())
			return;

		Set<Filter> whereFilters = new HashSet<Filter>();
		Set<Filter> havingFilters = new HashSet<Filter>();

		for (Filter filter : definition.getFilters()) {
			// TODO we might want to verify the filter is properly defined
			// before including it
			// if (filter.isFullyDefined()) { }
			if (isAggregate(filter.getFieldName())) {
				havingFilters.add(filter);
			} else {
				whereFilters.add(filter);
			}

			filter.setField(getFieldFromFieldName(filter.getFieldName()));
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
			if (!isAggregate(filter.getFieldName())) {
				String filterExp = toFilterSql(filter);
				where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
				whereIndex++;
			}
		}
		sql.addWhere(where);

		for (Filter filter : havingFilters) {
			if (isAggregate(filter.getFieldName())) {
				String filterExp = toFilterSql(filter);
				sql.addHaving(filterExp);
			}
		}
	}

	private Column convertColumn(String columnName) {
		if (columnName == null)
			return null;

		for (Column column : definition.getColumns()) {
			if (column.getFieldName().equals(columnName))
				return column;
		}

		return null;
	}

	private String toFilterSql(Filter filter) {
		if (!filter.isValid())
			return "true";

		Column column = convertColumn(filter.getFieldName());

		if (column == null) {
			column = new Column(filter.getFieldName());
		}

		String columnSql = toColumnSql(column);

		if (filter.getOperator().equals(QueryFilterOperator.Empty)) {
			return columnSql + " IS NULL OR " + columnSql + " = ''";
		} else if (filter.getOperator().equals(QueryFilterOperator.NotEmpty)) {
			return columnSql + " IS NOT NULL OR " + columnSql + " != ''";
		}

		String operand = filter.getOperator().getOperand();
		String valueSql = toValueSql(filter, column);

		return columnSql + " " + operand + " " + valueSql;
	}

	private String toColumnSql(Column column) {
		String columnSQL = columnToSql(column);

		if (column.getFieldName().equals("accountName"))
			columnSQL = "a.nameIndex";

		return columnSQL;
	}

	private String toValueSql(Filter filter, Column column) {
		String value = filter.getValue();

		// date filter
		ExtFieldType fieldType = getFieldFromFieldName(column.getFieldName()).getType();
		if (fieldType.equals(ExtFieldType.Date) && column.getFunction() == null) {
			QueryDateParameter parameter = new QueryDateParameter(value);

			value = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()), "");
		}

		switch (filter.getOperator()) {
		case NotBeginsWith:
		case BeginsWith:
			return "'" + value + "%'";
		case NotEndsWith:
		case EndsWith:
			return "'%" + value + "'";
		case NotContains:
		case Contains:
			return "'%" + value + "%'";
		case NotIn:
		case In:
			value = addQuotesToValues(value);
			return "(" + value + ")";
		case NotEmpty:
		case Empty:
			// TODO
		}

		return "'" + value + "'";
	}

	private String addQuotesToValues(String value) {
		String[] values = value.split(",");
		List<String> quotedList = new ArrayList<String>();
		for(String individualValue : values){
			individualValue.trim();
			individualValue = "'"+individualValue+"'";
			quotedList.add(individualValue);
		}
		value = StringUtils.join(quotedList.toArray(),",");
		return value;
	}

	private void addOrderByClauses(BaseModel baseModel) {
		if (definition.getSorts().isEmpty()) {
			if (usesGroupBy())
				return;

			sql.addOrderBy(baseModel.getDefaultSort());
			return;
		}

		for (Sort sort : definition.getSorts()) {
			String fieldName = sort.getFieldName();

			Column column = getColumnFromFieldName(fieldName);
			if (column == null) {
				Field field = availableFields.get(fieldName.toUpperCase());
				if (field != null && field.getDatabaseColumnName() != null)
					fieldName = field.getDatabaseColumnName();
			}

			if (!sort.isAscending())
				fieldName += " DESC";

			sql.addOrderBy(fieldName);
			sort.setField(getFieldFromFieldName(sort.getFieldName()));
		}
	}

//	public void addPermissions(Permissions permissions) {
//		String where = this.baseModel.getWhereClause(permissions);
//		sql.addWhere(where);
//	}

//	public void setPaging(int page, int rowsPerPage) {
//		if (page > 1)
//			sql.setStartRow((page - 1) * rowsPerPage);
//
//		sql.setLimit(rowsPerPage);
//		sql.setSQL_CALC_FOUND_ROWS(true);
//	}

	private Column getColumnFromFieldName(String fieldName) {
		for (Column column : definition.getColumns()) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}

		return null;
	}

	// Setters

//	public void setBaseModelFromReport(Report report) {
//		this.baseModel = ModelFactory.getBase(report.getModelType());
//	}
//
//	public void setBase(BaseModel base) {
//		this.baseModel = base;
//	}

	@Deprecated
	public Definition getDefinition() {
		return definition;
	}

	@Deprecated
	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	public Map<String, Field> getAvailableFields() {
		return availableFields;
	}
}
