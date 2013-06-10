package com.picsauditing.report;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ReportModelFactory;
import com.picsauditing.report.models.ReportJoin;
import com.picsauditing.search.SelectSQL;

public class SqlBuilder {

	private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);

	public SelectSQL initializeReportAndBuildSql(Report report, Permissions permissions) throws ReportValidationException {
		// todo: Consider making the model, including it's available fields, a transient property of the report.
		AbstractModel model = ReportModelFactory.build(report.getModelType(), permissions);
		Map<String, Field> availableFields = model.getAvailableFields();
		report = initializeReport(report, availableFields, permissions);
		SelectSQL selectSQL = buildSelectSql(report, availableFields, model);

		return selectSQL;
	}

	private Report initializeReport(Report report, Map<String, Field> availableFields, Permissions permissions) throws ReportValidationException {

		setColumnProperties(report.getColumns(), availableFields);
		setFilterProperties(report.getFilters(), availableFields, permissions);
		setSortProperties(report, availableFields);

		return report;
	}

	private SelectSQL buildSelectSql(Report report, Map<String, Field> availableFields, AbstractModel model)	throws ReportValidationException {
		SelectSQL selectSql = new SelectSQL();
		selectSql.setFromTable(model.getStartingJoin().getTableClause());

		addFieldsAndGroupBy(report.getColumns(), selectSql, availableFields);
		addRuntimeFilters(report, selectSql);
		addOrderByClause(report, selectSql);
		addJoins(model.getStartingJoin(), report, selectSql);
		addWhereClause(model, report, selectSql);

		return selectSql;
	}

	private void addWhereClause(AbstractModel model, Report report, SelectSQL selectSql) {
		List<Filter> filters = report.getFilters();
		String whereClause = model.getWhereClause(filters);
		selectSql.addWhere(whereClause);
	}

	private void addJoins(ReportJoin parentJoin, Report report, SelectSQL selectSql) {
		for (ReportJoin join : parentJoin.getJoins()) {
			if (join.isNeeded(report)) {
				selectSql.addJoin(join.toJoinClause());
				addJoins(join, report, selectSql);
			}
		}
	}

	private void setColumnProperties(List<Column> columns, Map<String, Field> availableFields) {
		for (Column column : columns) {
			column.appendSqlFunctionToName();
			column.addFieldCopy(availableFields);

			if (column.getField() == null) {
				continue;
			}

			column.setUrlOnFieldIfNecessary();
		}
	}
	private void addFieldsAndGroupBy(List<Column> columns, SelectSQL selectSql, Map<String, Field> availableFields) {
		for (Column column : columns) {
			Set<String> dependentFields = new HashSet<String>();

			if (column.getField() == null) {
				continue;
			}

			if (!column.hasAggregateMethod()) {
				dependentFields.addAll(column.getField().getDependentFields());
			}

			String columnSql = column.getSql();
			if (usesGroupBy(columns, availableFields) && !column.hasAggregateMethod()) {
				selectSql.addGroupBy(columnSql);
			}

			selectSql.addField(columnSql + " AS `" + column.getName() + "`");

			addDependentFields(dependentFields, columns, selectSql, availableFields);
		}
	}

	private void addDependentFields(Set<String> dependentFields, List<Column> columns, SelectSQL selectSql, Map<String, Field> availableFields) {
		for (String fieldName : dependentFields) {
			if (isFieldIncluded(fieldName, columns)) {
				continue;
			}

			Column column = new Column(fieldName);
			column.addFieldCopy(availableFields);
			String columnSql = column.getSql();
			selectSql.addField(columnSql + " AS `" + fieldName + "`");
		}
	}

	private boolean isFieldIncluded(String fieldName, List<Column> columns) {
		for (Column column : columns) {
			if (column.getFieldNameWithoutMethod().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	private boolean usesGroupBy(List<Column> columns, Map<String, Field> availableFields) {
		for (Column column : columns) {
			String fieldNameWithoutMethod = column.getFieldNameWithoutMethod();
			if (fieldNameWithoutMethod == null) {
				continue;
			}

			Field field = availableFields.get(fieldNameWithoutMethod.toUpperCase());
			if (field != null) {
				if (column.hasAggregateMethod()) {
					return true;
				}
			}
		}
		return false;
	}

	private void setFilterProperties(List<Filter> filters, Map<String, Field> availableFields, Permissions permissions) throws ReportValidationException {
		if (filters.isEmpty()) {
			return;
		}

		for (Filter filter : filters) {
			filter.addFieldCopy(availableFields);

			if (filter.isValid() || (filter.getValues().isEmpty() && filter.getFieldForComparison() == null)) {
				filter.updateCurrentUser(permissions);
			}
		}
	}

	private void addRuntimeFilters(Report report, SelectSQL selectSql) throws ReportValidationException {
		if (report.getFilters().isEmpty()) {
			return;
		}

		List<Filter> whereFilters = new ArrayList<Filter>();
		List<Filter> havingFilters = new ArrayList<Filter>();

		for (Filter filter : report.getFilters()) {

			// TODO See if this can be safely added to filter.isValid()
			if (filter.isValid() || (filter.getValues().isEmpty() && filter.getFieldForComparison() == null)) {
				if (filter.hasAggregateMethod()) {
					havingFilters.add(filter);
				} else {
					whereFilters.add(filter);
				}
			}
		}

		String filterExpression = report.getFilterExpression();
		String whereClause = FilterExpression.parseWhereClause(filterExpression, whereFilters);
		selectSql.addWhere(whereClause);

		for (Filter filter : havingFilters) {
			String filterExp = filter.getSqlForFilter();
			selectSql.addHaving(filterExp);
		}
	}

	private void setSortProperties(Report report, Map<String, Field> availableFields) {
		if (report.getSorts().isEmpty()) {
			return;
		}

		for (Sort sort : report.getSorts()) {
			sort.addFieldCopy(availableFields);

			Column column = getColumnByFieldName(sort.getName(), report.getColumns());
			if (column == null) {
				Field field = sort.getField();
				if (field == null || field.getDatabaseColumnName() == null) {
					continue;
				}
				sort.setField(field);
			}
		}
	}

	private void addOrderByClause(Report report, SelectSQL selectSql) {
		if (report.getSorts().isEmpty()) {
			return;
		}

		for (Sort sort : report.getSorts()) {

			String fieldName;
			Column column = getColumnByFieldName(sort.getName(), report.getColumns());
			if (column == null) {
				Field field = sort.getField();
				if (field != null && field.getDatabaseColumnName() != null) {
					fieldName = field.getDatabaseColumnName();
				} else {
					continue;
				}
			} else {
				fieldName = column.getName();
			}

			if (!sort.isAscending()) {
				fieldName += " DESC";
			}

			selectSql.addOrderBy(fieldName);
		}
	}

	private Column getColumnByFieldName(String fieldName, List<Column> columns) {
		for (Column column : columns) {
			if (column.getName().equals(fieldName)) {
				return column;
			}
		}

		return null;
	}
}
