package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SimpleReportColumn;
import com.picsauditing.report.fields.SimpleReportFilter;
import com.picsauditing.report.fields.SimpleReportSort;
import com.picsauditing.report.models.ModelBase;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.tables.BaseTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SqlBuilder {
	private ModelBase base;
	private List<SimpleReportColumn> includedColumns = new ArrayList<SimpleReportColumn>();
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	private SimpleReportDefinition definition = new SimpleReportDefinition();
	private SelectSQL sql;

	public SelectSQL getSql() {
		sql = new SelectSQL();
		includedColumns.clear();
		availableFields.clear();

		setFrom();
		addAvailableFields(base.getFrom());

		if (definition.getColumns().size() == 0)
			addDefaultFields();
		addFieldsAndGroupBy();
		addRuntimeFilters();
		addOrderBy();

		addJoins(base.getFrom());

		return sql;
	}

	private void addDefaultFields() {
		for (QueryField field : availableFields.values()) {
			if (field.isSuggested())
				definition.getColumns().add(new SimpleReportColumn(field.getDataIndex()));
		}
		if (definition.getColumns().size() == 0)
			throw new RuntimeException("No columns exist for Report or BaseModel");
	}

	private void setFrom() {
		String from = base.getFrom().getTable();
		if (!Strings.isEmpty(base.getFrom().getAlias()))
			from += " AS " + base.getFrom().getAlias();
		sql.setFromTable(from);
	}

	private void addAvailableFields(BaseTable table) {
		// We may be able to use the ModelBase.getAvailableFields...
		availableFields.putAll(table.getFields());
		for (BaseTable join : table.getJoins()) {
			addAvailableFields(join);
		}
	}

	private void addJoins(BaseTable table) {
		for (BaseTable join : table.getJoins()) {
			// System.out.println("Adding join " + join.getTable() + " " + join.getAlias());
			if (isJoinNeeded(join)) {
				String joinSyntax = "";
				if (!join.isInnerJoin())
					joinSyntax += "LEFT ";
				joinSyntax += "JOIN " + join.getTable();
				if (!Strings.isEmpty(join.getAlias()))
					joinSyntax += " AS " + join.getAlias();
				joinSyntax += " ON " + join.getWhere();
				sql.addJoin(joinSyntax);
			}
		}
	}

	private boolean isJoinNeeded(BaseTable table) {
		if (table.isInnerJoin())
			return true;

		for (BaseTable join : table.getJoins()) {
			if (isJoinNeeded(join))
				return true;
		}

		for (QueryField field : table.getFields().values()) {
			for (SimpleReportColumn column : includedColumns) {
				if (column.getAvailableFieldName().equals(field.getDataIndex()))
					return true;
			}
		}

		return false;
	}

	private void addFieldsAndGroupBy() {
		boolean usesGroupBy = usesGroupBy();
		for (SimpleReportColumn column : definition.getColumns()) {
			if (isColumnRequestedValid(column)) {
				String columnSQL = columnToSQL(column);
				if (usesGroupBy && !isAggregate(column.getName())) {
					sql.addGroupBy(columnSQL);
				}
				sql.addField(columnSQL + " AS `" + column.getName() + "`");
				includedColumns.add(column);
			}
		}
	}

	private boolean usesGroupBy() {
		for (SimpleReportColumn column : definition.getColumns()) {
			if (isColumnRequestedValid(column)) {
				if (isAggregate(column.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isColumnRequestedValid(SimpleReportColumn column) {
		return getQueryFieldFromSimpleColumn(column) != null;
	}

	private QueryField getQueryFieldFromSimpleColumn(SimpleReportColumn column) {
		return availableFields.get(column.getAvailableFieldName().toUpperCase());
	}

	private boolean isAggregate(String columnName) {
		SimpleReportColumn column = convertColumn(columnName);
		if (column == null)
			return false;
		if (column.getFunction() == null)
			return false;

		return column.getFunction().isAggregate();
	}

	private String columnToSQL(SimpleReportColumn column) {
		String fieldSQL = getQueryFieldFromSimpleColumn(column).getSql();
		if (column.getFunction() == null)
			return fieldSQL;
		switch (column.getFunction()) {
		case Average:
			return "AVG(" + fieldSQL + ")";
		case Count:
			return "COUNT(" + fieldSQL + ")";
		case CountDistinct:
			return "COUNT(DISTINCT " + fieldSQL + ")";
		case Date:
			return "DATE(" + fieldSQL + ")";
		case LowerCase:
			return "LOWER(" + fieldSQL + ")";
		case Max:
			return "MAX(" + fieldSQL + ")";
		case Min:
			return "MIN(" + fieldSQL + ")";
		case Month:
			return "MONTH(" + fieldSQL + ")";
		case Round:
			return "ROUND(" + fieldSQL + ")";
		case Sum:
			return "SUM(" + fieldSQL + ")";
		case UpperCase:
			return "UPPER(" + fieldSQL + ")";
		case Year:
			return "YEAR(" + fieldSQL + ")";
		}
		return fieldSQL;
	}

	private void addRuntimeFilters() {
		if (definition.getFilters().size() == 0) {
			return;
		}

		String where = definition.getFilterExpression();
		if (where == null || Strings.isEmpty(where)) {
			where = "";
			for (int i = 0; i < definition.getFilters().size(); i++) {
				where += "{" + i + "} AND ";
			}
			where = StringUtils.removeEnd(where, " AND ");
		}

		for (int i = 0; i < definition.getFilters().size(); i++) {
			SimpleReportFilter queryFilter = definition.getFilters().get(i);

			if (queryFilter.getOperator().equals(QueryFilterOperator.InReport)) {
				// TODO Replace InReport filters out before hand with a SelectSQL object so we don't have to use a DAO
				// here

				// Report subReport = dao.find(Report.class, Integer.parseInt(queryFilter.getValue()));
				// QueryRunner subRunner = new QueryRunner(subReport);
				// SelectSQL subSql = subRunner.buildQueryWithoutLimits();
				// queryFilter.setValue(subSql.toString());
			}

			String filterExp = toFilterSql(queryFilter);
			where = where.replace("{" + i + "}", "(" + filterExp + ")");
		}
		sql.addWhere(where);

		for (SimpleReportFilter filter : definition.getFilters()) {
			if (isAggregate(filter.getColumn()) || isAggregate(filter.getColumn2())) {
				// TODO convert to SQL
				String having = "";
				sql.addHaving(having);
			}
		}
	}

	private SimpleReportColumn convertColumn(String columnName) {
		if (columnName == null)
			return null;
		for (SimpleReportColumn column : definition.getColumns()) {
			if (column.getName().equals(columnName))
				return column;
		}
		return null;
	}

	private String toFilterSql(SimpleReportFilter filter) {

		SimpleReportColumn column = convertColumn(filter.getColumn());

		String columnSQL = toColumnSql(column);
		String valueSql = toValueSql(filter, column);

		return columnSQL + " " + filter.getOperator().getOperand() + " " + valueSql;
	}

	private String toColumnSql(SimpleReportColumn column) {
		String columnSQL = columnToSQL(column);

		if (column.getName().equals("accountName"))
			columnSQL = "a.nameIndex";
		return columnSQL;
	}

	private String toValueSql(SimpleReportFilter filter, SimpleReportColumn column) {
		if (filter.getColumn2() != null) {
			return columnToSQL(convertColumn(filter.getColumn2()));
		}

		if (getQueryFieldFromSimpleColumn(column).getType().equals(ExtFieldType.Date) && column.getFunction() == null) {
			QueryDateParameter parameter = new QueryDateParameter(filter.getValue());
			return DateBean.toDBFormat(parameter.getTime());
		}

		String value = filter.getValue();
		switch (filter.getOperator()) {
		case BeginsWith:
			return "'" + value + "%'";
		case EndsWith:
			return "'%" + value + "'";
		case Contains:
			return "'%" + value + "%'";
		case In:
		case InReport:
			// this only supports numbers, no strings or dates
			return "(" + value + ")";
		case Empty:
			// TODO
		}
		return "'" + value + "'";
	}

	private void addOrderBy() {
		if (definition.getOrderBy().size() == 0) {
			sql.addOrderBy(base.getDefaultSort());
			return;
		}

		for (SimpleReportSort sort : definition.getOrderBy()) {

			String orderBy = sort.getColumn();
			SimpleReportColumn column = getColumn(sort.getColumn());
			if (column == null) {
				QueryField field = availableFields.get(sort.getColumn().toUpperCase());
				if (field != null && field.getSql() != null)
					orderBy = field.getSql();
			}

			if (!sort.isAscending())
				orderBy += " DESC";
			sql.addOrderBy(orderBy);
		}
	}

	public void addPermissions(Permissions permissions) {
		String where = this.base.getWhereClause(permissions);
		sql.addWhere(where);
	}

	public void addPaging(int page) {
		if (page > 1)
			sql.setStartRow((page - 1) * definition.getRowsPerPage());
		sql.setLimit(definition.getRowsPerPage());
		sql.setSQL_CALC_FOUND_ROWS(true);
	}

	private SimpleReportColumn getColumn(String name) {
		for (SimpleReportColumn column : includedColumns) {
			if (column.getName().equals(name))
				return column;
		}
		return null;
	}

	// Setters

	public void setReport(Report report) {
		this.base = ModelFactory.getBase(report.getModelType());
	}

	public void setBase(ModelBase base) {
		this.base = base;
	}

	public SimpleReportDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(SimpleReportDefinition definition) {
		this.definition = definition;
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}

	public List<SimpleReportColumn> getIncludedColumns() {
		return includedColumns;
	}
}
