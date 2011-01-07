package com.picsauditing.actions.report;

import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByEmployee extends ReportEmployee {

	public ReportCompetencyByEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		
		sql.addJoin("LEFT JOIN (SELECT c.employeeID, COUNT(*) skilled FROM "
				+ "(SELECT DISTINCT ec.employeeID, ec.competencyID FROM employee_competency ec "
				+ "JOIN employee_role er ON er.employeeID = ec.employeeID AND ec.skilled = 1 "
				+ "JOIN job_role jr ON jr.id = er.jobRoleID AND jr.active = 1) c "
				+ "GROUP BY c.employeeID) ec ON ec.employeeID = e.id");
		sql.addJoin("LEFT JOIN employee_role er ON er.employeeID = e.id");
		sql.addJoin("LEFT JOIN job_role jr ON jr.accountID = a.id AND jr.id = er.jobRoleID AND jr.active = 1");
		sql.addJoin("LEFT JOIN job_competency jc ON jc.jobRoleID = jr.id");
		
		sql.addField("e.title");
		sql.addField("GROUP_CONCAT(DISTINCT jr.name ORDER BY jr.name SEPARATOR ', ') roles");
		sql.addField("IFNULL(ec.skilled, 0) skilled");
		sql.addField("COUNT(DISTINCT jc.competencyID) required");
		sql.addField("IFNULL(FLOOR((IFNULL(ec.skilled, 0)/COUNT(DISTINCT jc.competencyID)) * 100), 0) percent");
		
		sql.addGroupBy("e.id");
		
		PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
		sql.addWhere("1 " + permQuery.toString());
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		getFilter().setShowSsn(false);

		return super.execute();
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("title", "Title"));
		excelSheet.addColumn(new ExcelColumn("roles", "Job Roles"));
		excelSheet.addColumn(new ExcelColumn("skilled", "Competency", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("required", "Required", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("percent", "Competency %", ExcelCellType.Integer));
	}
}
