package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.search.SelectSQL;
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

		sql.addField("GROUP_CONCAT(DISTINCT jr.name ORDER BY jr.name SEPARATOR ', ') roles");
		sql.addField("IFNULL(ec.skilled, 0) skilled");
		sql.addField("COUNT(DISTINCT jc.competencyID) required");
		sql.addField("IFNULL(FLOOR((IFNULL(ec.skilled, 0)/COUNT(DISTINCT jc.competencyID)) * 100), 0) percent");

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
		excelSheet.addColumn(new ExcelColumn("roles", getText(getScope() + ".label.JobRoles")));
		excelSheet.addColumn(new ExcelColumn("skilled", getText(getScope() + ".label.Competency"), ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("required", getText(getScope() + ".label.Required"), ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("percent", getText(getScope() + ".label.Competency") + " %", ExcelCellType.Integer));
	}
}
