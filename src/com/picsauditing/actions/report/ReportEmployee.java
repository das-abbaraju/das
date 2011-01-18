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
	protected String filename;

	public ReportEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		buildQuery();
		run(sql);
		
		if (download || "download".equals(button))
			return getDownload();

		return SUCCESS;
	}

	protected void buildQuery() {
		sql.addJoin("JOIN accounts a ON a.id = e.accountID");

		sql.addField("a.id accountID");
		sql.addField("a.name");
		sql.addField("e.id employeeID");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("e.title");
		
		addFilterToSQL();
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

		if (f.isLimitEmployees() && f.isShowLimitEmployees())
			sql.addWhere("a.id = " + permissions.getAccountId());
	}
	
	protected void addExcelColumns() {
		excelSheet.setData(data);
		
		if (permissions.isOperatorCorporate())
			excelSheet.addColumn(new ExcelColumn("name", "Company Name"));
		
		excelSheet.addColumn(new ExcelColumn("firstName", "Employee First Name"));
		excelSheet.addColumn(new ExcelColumn("lastName", "Employee Last Name"));
	}

	public ReportFilterEmployee getFilter() {
		return filter;
	}
	
	protected String getDownload() throws Exception {
		addExcelColumns();
		
		if (Strings.isEmpty(filename)) {
			String className = this.getClass().getName();
			filename = className.substring(className.lastIndexOf(".") + 1);
		}
		
		HSSFWorkbook wb = buildWorkbook(filename);

		filename += ".xls";
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		return null;
	}
	
	protected HSSFWorkbook buildWorkbook(String filename) throws Exception {
		excelSheet.setName(filename);
		return excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));
	}
}
