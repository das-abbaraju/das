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

	private ModelBase baseModel;
	private Map<String, Field> availableFields = new TreeMap<String, Field>();
	private Definition definition = new Definition();
	private SelectSQL sql;

	public SelectSQL getSql() {
		sql = new SelectSQL();
		availableFields.clear();

		if (baseModel == null)
			return sql;
		
		setFrom();
		addAvailableFields(baseModel.getFrom());

		addFieldsAndGroupBy();
		addRuntimeFilters();
		addOrderByClauses();

		addJoins(baseModel.getFrom());

		return sql;
	}

	private void setFrom() {
		String from = baseModel.getFrom().getTable();
		if (!Strings.isEmpty(baseModel.getFrom().getAlias()))
			from += " AS " + baseModel.getFrom().getAlias();

		sql.setFromTable(from);
	}

	private void addAvailableFields(BaseReportTable table) {
		// We may be able to use the ModelBase.getAvailableFields...
		availableFields.putAll(table.getAvailableFieldsMap());
		for (BaseReportTable joinTable : table.getJoins()) {
			addAvailableFields(joinTable);
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

		for (BaseReportTable joinTable : table.getJoins()) {
			if (isJoinNeeded(joinTable))
				return true;
		}

		for (Field field : table.getAvailableFieldsMap().values()) {
			for (Column column : definition.getColumns()) {
				if (column.getAvailableFieldName().equals(field.getName()))
					return true;
			}
			for (Filter filter : definition.getFilters()) {
				if (filter.getFieldName().equals(field.getName()))
					return true;
			}
		}

		return false;
	}

	private void addFieldsAndGroupBy() {
		Set<String> dependentFields = new HashSet<String>();
		boolean usesGroupBy = usesGroupBy();
		for (Column column : definition.getColumns()) {
			Field field = getFieldFromFieldName(column.getFieldName());
			if (field != null) {
				if (column.getFunction() == null || !column.getFunction().isAggregate()) {
					// For example: Don't add in accountID automatically if contractorName uses an aggregation like COUNT
					dependentFields.addAll(field.getDependentFields());
				}

				String columnSQL = columnToSql(column);
				if (usesGroupBy && !isAggregate(column.getFieldName())) {
					sql.addGroupBy(columnSQL);
				}

				sql.addField(columnSQL + " AS `" + column.getFieldName() + "`");
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
			String columnSQL = columnToSql(column);
			sql.addField(columnSQL + " AS `" + fieldName + "`");
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
		if (definition.getFilters().size() == 0) {
			return;
		}
		
		Set<Filter> whereFilters = new HashSet<Filter>();
		Set<Filter> havingFilters = new HashSet<Filter>();
		
		for (Filter filter : definition.getFilters()) {
			// TODO we might want to verify the filter is properly defined before including it
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

	private void addOrderByClauses() {
		if (definition.getSorts().size() == 0) {
			if (usesGroupBy()) {
				return;
			}
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

	public void addPermissions(Permissions permissions) {
		String where = this.baseModel.getWhereClause(permissions);
		sql.addWhere(where);
	}

	public void addPaging(int page) {
		if (page > 1)
			sql.setStartRow((page - 1) * definition.getRowsPerPage());
		sql.setLimit(definition.getRowsPerPage());
		sql.setSQL_CALC_FOUND_ROWS(true);
	}

	private Column getColumnFromFieldName(String fieldName) {
		for (Column column : definition.getColumns()) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}
		return null;
	}

	// Setters

	public ModelBase setReport(Report report) {
		this.baseModel = ModelFactory.getBase(report.getModelType());
		return this.baseModel;
	}

	public void setBase(ModelBase base) {
		this.baseModel = base;
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
