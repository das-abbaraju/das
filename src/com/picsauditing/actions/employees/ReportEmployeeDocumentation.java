package com.picsauditing.actions.employees;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;

import javax.servlet.ServletOutputStream;

public class ReportEmployeeDocumentation extends ReportActionSupport {
	private ReportFilterEmployee filter = new ReportFilterEmployee();
	private SelectSQL sql = new SelectSQL("employee_competency ec");

	public ReportEmployeeDocumentation() {
		orderByDefault = "a.name, e.lastName, e.firstName";
		reportName = "ReportEmployeeDocumentation";
	}

	@Override
	public String execute() throws Exception {
		getFilter().setShowEmail(false);
		getFilter().setShowSsn(false);

		buildQuery();
		run(sql);

		if (download) {
			addExcelColumns();
			String filename = this.getClass().getSimpleName();
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

	private void buildQuery() {
		sql.addJoin("JOIN employee e ON e.id = ec.`employeeID`");
		sql.addJoin("JOIN accounts a ON a.id = e.accountID");
		sql.addJoin("JOIN operator_competency oc ON oc.id = ec.competencyID");
		sql.addJoin("JOIN operator_competency_course occ ON occ.competencyID = oc.id AND occ.courseType = " +
				"'REQUIRES_DOCUMENTATION'");
		sql.addJoin("JOIN accounts o ON o.id = oc.opID");
		sql.addJoin("LEFT JOIN operator_competency_employee_file ocef ON ocef.competencyID = ec.competencyID " +
				"AND ocef.employeeID = ec.employeeID");

		sql.addField("e.id employeeID");
		sql.addField("TRIM(e.firstName) firstName");
		sql.addField("TRIM(e.lastName) lastName");
		sql.addField("a.id");
		sql.addField("a.name");
		sql.addField("o.name opName");
		sql.addField("oc.label");
		sql.addField("oc.description");
		sql.addField("ocef.id fileID");
		sql.addField("ocef.expiration");
		sql.addField("ocef.fileName");

		if (permissions.isOperatorCorporate()) {
			sql.addWhere("o.id IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")");
		} else if (permissions.isContractor()) {
			sql.addWhere("a.id = " + permissions.getAccountId());
		}

		sql.addWhere("a.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo'" : "") + ")");

		addFilterToSQL();
	}

	private void addFilterToSQL() {
		ReportFilterEmployee f = getFilter();
		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			String accountName = Strings.escapeQuotes(f.getAccountName().trim());
			report.addFilter(new SelectFilter("name", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Strings.escapeQuotes(accountName)
					+ "%' OR a.id = '" + Strings.escapeQuotes(accountName) + "'", accountName));
			f.setLimitEmployees(false);
		}

		if (filterOn(f.getFirstName())) {
			String firstName = Strings.escapeQuotes(f.getFirstName().trim());
			sql.addWhere("e.firstName LIKE '%" + firstName + "%'");
			setFiltered(true);
		}

		if (filterOn(f.getLastName())) {
			String lastName = Strings.escapeQuotes(f.getLastName().trim());
			sql.addWhere("e.lastName LIKE '%" + lastName + "%'");
			setFiltered(true);
		}
	}

	public ReportFilterEmployee getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterEmployee filter) {
		this.filter = filter;
	}

	private void addExcelColumns() {
		excelSheet.setData(data);
		excelSheet.addColumn(new ExcelColumn("id", ExcelCellType.Integer), 0);
		excelSheet.addColumn(new ExcelColumn("name", getText("global.Contractor")));
		excelSheet.addColumn(new ExcelColumn("firstName", getText("Employee.firstName")));
		excelSheet.addColumn(new ExcelColumn("lastName", getText("Employee.lastName")));
		excelSheet.addColumn(new ExcelColumn("label", getText("OperatorCompetency.label")));
		excelSheet.addColumn(new ExcelColumn("expiration", getText("global.ExpirationDate"), ExcelCellType.Date));
	}
}
