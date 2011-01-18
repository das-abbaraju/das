package com.picsauditing.actions.report.oq;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportOQ extends ReportEmployee {
	@Override
	public String execute() throws Exception {
		loadPermissions();
		
		if (!permissions.isRequiresOQ())
			throw new NoRightsException("Operator Qualification");

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

		orderByDefault = "a.name, js.name";

		sql.addJoin("JOIN employee_site es ON es.employeeID = e.id"
				+ dateRange("es.effectiveDate", "es.expirationDate"));
		sql.addJoin("JOIN job_site js ON js.id = es.jobSiteID" + dateRange("js.projectStart", "js.projectStop"));
		sql.addJoin("JOIN employee_qualification eq ON eq.employeeID = e.id AND eq.qualified = 1"
				+ dateRange("eq.effectiveDate", "eq.expirationDate"));
		sql.addJoin("JOIN job_site_task jst ON jst.jobID = js.id AND jst.taskID = eq.taskID");

		sql.addField("js.id jsID");
		sql.addField("js.name jsName");
		sql.addField("COUNT(DISTINCT e.id) employeeCount");

		sql.addGroupBy("a.id, js.id");
	}

	@Override
	protected void addExcelColumns() {
		filename = "ReportOQByCompanySite";
		excelSheet.setData(data);

		excelSheet.addColumn(new ExcelColumn("name", "Company Name"));
		excelSheet.addColumn(new ExcelColumn("jsName", "Project Name"));
		excelSheet.addColumn(new ExcelColumn("employeeCount", "Employees", ExcelCellType.Integer));
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		if (filterOn(getFilter().getProjects()))
			sql.addWhere("js.id IN (" + Strings.implode(getFilter().getProjects()) + ")");
	}

	private String dateRange(String start, String stop) {
		return " AND (" + start + " IS NULL OR " + start + " < NOW()) " + "AND (" + stop + " IS NULL OR " + stop
				+ " > NOW())";
	}
}
