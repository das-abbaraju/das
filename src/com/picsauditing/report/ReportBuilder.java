package com.picsauditing.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ReportModelDAO;
import com.picsauditing.jpa.entities.ReportModel;
import com.picsauditing.jpa.entities.ReportModelColumn;
import com.picsauditing.search.SelectSQL;


public class ReportBuilder {
	
	@Autowired
	ReportModelDAO reportModelDAO;
	
	public void buildQueryReport(int reportID) {
		ReportModel reportModel = reportModelDAO.find(reportID);
		
		SelectSQL sql = getParentJoinQueryText(reportModel);

		for (ReportModelColumn column : reportModel.getColumns()) {
			sql.addField(getFieldQueryText(column, true));
		}

		System.out.println(sql.toString());
	}

	private SelectSQL getParentJoinQueryText(ReportModel reportModel)
	{
		SelectSQL sql = null;
		if (reportModel.getParent() != null)
		{
			sql = getParentJoinQueryText(reportModel.getParent());
			sql.addJoin(getJoinQueryText(reportModel));
		}
		else
		{
			sql = new SelectSQL(getTableQueryText(reportModel, true));
		}
		
		sql.addWhere(reportModel.getCondition());

		return sql;
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
	
	private String getJoinQueryText(ReportModel reportModel) {
		return "JOIN " + getTableQueryText(reportModel, true);
	}
}
