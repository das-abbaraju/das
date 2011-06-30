package com.picsauditing.actions.report;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.Account;
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
	protected boolean hse = false;

	public ReportEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	@Override
	public String execute() throws Exception {
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

		String accountStatus = "'Active'";
		if (permissions.isAdmin() || permissions.getAccountStatus().isDemo())
			accountStatus += ", 'Demo'";
		sql.addWhere(String.format("a.status IN (%s)", accountStatus));
		// TODO make sure we need to default this
		sql.addWhere("e.active = 1");
		if (hse)
			sql.addWhere("a.requiresCompetencyReview = 1");

		if (permissions.isContractor())
			sql.addWhere(String.format("a.id = %d", permissions.getAccountId()));

		if (permissions.isOperatorCorporate()) {
			SelectSQL sql2 = new SelectSQL("generalcontractors gc");

			if (permissions.isOperator()) {
				if (hse) {
					sql2.addJoin("JOIN facilities f ON f.opID = gc.genID");
					sql2.addJoin(String.format("JOIN facilities c ON c.corporateID = f.corporateID "
							+ "AND c.opID = %d AND c.corporateID NOT IN (%s)", permissions.getAccountId(),
							Strings.implode(Account.PICS_CORPORATE)));
				} else {
					sql2.addWhere(String.format("gc.genID = %d", permissions.getAccountId()));
				}
			}

			if (permissions.isCorporate()) {
				sql.addWhere(String.format("a.id IN (SELECT gc.subID FROM generalcontractors gc "
						+ "JOIN facilities f ON f.opID = gc.genID AND f.corporateID = %1$d) OR a.id = %1$d "
						+ "OR a.id IN (SELECT opID FROM facilities WHERE corporateID = %1$d)",
						permissions.getAccountId()));
			}
		}

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

		if (filterOn(f.getOperators())) {
			sql.addWhere(String.format("e.id IN (SELECT es.employeeID FROM employee_site es WHERE es.opID IN (%s))",
					Strings.implode(f.getOperators())));
		}
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
