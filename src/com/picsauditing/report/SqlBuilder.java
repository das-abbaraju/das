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
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.Column;
import com.picsauditing.report.fields.Filter;
import com.picsauditing.report.fields.Sort;
import com.picsauditing.report.models.ModelBase;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.tables.BaseReportTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SqlBuilder {
	private ModelBase base;
	private Map<String, Field> availableFields = new TreeMap<String, Field>();
	private Definition definition = new Definition();
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

		for (Field field : table.getFields().values()) {
			for (Column column : definition.getColumns()) {
				if (column.getAvailableFieldName().equals(field.getName()))
					return true;
			}
			for (Filter filter : definition.getFilters()) {
				if (filter.getName().equals(field.getName()))
					return true;
			}
		}

		return false;
	}

	private void addFieldsAndGroupBy() {
		Set<String> dependentFields = new HashSet<String>();
		boolean usesGroupBy = usesGroupBy();
		for (Column column : definition.getColumns()) {
			Field field = getQueryFieldFromReportColumn(column);
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
			Column column = new Column(fieldName);
			String columnSQL = columnToSQL(column);
			sql.addField(columnSQL + " AS `" + fieldName + "`");
		}
	}

	private boolean isFieldIncluded(String fieldName) {
		for (Column column : definition.getColumns()) {
			if (column.getName().equals(fieldName))
				return true;
		}
		return false;
	}

	private boolean usesGroupBy() {
		for (Column column : definition.getColumns()) {
			if (getQueryFieldFromReportColumn(column) != null) {
				if (isAggregate(column.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private Field getQueryFieldFromReportColumn(Column column) {
		return availableFields.get(column.getAvailableFieldName().toUpperCase());
	}

	private Field getQueryFieldFromReportFilter(Filter filter) {
		return availableFields.get(filter.getName().toUpperCase());
	}

	private Field getQueryFieldFromReportSort(Sort sort) {
		return availableFields.get(sort.getName().toUpperCase());
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

	private String columnToSQL(Column column) {
		Field field = getQueryFieldFromReportColumn(column);
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
		
		Set<Filter> whereFilters = new HashSet<Filter>();
		Set<Filter> havingFilters = new HashSet<Filter>();
		
		for (Filter filter : definition.getFilters()) {
			// TODO we might want to verify the filter is properly defined before including it
			// if (filter.isFullyDefined()) { }
			if (isAggregate(filter.getName())) {
				havingFilters.add(filter);
				filter.setField(getQueryFieldFromReportFilter(filter));
			} else {
				whereFilters.add(filter);
				filter.setField(getQueryFieldFromReportFilter(filter));
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
		for (Filter filter : whereFilters) {
			if (!isAggregate(filter.getName())) {
				String filterExp = toFilterSql(filter);
				where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
				whereIndex++;
			}
		}
		sql.addWhere(where);

		for (Filter filter : havingFilters) {
			if (isAggregate(filter.getName())) {
				String filterExp = toFilterSql(filter);
				sql.addHaving(filterExp);
			}
		}
	}

	private Column convertColumn(String columnName) {
		if (columnName == null)
			return null;
		for (Column column : definition.getColumns()) {
			if (column.getName().equals(columnName))
				return column;
		}
		return null;
	}

	private String toFilterSql(Filter filter) {
		if (!filter.isValid())
			return "true";

		Column column = convertColumn(filter.getName());

		if (column == null) {
			column = new Column(filter.getName());
		}

		String columnSQL = toColumnSql(column);
		String valueSql = toValueSql(filter, column);
		
		String operand = filter.getOperator().getOperand();
		
		if (filter.getOperator().equals(QueryFilterOperator.Empty)) {
			return columnSQL + " IS NULL OR " + columnSQL + " = ''";
		}
		else if (filter.getOperator().equals(QueryFilterOperator.Empty)) {
			return columnSQL + " NOT IS NULL OR " + columnSQL + " != ''";
		}
		
		return columnSQL + " " + operand + " " + valueSql;
	}

	private String toColumnSql(Column column) {
		String columnSQL = columnToSQL(column);

		if (column.getName().equals("accountName"))
			columnSQL = "a.nameIndex";
		return columnSQL;
	}

	private String toValueSql(Filter filter, Column column) {
		String value = filter.getValue();

		// date filter
		if (getQueryFieldFromReportColumn(column).getType().equals(ExtFieldType.Date) && column.getFunction() == null) {
			QueryDateParameter parameter = new QueryDateParameter(value);
			
			value = StringUtils.defaultIfEmpty(DateBean.toDBFormat(parameter.getTime()),"");
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
    			// TODO this only supports numbers, no strings or dates, change it so that it does support more. 
    			return "(" + value + ")";
    		case NotEmpty:
    		case Empty:
    			// TODO
		}
		
		return "'" + value + "'";
	}

	private void addOrderBy() {
		if (definition.getSorts().size() == 0) {
			if (usesGroupBy()) {
				return;
			}
			sql.addOrderBy(base.getDefaultSort());
			return;
		}

		for (Sort sort : definition.getSorts()) {

			String orderBy = sort.getName();
			Column column = getName(sort.getName());
			if (column == null) {
				Field field = availableFields.get(sort.getName().toUpperCase());
				if (field != null && field.getSql() != null)
					orderBy = field.getSql();
			}

			if (!sort.isAscending())
				orderBy += " DESC";
			sql.addOrderBy(orderBy);
			sort.setField(getQueryFieldFromReportSort(sort));
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

	private Column getName(String name) {
		for (Column column : definition.getColumns()) {
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

	public Definition getDefinition() {
		return definition;
	}

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	public Map<String, Field> getAvailableFields() {
		return availableFields;
	}
}
