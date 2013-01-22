package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ReportJoin;
import com.picsauditing.search.SelectSQL;

public class SqlBuilder {

	private Report report;
	private SelectSQL sql;
	private Map<String, Field> availableFields;

	private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);

	public SelectSQL initializeSql(Report report, Permissions permissions) throws ReportValidationException {
		// FIXME this has nothing to do with building SQL
		AbstractModel model = ModelFactory.build(report.getModelType(), permissions);
		this.report = report;
		return initializeSql(model, this.report, permissions);
	}

	public SelectSQL initializeSql(AbstractModel model, Report report, Permissions permissions)
			throws ReportValidationException {
		logger.info("Starting SqlBuilder for " + model);
		this.report = report;

		sql = new SelectSQL();

		sql.setFromTable(model.getStartingJoin().getTableClause());

		// FIXME this has nothing to do with building SQL
		availableFields = model.getAvailableFields();

		// FIXME this does a lot of extra column stuff not to do with building SQL
		addFieldsAndGroupBy(report.getColumns());
		// FIXME this does a lot of extra filter stuff not to do with building SQL
		addRuntimeFilters(permissions);
		// FIXME this does a lot of extra sort stuff not to do with building SQL
		addOrderByClauses(model);

		addJoins(model.getStartingJoin());

		sql.addWhere(model.getWhereClause(report.getFilters()));

		logger.debug("SQL: " + sql);
		logger.info("Completed SqlBuilder");
		return sql;
	}

	private void addJoins(ReportJoin parentJoin) {
		for (ReportJoin join : parentJoin.getJoins()) {
			if (join.isNeeded(report)) {
				sql.addJoin(join.toJoinClause());
				addJoins(join);
			}
		}
	}

	private void addFieldsAndGroupBy(List<Column> columns) {
		// Make sure column has a field(?)
		for (Column column : columns) {
			Set<String> dependentFields = new HashSet<String>();

			column.setMethodToFieldName();

			column.addFieldCopy(availableFields);

			if (column.getField() == null) {
				continue;
			}

			if (!column.isHasAggregateMethod()) {
				// For example: Don't add in accountID automatically if
				// contractorName uses an aggregation like COUNT
				dependentFields.addAll(column.getField().getDependentFields());
			}

			String columnSql = column.getSql();
			if (usesGroupBy() && !column.isHasAggregateMethod()) {
				sql.addGroupBy(columnSql);
			}

			sql.addField(columnSql + " AS `" + column.getName() + "`");

			addDependentFields(dependentFields);
		}
	}

	private void addDependentFields(Set<String> dependentFields) {
		for (String fieldName : dependentFields) {
			if (isFieldIncluded(fieldName)) {
				continue;
			}

			Column column = new Column(fieldName);
			column.addFieldCopy(availableFields);
			String columnSql = column.getSql();
			sql.addField(columnSql + " AS `" + fieldName + "`");
		}
	}

	private boolean isFieldIncluded(String fieldName) {
		for (Column column : report.getColumns()) {
			if (column.getFieldNameWithoutMethod().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	private boolean usesGroupBy() {
		for (Column column : report.getColumns()) {
			String fieldNameWithoutMethod = column.getFieldNameWithoutMethod();
			if (fieldNameWithoutMethod == null) {
				continue;
			}

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
		if (report.getFilters().isEmpty()) {
			return;
		}

		List<Filter> whereFilters = new ArrayList<Filter>();
		List<Filter> havingFilters = new ArrayList<Filter>();

		for (Filter filter : report.getFilters()) {
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

		sql.addWhere(FilterExpression.parseWhereClause(report.getFilterExpression(), whereFilters));

		for (Filter filter : havingFilters) {
			String filterExp = filter.getSqlForFilter();
			sql.addHaving(filterExp);
		}
	}

	private void addOrderByClauses(AbstractModel model) {
		if (report.getSorts().isEmpty()) {
			return;
		}

		for (Sort sort : report.getSorts()) {
			sort.addFieldCopy(availableFields);

			String fieldName;
			Column column = getColumnFromFieldName(sort.getName());
			if (column == null) {
				Field field = sort.getField();
				if (field != null && field.getDatabaseColumnName() != null) {
					fieldName = field.getDatabaseColumnName();
				} else {
					continue;
				}
				sort.setField(field);
			} else {
				fieldName = column.getName();
			}

			if (!sort.isAscending()) {
				fieldName += " DESC";
			}

			sql.addOrderBy(fieldName);
		}
	}

	private Column getColumnFromFieldName(String fieldName) {
		for (Column column : report.getColumns()) {
			if (column.getName().equals(fieldName)) {
				return column;
			}
		}

		return null;
	}
}
