package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.tables.AbstractTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SqlBuilder {
	private Definition definition = new Definition();
	private SelectSQL sql;
	private Map<String, Field> availableFields;
	
	private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);

	public SelectSQL initializeSql(AbstractModel model, Definition definition, Permissions permissions)
			throws ReportValidationException {
		logger.info("Starting SqlBuilder for " + model);
		this.definition = definition;

		sql = new SelectSQL();

		setFrom(model);

		availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		addFieldsAndGroupBy(definition.getColumns());
		addRuntimeFilters(permissions);
		addOrderByClauses(model);

		addJoins(model.getRootTable());

		sql.addWhere(model.getWhereClause(permissions, definition.getFilters()));
		
		logger.debug("SQL: " + sql);
		logger.info("Completed SqlBuilder");
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
				sql.addJoin(joinTable.getJoinSql());
				addJoins(joinTable);
			}
		}
	}

	private void addFieldsAndGroupBy(List<Column> columns) {
		boolean usesGroupBy = usesGroupBy();

		// Make sure column has a field(?)
		for (Column column : columns) {
			Set<String> dependentFields = new HashSet<String>();

			column.setMethodToFieldName();
			
			column.addFieldCopy(availableFields);

			if (column.getField() == null)
				continue;

			if (!column.isHasAggregateMethod()) {
				// For example: Don't add in accountID automatically if
				// contractorName uses an aggregation like COUNT
				dependentFields.addAll(column.getField().getDependentFields());
			}

			String columnSql = column.getSql();
			if (usesGroupBy && !column.isHasAggregateMethod()) {
				sql.addGroupBy(columnSql);
			}

			sql.addField(columnSql + " AS `" + column.getFieldName() + "`");

			addDependentFields(dependentFields);
		}
	}

	private void addDependentFields(Set<String> dependentFields) {
		for (String fieldName : dependentFields) {
			if (isFieldIncluded(fieldName))
				continue;

			Column column = new Column(fieldName);
			column.addFieldCopy(availableFields);
			String columnSql = column.getSql();
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
				if (column.isHasAggregateMethod()) {
					return true;
				}
			}
		}
		return false;
	}

	private void addRuntimeFilters(Permissions permissions) throws ReportValidationException {
		if (definition.getFilters().isEmpty())
			return;

		List<Filter> whereFilters = new ArrayList<Filter>();
		List<Filter> havingFilters = new ArrayList<Filter>();

		for (Filter filter : definition.getFilters()) {
			filter.addFieldCopy(availableFields);
			
			if (filter.isValid()) {
				filter.updateCurrentUser(permissions);
				if (filter.isHasAggregateMethod()) {
					havingFilters.add(filter);
				} else {
					whereFilters.add(filter);
				}
			}
		}

		sql.addWhere(createWhereClause(whereFilters));

		for (Filter filter : havingFilters) {
			String filterExp = filter.getSqlForFilter();
			sql.addHaving(filterExp);
		}
	}

	private String createWhereClause(List<Filter> whereFilters) throws ReportValidationException {
		String where = definition.getFilterExpression();
		if (where == null || Strings.isEmpty(where)) {
			where = getDefaultFilterExpression(whereFilters);
		}

		int whereIndex = 0;
		for (Filter filter : whereFilters) {
			String filterExp = filter.getSqlForFilter();
			where = where.replace("{" + whereIndex + "}", "(" + filterExp + ")");
			whereIndex++;
		}

		if (where.contains("{")) {
			// TODO Create a new Exception call ReportFilterExpression extends
			throw new ReportValidationException("DynamicReports.FilterExpressionInvalid");
		}
		return where;
	}

	private String getDefaultFilterExpression(List<Filter> whereFilters) {
		String where = "";
		for (int i = 0; i < whereFilters.size(); i++) {
			where += "{" + i + "} AND ";
		}
		return StringUtils.removeEnd(where, " AND ");
	}

	private void addOrderByClauses(AbstractModel model) {
		if (definition.getSorts().isEmpty()) {
			if (usesGroupBy())
				return;

			sql.addOrderBy(model.getDefaultSort());
			return;
		}

		for (Sort sort : definition.getSorts()) {
			sort.addFieldCopy(availableFields);
			
			String fieldName;
			Column column = getColumnFromFieldName(sort.getFieldName());
			if (column == null) {
				Field field = sort.getField();
				if (field != null && field.getDatabaseColumnName() != null)
					fieldName = field.getDatabaseColumnName();
				else
					continue;
				sort.setField(field);
			} else {
				fieldName = column.getFieldName();
			}

			if (!sort.isAscending())
				fieldName += " DESC";

			sql.addOrderBy(fieldName);
		}
	}

	private Column getColumnFromFieldName(String fieldName) {
		for (Column column : definition.getColumns()) {
			if (column.getFieldName().equals(fieldName))
				return column;
		}

		return null;
	}
}
