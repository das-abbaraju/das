package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.Strings;
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

		sql.addJoin("LEFT JOIN (SELECT er.employeeID, GROUP_CONCAT(jr.name ORDER BY jr.name SEPARATOR ', ') names "
				+ "FROM employee_role er JOIN job_role jr ON jr.id = er.jobRoleID "
				+ "GROUP BY er.employeeID) roles ON roles.employeeID = e.id");
		sql.addJoin("LEFT JOIN (SELECT er.employeeID, COUNT(DISTINCT jc.competencyID) counts "
				+ "FROM employee_role er JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID "
				+ "GROUP BY er.employeeID) required ON required.employeeID = e.id");
		sql.addJoin("LEFT JOIN (SELECT ec.employeeID, COUNT(DISTINCT ec.competencyID) counts "
				+ "FROM employee_competency ec JOIN employee_role er ON er.employeeID = ec.employeeID "
				+ "JOIN job_competency jc ON jc.competencyID = ec.competencyID AND jc.jobRoleID = er.jobRoleID "
				+ "JOIN job_role jr on jr.id = jc.jobRoleID AND er.jobRoleID and jr.active = 1 "
				+ "GROUP BY ec.employeeID) skilled ON skilled.employeeID = e.id");

		if (permissions.isOperator()) {
			String accountStatus = "'Active'";
			if (permissions.getAccountStatus().isDemo())
				accountStatus += ", 'Demo'";

			sql.addJoin(String.format("JOIN generalcontractors gc ON gc.subID = a.id AND (gc.genID IN "
					+ "(SELECT f.opID FROM facilities f JOIN facilities c ON c.corporateID = f.corporateID "
					+ "AND c.corporateID NOT IN (%s) AND c.opID = %d) OR gc.genID = %2$d)",
					Strings.implode(Account.PICS_CORPORATE), permissions.getAccountId()));
			sql.addJoin(String.format("JOIN accounts o ON o.id = gc.genID AND o.status IN (%s)", accountStatus));
			sql.addJoin(String.format(
					"LEFT JOIN (SELECT subID FROM generalcontractors WHERE genID = %d) gcw ON gcw.subID = a.id",
					permissions.getAccountId()));
			sql.addField("ISNULL(gcw.subID) notWorksFor");
		}
		
		sql.addField("roles.names roles");
		sql.addField("IFNULL(skilled.counts, 0) skilled");
		sql.addField("IFNULL(required.counts, 0) required");
		sql.addField("ROUND((IFNULL(skilled.counts, 0) / IFNULL(required.counts, 1)) * 100) percent");

		sql.addWhere("a.requiresCompetencyReview = 1");
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
		excelSheet.addColumn(new ExcelColumn("title", getText("Employee.title")));
		excelSheet.addColumn(new ExcelColumn("roles", getText(String.format("%s.label.JobRoles", getScope()))));
		excelSheet.addColumn(new ExcelColumn("skilled", getText(String.format(".label.Competency", getScope())),
				ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("required", getText(String.format(".label.Required", getScope())),
				ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("percent", getText(String.format(".label.Competency", getScope())) + " %",
				ExcelCellType.Integer));
	}
}
