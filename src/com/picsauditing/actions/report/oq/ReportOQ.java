package com.picsauditing.actions.report.oq;

import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportOQ extends ReportEmployee {
	@Override
	public String execute() throws Exception {
		loadPermissions();
		
		getFilter().setShowFirstName(false);
		getFilter().setShowLastName(false);
		getFilter().setShowEmail(false);
		getFilter().setShowSsn(false);
		getFilter().setShowProjects(true);
		getFilter().setPermissions(permissions);
		
		return super.execute();
	}
	
	protected void buildQuery() {
		super.buildQuery();
		
		sql.addJoin("JOIN employee_site es ON es.employeeID = e.id");
		sql.addJoin("JOIN job_site js ON js.id = es.jobSiteID " +
				"AND (js.projectStart IS NULL OR js.projectStart < NOW()) " +
				"AND (js.projectStop IS NULL OR js.projectStop > NOW())");
		
		sql.addField("js.id jobSiteID");
		sql.addField("js.label");
		
		sql.addWhere("e.id IN (" +
				"SELECT eq.employeeID FROM employee_qualification eq " +
				"JOIN job_site_task jst ON jst.taskID = eq.taskID WHERE jst.jobID = js.id)");
		
		sql.addGroupBy("a.name, js.label, e.id");
		
		String subselect = sql.toString();
		
		sql = new SelectSQL("(" + subselect + ") t");
		sql.addField("t.accountID");
		sql.addField("t.name");
		sql.addField("t.jobSiteID");
		sql.addField("t.label");
		sql.addField("count(*) employeeCount");
		sql.addGroupBy("t.name, t.label");
		
		String fullClause = sql.toString();
		
		sql.setFullClause(fullClause);
	}
	
	@Override
	protected void addExcelColumns() {
		filename = "ReportOQByCompanySite";
		excelSheet.setData(data);
		
		excelSheet.addColumn(new ExcelColumn("name", "Company Name"));
		excelSheet.addColumn(new ExcelColumn("label", "Site Label"));
		excelSheet.addColumn(new ExcelColumn("employeeCount", "Employees", ExcelCellType.Integer));
	}
	
	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();
		
		if (filterOn(getFilter().getProjects()))
			sql.addWhere("js.id IN (" + Strings.implode(getFilter().getProjects()) + ")");
	}
}
