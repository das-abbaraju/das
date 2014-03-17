package com.picsauditing.actions.report.oq;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportOQ extends ReportEmployee {
	@Override
	public String execute() throws Exception {
		if (!permissions.isRequiresOQ())
			throw new NoRightsException("Operator Qualification");

		getFilter().setShowFirstName(false);
		getFilter().setShowLastName(false);
		getFilter().setShowEmail(false);
		getFilter().setShowProjects(true);
		getFilter().setPermissions(permissions);

		return super.execute();
	}

	protected void buildQuery() {
		super.buildQuery();

		orderByDefault = "a.name, js.name";

		sql.addJoin("JOIN employee_site es ON es.employeeID = e.id"
				+ dateRange("es.effectiveDate", "es.expirationDate"));
		sql.addJoin(String.format("JOIN job_site js ON js.id = es.jobSiteID%s",
				dateRange("js.projectStart", "js.projectStop")));
		sql.addJoin(String.format("LEFT JOIN employee_qualification eq ON eq.employeeID = e.id AND eq.qualified = 1%s",
				dateRange("eq.effectiveDate", "eq.expirationDate")));
		sql.addJoin("JOIN job_site_task jst ON jst.jobID = js.id AND jst.taskID = eq.taskID");

		SelectSQL sql2 = new SelectSQL("employee e2");
		sql2.addJoin("JOIN employee_site es2 ON es2.employeeID = e2.id"
				+ dateRange("es2.effectiveDate", "es2.expirationDate"));
		sql2.addJoin(String.format("JOIN job_site js2 ON js2.id = es2.jobSiteID%s AND js2.opID = %d",
				dateRange("js2.projectStart", "js2.projectStop"), permissions.getAccountId()));
		sql2.addField("e2.accountID");
		sql2.addField("js2.id jobID");
		sql2.addField("COUNT(*) totals");
		sql2.addGroupBy("e2.accountID, js2.id");

		sql.addJoin(String.format("JOIN (%s) e2 ON e2.accountID = a.id AND e2.jobID = js.id", sql2.toString()));

		sql.addField("js.id jsID");
		sql.addField("js.name jsName");
		sql.addField("COUNT(DISTINCT e.id) employeeCount");
		sql.addField("e2.totals employeeTotals");
		sql.addField("CASE a.type WHEN 'Contractor' THEN 1 ELSE 0 END isContractor");

		sql.addGroupBy("a.id, js.id");
	}

	@Override
	protected void addExcelColumns() {
		filename = "ReportOQByCompanySite";
		excelSheet.setData(data);

		excelSheet.addColumn(new ExcelColumn("name", getText("global.CompanyName")));
		excelSheet.addColumn(new ExcelColumn("jsName", "Project Name"));
		excelSheet.addColumn(new ExcelColumn("employeeCount", "Qualified Employees", ExcelCellType.Integer));
		excelSheet.addColumn(new ExcelColumn("employeeTotals", "Total Employees", ExcelCellType.Integer));
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getProjects()))
			sql.addWhere("js.id IN (" + Strings.implode(getFilter().getProjects()) + ")");
	}

	private String dateRange(String start, String stop) {
		return String.format(" AND (%1$s IS NULL OR %1$s < NOW()) AND (%2$s IS NULL OR %2$s > NOW())", start, stop);
	}
}
