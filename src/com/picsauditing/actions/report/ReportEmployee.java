package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportEmployee extends ReportActionSupport {
	@Autowired
	protected AccountDAO accountDAO;

	protected SelectSQL sql = new SelectSQL("employee e");
	protected ReportFilterEmployee filter = new ReportFilterEmployee();
	protected String filename;

	public ReportEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	@Override
	public String execute() throws Exception {
		buildQuery();
		run(sql);

		if (download || "download".equals(button))
			return download();

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
		sql.addField("CONCAT('EmployeeClassification.', e.classification, '.description') classification");
		sql.addField("e.hireDate");
		sql.addField("e.email");
		sql.addField("e.phone");
		sql.addField("e.twicExpiration");

		String accountStatus = "'Active'";
		if (permissions.isAdmin() || permissions.getAccountStatus().isDemo())
			accountStatus += ", 'Demo'";
		sql.addWhere(String.format("a.status IN (%s)", accountStatus));
		// TODO make sure we need to default this
		sql.addWhere("e.active = 1");

		if (permissions.isContractor())
			sql.addWhere(String.format("a.id = %d", permissions.getAccountId()));

		addFilterToSQL();
	}

	protected void addFilterToSQL() {
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
		}

		if (filterOn(f.getLastName())) {
			String lastName = Strings.escapeQuotes(f.getLastName().trim());
			sql.addWhere("e.lastName LIKE '%" + lastName + "%'");
		}

		if (filterOn(f.getEmail())) {
			String email = Strings.escapeQuotes(f.getEmail().trim());
			sql.addWhere("e.email LIKE '%" + email + "%'");
		}

		if (f.isLimitEmployees() && f.isShowLimitEmployees())
			sql.addWhere("a.id = " + permissions.getAccountId());

		if (filterOn(f.getOperators())) {
			List<Integer> allChildren = new ArrayList<Integer>();
			// combine all operators and their children into 1 list
			for (int corpOpID : f.getOperators()) {
				List<Integer> children = new ArrayList<Integer>();
				OperatorAccount op = (OperatorAccount) accountDAO.find(corpOpID);

				children.add(op.getId());
				for (Facility facil : op.getOperatorFacilities())
					children.add(facil.getOperator().getId());

				if (f.isShowAnyOperator()) {
					allChildren.addAll(children);
				} else {
					sql.addWhere(String.format(
							"e.id IN (SELECT es.employeeID FROM employee_site es WHERE es.opID IN (%s))",
							Strings.implode(children)));
				}
			}

			if (f.isShowAnyOperator()) {
				sql.addWhere(String.format(
						"e.id IN (SELECT es.employeeID FROM employee_site es WHERE es.opID IN (%s))",
						Strings.implode(allChildren)));
			}
		}
	}

	protected void addExcelColumns() {
		excelSheet.setData(data);

		if (!permissions.isContractor())
			excelSheet.addColumn(new ExcelColumn("name", getText("global.CompanyName")));

		excelSheet.addColumn(new ExcelColumn("firstName", getText("Employee.firstName")));
		excelSheet.addColumn(new ExcelColumn("lastName", getText("Employee.lastName")));
	}

	public ReportFilterEmployee getFilter() {
		return filter;
	}

	public String download() throws Exception {
		if (data == null || data.isEmpty()) {
			buildQuery();
			download = true;
			run(sql);
		}

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
