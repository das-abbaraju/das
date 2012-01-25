package com.picsauditing.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SimpleReportField;
import com.picsauditing.report.fields.SimpleReportFilter;
import com.picsauditing.report.models.ModelBase;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.tables.BaseTable;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public class SqlBuilder {
	private ModelBase base;
	private List<QueryField> includedFields = new ArrayList<QueryField>();
	// We may be able to use the ModelBase.getAvailableFields...
	private Map<String, QueryField> availableFields = new HashMap<String, QueryField>();
	private SimpleReportDefinition definition = new SimpleReportDefinition();
	private SelectSQL sql;

	public SelectSQL getSql() {
		sql = new SelectSQL();
		includedFields.clear();
		availableFields.clear();
		
		setFrom();

		addJoins(base.getFrom());
		addFields(base.getFrom());

		addRuntimeFilters();
		addGroupBy();
		addHaving();
		addOrderBy();

		return sql;
	}

	private void setFrom() {
		String from = base.getFrom().getTable();
		if (!Strings.isEmpty(base.getFrom().getAlias()))
			from += " AS " + base.getFrom().getAlias();
		sql.setFromTable(from);
	}

	private void addJoins(BaseTable table) {
		for (BaseTable join : table.getJoins()) {
			// System.out.println("Adding join " + join.getTable() + " " + join.getAlias());
			addFields(join);
			if (join.isInnerJoin() || includedFields.size() > 0) {
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

	private void addFields(BaseTable join) {
		for (String fieldName : join.getFields().keySet()) {
			QueryField queryField = join.getFields().get(fieldName);
			availableFields.put(fieldName, queryField);
			if (isFieldRequested(fieldName)) {
				includedFields.add(queryField);
				sql.addField(queryField.getSql() + " AS `" + queryField.getDataIndex() + "`");
			}
		}
	}

	private boolean isFieldRequested(String fieldName) {
		if (definition == null || definition.getColumns().size() == 0)
			return true;
		for (SimpleReportField requestField : definition.getColumns()) {
			if (fieldName.equalsIgnoreCase(requestField.getName()))
				return true;
		}
		return false;
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

			String filterExp = queryFilter.toExpression(availableFields);
			where = where.replace("{" + i + "}", "(" + filterExp + ")");
		}
		sql.addWhere(where);
	}

	private void addGroupBy() {
		if (definition.getGroupBy().size() == 0) {
			return;
		}

		for (SimpleReportField field : definition.getGroupBy()) {
			String groupBy = field.toSQL(availableFields);
			sql.addGroupBy(groupBy);
		}

		// addTotalField();
	}

	private void addHaving() {
		if (definition.getHaving().size() == 0) {
			return;
		}

		for (SimpleReportField field : definition.getHaving()) {
			String having = field.toSQL(availableFields);
			sql.addHaving(having);
		}
	}

	private void addOrderBy() {
		if (definition.getOrderBy().size() == 0) {
			sql.addOrderBy(base.getDefaultSort());
			return;
		}

		for (SimpleReportField field : definition.getOrderBy()) {
			String orderBy = field.getName();
			if (!sql.getFields().contains(field.getName()))
				orderBy = availableFields.get(field.getName()).getSql();
			if (!field.isAscending())
				orderBy += " DESC";
			sql.addOrderBy(orderBy);
		}
	}

	// Setters

	public void setReport(Report report) {
		this.base = ModelFactory.getBase(report.getModelType());
	}

	public void setBase(ModelBase base) {
		this.base = base;
	}

	public void setDefinition(SimpleReportDefinition definition) {
		this.definition = definition;
	}

	public Map<String, QueryField> getAvailableFields() {
		return availableFields;
	}

	public void setPermissions(Permissions permissions) {
		String where = this.base.getWhereClause(permissions);
		sql.addWhere(where);
	}

	public List<QueryField> getIncludedFields() {
		return includedFields;
	}
}
