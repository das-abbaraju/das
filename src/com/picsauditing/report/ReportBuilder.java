package com.picsauditing.report;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ReportModelDAO;
import com.picsauditing.dao.ReportQueryDAO;
import com.picsauditing.jpa.entities.ReportModel;
import com.picsauditing.jpa.entities.ReportModelColumn;
import com.picsauditing.jpa.entities.ReportQuery;
import com.picsauditing.jpa.entities.ReportQueryColumn;
import com.picsauditing.search.SelectSQL;

public class ReportBuilder {

	@Autowired
	ReportModelDAO reportModelDAO;
	@Autowired
	ReportQueryDAO reportQueryDAO;

	public void buildNewQuery(List<ReportModelColumn> columns) {
		
	}

	public void buildExistingQuery(int queryID) {
		SelectSQL sql = null;
		ReportQuery reportQuery = reportQueryDAO.find(queryID);

		if (reportQuery == null) {
			ReportModel reportModel = reportModelDAO.find(queryID);
			sql = buildBaseSQLQuery(reportModel, true);
		} else {
			sql = buildBaseSQLQuery(reportQuery.getModel(), false);

			addFields(sql, reportQuery);

			if (reportQuery.getCondition() != null)
				sql.addWhere(reportQuery.getCondition());

			// the group by and order by must be sorted properly
			addByClauses(sql, reportQuery);

			if (reportQuery.getReportLimit() > 0)
				sql.setLimit(reportQuery.getReportLimit());
		}

		System.out.println(sql.toString());
		System.out.println();
	}

	private void addFields(SelectSQL sql, ReportQuery reportQuery) {
		for (ReportQueryColumn column : reportQuery.getColumns()) {
			sql.addField(getFieldWithTableAliasQueryText(reportQuery.getModel(), column.getModelColumn(), true));
		}
	}

	private void addByClauses(SelectSQL sql, ReportQuery reportQuery) {
		Set<ReportQueryColumn> orderedColumns = new TreeSet<ReportQueryColumn>(reportQuery.getColumns());
		for (ReportQueryColumn column : orderedColumns) {
			String byClause = getFieldWithTableAliasQueryText(reportQuery.getModel(), column.getModelColumn(), false);

			if (column.isOrderByDesc())
				byClause += " DESC";
			if (column.getGroupBy() > 0)
				sql.addGroupBy(byClause);
			if (column.getOrderBy() > 0)
				sql.addOrderBy(byClause);
		}
	}

	private SelectSQL buildBaseSQLQuery(ReportModel reportModel, boolean displayDefinedModelColumns) {
		SelectSQL sql = null;

		// get primary table and joins through recursion
		if (reportModel.getParent() != null) {
			sql = buildBaseSQLQuery(reportModel.getParent(), displayDefinedModelColumns);
			sql.addJoin(getJoinQueryText(reportModel, false));
		} else {
			sql = new SelectSQL(getTableQueryText(reportModel, true));
		}

		sql.addWhere(reportModel.getCondition());

		if (displayDefinedModelColumns) {
			for (ReportModelColumn column : reportModel.getColumns()) {
				sql.addField(getFieldWithTableAliasQueryText(reportModel, column, true));
			}
		}

		return sql;
	}

	private String getFieldWithTableAliasQueryText(ReportModel reportModel, ReportModelColumn column,
			boolean displayColumnAlias) {
		return reportModel.getTableAlias() + "." + getFieldQueryText(column, displayColumnAlias);
	}

	private String getFieldQueryText(ReportModelColumn column, boolean displayColumnAlias) {
		String field = column.getColumnName();

		if (displayColumnAlias && column.getColumnAlias() != null)
			field += " AS " + column.getColumnAlias();
		return field;
	}

	private String getTableQueryText(ReportModel reportModel, boolean displayTableAlias) {
		String table = reportModel.getTableName();

		if (displayTableAlias && reportModel.getTableAlias() != null)
			table += " " + reportModel.getTableAlias();
		return table;
	}

	private String getJoinQueryText(ReportModel reportModel, boolean isLeftJoin) {
		String join = "";
		if (isLeftJoin)
			join += "LEFT ";
		join += "JOIN " + getTableQueryText(reportModel, true);
		return join;
	}
}
