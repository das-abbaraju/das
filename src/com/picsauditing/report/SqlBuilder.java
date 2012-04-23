package com.picsauditing.report;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.ExtFieldType;
import com.picsauditing.report.fields.QueryDateParameter;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.ReportColumn;
import com.picsauditing.report.fields.ReportFilter;
import com.picsauditing.report.fields.ReportSort;
import com.picsauditing.report.models.ModelBase;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.tables.BaseReportTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SqlBuilder {
	private ModelBase base;
	private Map<String, QueryField> availableFields = new TreeMap<String, QueryField>();
	private ReportDefinition definition = new ReportDefinition();
	private SelectSQL sql;

	public SelectSQL getSql() {
		sql = new SelectSQL();
		availableFields.clear();

		if (base == null)
			return sql;
		
		setFrom();
		addAvailableFields(base.getFrom());

		addFieldsAndGroupBy();
		addRuntimeFilters();
		addOrderBy();

		addJoins(base.getFrom());

		return sql;
	}

	private void setFrom() {
		String from = base.getFrom().getTable();
		if (!Strings.isEmpty(base.getFrom().getAlias()))
			from += " AS " + base.getFrom().getAlias();
		sql.setFromTable(from);
	}

	private void addAvailableFields(BaseReportTable table) {
		// We may be able to use the ModelBase.getAvailableFields...
		availableFields.putAll(table.getFields());
		for (BaseReportTable join : table.getJoins()) {
			addAvailableFields(join);
		}
	}

	private void addJoins(BaseReportTable table) {
		for (BaseReportTable join : table.getJoins()) {
			if (isJoinNeeded(join)) {
				String joinSyntax = "";
				if (!join.isInnerJoin())
					joinSyntax += "LEFT ";
				joinSyntax += "JOIN " + join.getTable();
				if (!Strings.isEmpty(join.getAlias()))
					joinSyntax += " AS " + join.getAlias();
				joinSyntax += " ON " + join.getWhere();
				sql.addJoin(joinSyntax);
				addJoins(join);
			}
		}
	}

	private boolean isJoinNeeded(BaseReportTable table) {
		if (table.isInnerJoin())
			return true;

		for (BaseReportTable join : table.getJoins()) {
			if (isJoinNeeded(join))
				return true;
		}

		for (QueryField field : table.getFields().values()) {
			for (ReportColumn column : definition.getColumns()) {
				if (column.getAvailableFieldName().equals(field.getName()))
					return true;
			}
			for (ReportFilter filter : definition.getFilters()) {
				if (filter.getColumn().equals(field.getName()))
					return true;
			}
		}

		return false;
	}

	private void addFieldsAndGroupBy() {
		Set<String> dependentFields = new HashSet<String>();
		boolean usesGroupBy = usesGroupBy();
		for (ReportColumn column : definition.getColumns()) {
			QueryField field = getQueryFieldFromSimpleColumn(column);
			if (field != null) {
				if (column.getFunction() == null || !column.getFunction().isAggregate()) {
					// For example: Don't add in accountID automatically if contractorName uses an aggregation like COUNT
					dependentFields.addAll(field.getDependentFields());
				}
				String columnSQL = columnToSQL(column);
				if (usesGroupBy && !isAggregate(column.getName())) {
					sql.addGroupBy(columnSQL);
				}
				sql.addField(columnSQL + " AS `" + column.getName() + "`");
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
			ReportColumn column = new ReportColumn(fieldName);
			String columnSQL = columnToSQL(column);
			sql.addField(columnSQL + " AS `" + fieldName + "`");
		}
	}

	private boolean isFieldIncluded(String fieldName) {
		for (ReportColumn column : definition.getColumns()) {
			if (column.getName().equals(fieldName))
				return true;
		}
		return false;
	}

	private boolean usesGroupBy() {
		for (ReportColumn column : definition.getColumns()) {
			if (getQueryFieldFromSimpleColumn(column) != null) {
				if (isAggregate(column.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private QueryField getQueryFieldFromSimpleColumn(ReportColumn column) {
		return availableFields.get(column.getAvailableFieldName().toUpperCase());
	}

	private boolean isAggregate(String columnName) {
		if (columnName == null)
			return false;
		ReportColumn column = convertColumn(columnName);
		if (column == null)
			return false;
		if (column.getFunction() == null)
			return false;

		return column.getFunction().isAggregate();
	}

	private String columnToSQL(ReportColumn column) {
		QueryField field = getQueryFieldFromSimpleColumn(column);
		String fieldSQL = field.getSql();
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
		
		Set<ReportFilter> whereFilters = new HashSet<ReportFilter>();
		Set<ReportFilter> havingFilters = new HashSet<ReportFilter>();
		
		for (ReportFilter filter : definition.getFilters()) {
			// TODO we might want to verify the filter is properly defined before including it
			// if (filter.isFullyDefined()) { }
			if (isAggregate(filter.getColumn()) || isAggregate(filter.getColumn2())) {
				havingFilters.add(filter);
			} else {
				whereFilters.add(filter);
			}
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
		for (ReportFilter filter : whereFilters) {
			if (!isAggregate(filter.getColumn()) && !isAggregate(filter.getColumn2())) {
				String filterExp = toFilterSql(filter);
				where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
				whereIndex++;
			}
		}
		sql.addWhere(where);

		for (ReportFilter filter : havingFilters) {
			if (isAggregate(filter.getColumn()) || isAggregate(filter.getColumn2())) {
				String filterExp = toFilterSql(filter);
				sql.addHaving(filterExp);
			}
		}
	}

	private ReportColumn convertColumn(String columnName) {
		if (columnName == null)
			return null;
		for (ReportColumn column : definition.getColumns()) {
			if (column.getName().equals(columnName))
				return column;
		}
		return null;
	}

	private String toFilterSql(ReportFilter filter) {
		if (!filter.isValid())
			return "true";

		ReportColumn column = convertColumn(filter.getColumn());

		if (column == null) {
			column = new ReportColumn(filter.getColumn());
		}

		String columnSQL = toColumnSql(column);
		String valueSql = toValueSql(filter, column);
		
		String operand = filter.getOperator().getOperand();
		
		if (filter.getOperator().equals(QueryFilterOperator.Empty)) {
			if (filter.isNot()) {
				return columnSQL + " NOT IS NULL OR " + columnSQL + " != ''";
			} else {
				return columnSQL + " IS NULL OR " + columnSQL + " = ''";
			}
		}
		
		if (!filter.isNot())
			return columnSQL + " " + operand + " " + valueSql;

		switch (filter.getOperator()) {
		case Equals:
			return columnSQL + " !" + operand + " " + valueSql;
		case GreaterThan:
		case GreaterThanOrEquals:
		case LessThan:
		case LessThanOrEquals:
			return "NOT " + columnSQL + " " + operand + " " + valueSql;
		default:
			return columnSQL + " NOT " + operand + " " + valueSql;
		}
	}

	private String toColumnSql(ReportColumn column) {
		String columnSQL = columnToSQL(column);

		if (column.getName().equals("accountName"))
			columnSQL = "a.nameIndex";
		return columnSQL;
	}

	private String toValueSql(ReportFilter filter, ReportColumn column) {
		if (!Strings.isEmpty(filter.getColumn2())) {
			return columnToSQL(convertColumn(filter.getColumn2()));
		}

		String value = filter.getValue();

		// date filter
		if (getQueryFieldFromSimpleColumn(column).getType().equals(ExtFieldType.Date) && column.getFunction() == null) {
			QueryDateParameter parameter = new QueryDateParameter(value);
			
			value = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()),"");
		}

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
			if (usesGroupBy()) {
				return;
			}
			sql.addOrderBy(base.getDefaultSort());
			return;
		}

		for (ReportSort sort : definition.getOrderBy()) {

			String orderBy = sort.getColumn();
			ReportColumn column = getColumn(sort.getColumn());
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

	private ReportColumn getColumn(String name) {
		for (ReportColumn column : definition.getColumns()) {
			if (column.getName().equals(name))
				return column;
		}
		return null;
	}

	// Setters

	public ModelBase setReport(Report report) {
		this.base = ModelFactory.getBase(report.getModelType());
		return this.base;
	}

	public void setBase(ModelBase base) {
		this.base = base;
	}

	public ReportDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ReportDefinition definition) {
		this.definition = definition;
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}
}
