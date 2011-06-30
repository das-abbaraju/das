package com.picsauditing.actions.report;

import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportCompetencyByEmployee extends ReportEmployee {

	public ReportCompetencyByEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
		hse = true;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		SelectSQL sql2 = new SelectSQL("employee_competency ec");
		sql2.addJoin("JOIN employee_role er ON er.employeeID = ec.employeeID AND ec.skilled = 1");
		sql2.addJoin("JOIN job_role jr ON jr.id = er.jobRoleID AND jr.active = 1");
		sql2.addField("ec.employeeID");
		sql2.addField("ec.competencyID");

		SelectSQL sql3 = new SelectSQL(String.format("(%s) c", sql2.toString()));
		sql3.addField("c.employeeID");
		sql3.addField("COUNT(*) skilled");
		sql3.addGroupBy("c.employeeID");

		sql.addJoin(String.format("LEFT JOIN (%s) ec ON ec.employeeID = e.id", sql3.toString()));
		sql.addJoin("LEFT JOIN employee_role er ON er.employeeID = e.id");
		sql.addJoin("LEFT JOIN job_role jr ON jr.accountID = a.id AND jr.id = er.jobRoleID AND jr.active = 1");
		sql.addJoin("LEFT JOIN job_competency jc ON jc.jobRoleID = jr.id");

		sql.addField("GROUP_CONCAT(DISTINCT jr.name ORDER BY jr.name SEPARATOR ', ') roles");
		sql.addField("IFNULL(ec.skilled, 0) skilled");
		sql.addField("COUNT(DISTINCT jc.competencyID) required");
		sql.addField("IFNULL(FLOOR((IFNULL(ec.skilled, 0)/COUNT(DISTINCT jc.competencyID)) * 100), 0) percent");

		if (permissions.isCorporate()) {
			PermissionQueryBuilderEmployee permQuery = new PermissionQueryBuilderEmployee(permissions);
			sql.addWhere("1 " + permQuery.toString());
		}

		sql.addGroupBy("e.id");
	}

	public String execute() throws Exception {
		getFilter().setShowSsn(false);
		getFilter().setShowOperators(true);

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
