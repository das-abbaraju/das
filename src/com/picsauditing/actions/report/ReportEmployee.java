package com.picsauditing.actions.report;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportEmployee extends ReportActionSupport {
	protected SelectSQL sql = new SelectSQL("employee e");
	protected ReportFilterEmployee filter = new ReportFilterEmployee();
	protected String filename = "ReportEmployee";

	public ReportEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		buildQuery();
		run(sql);
		
		if (download || "download".equals(button)) {
			addExcelColumns();
			excelSheet.setName(filename);
			HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

			filename += ".xls";

			ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
			ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
			wb.write(outstream);
			outstream.flush();
			ServletActionContext.getResponse().flushBuffer();
			return null;
		}

		return SUCCESS;
	}

	protected void buildQuery() {
		sql.addJoin("JOIN accounts a ON a.id = e.accountID");

		sql.addField("a.id accountID");
		sql.addField("a.name");
		sql.addField("e.id employeeID");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
	}

	protected void addFilterToSQL() {
		ReportFilterEmployee f = getFilter();

		if (filterOn(f.getAccountName(), ReportFilterEmployee.DEFAULT_NAME)) {
			String accountName = Utilities.escapeQuotes(f.getAccountName().trim());
			report.addFilter(new SelectFilter("name", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Utilities.escapeQuotes(accountName)
					+ "%' OR a.id = '" + Utilities.escapeQuotes(accountName) + "'", accountName));
			f.setLimitEmployees(false);
		}

		if (filterOn(f.getFirstName())) {
			String firstName = Utilities.escapeQuotes(f.getFirstName().trim());
			sql.addWhere("e.firstName LIKE '%" + firstName + "%'");
		}

		if (filterOn(f.getLastName())) {
			String lastName = Utilities.escapeQuotes(f.getLastName().trim());
			sql.addWhere("e.lastName LIKE '%" + lastName + "%'");
		}

		if (filterOn(f.getEmail())) {
			String email = Utilities.escapeQuotes(f.getEmail().trim());
			sql.addWhere("e.email LIKE '%" + email + "%'");
		}

		if (filterOn(f.getAssessmentCenters()))
			sql.addWhere("e.id IN (SELECT DISTINCT a_r.employeeID FROM assessment_result a_r "
					+ "JOIN assessment_test a_t ON a_t.id = a_r.assessmentTestID AND a_t.assessmentCenterID IN ("
					+ Strings.implode(f.getAssessmentCenters()) + "))");

		if (filterOn(f.getProjects()))
			sql.addWhere("e.id IN (SELECT DISTINCT e_s.employeeID FROM employee_site e_s WHERE e_s.jobSiteID IN ("
					+ Strings.implode(f.getProjects()) + "))");

		if (f.isLimitEmployees())
			sql.addWhere("a.id = " + permissions.getAccountId());
	}
	
	protected void addExcelColumns() {
		excelSheet.setData(data);
		
		excelSheet.addColumn(new ExcelColumn("name", "Company Name"));
		excelSheet.addColumn(new ExcelColumn("firstName", "Employee First Name"));
		excelSheet.addColumn(new ExcelColumn("name", "Employee Last Name"));
	}

	public ReportFilterEmployee getFilter() {
		return filter;
	}
}
