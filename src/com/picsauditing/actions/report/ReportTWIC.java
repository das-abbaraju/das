package com.picsauditing.actions.report;

import java.util.List;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.ReportFilterEmployee;

public class ReportTWIC extends ReportAccount {

	// protected EmployeeDAO employeeDAO;
	// protected AccountDAO accountDAO;
	private ReportFilterEmployee filter;
	protected SelectSQL sql = new SelectSQL();

	// private Account account;
	// private List<Employee> employeeList = null;

	public void prepare() throws Exception {
		super.prepare();
		
		getFilter().setShowAccountName(true);
	}

	protected void buildQuery() {

		getFilter().setShowAccountName(true);
		sql = new SelectSQL("employee e");

		sql.addField("e.firstName");
		sql.addField("a.name");
		sql.addJoin("JOIN accounts a on a.id = e.accountID");
		sql.addWhere("a.id=3");
		System.out.println(sql.toString());
	}

	/*
	 * public List<Employee> getEmployeeList() { if (employeeList == null)
	 * employeeList = employeeDAO.findByAccount(account); return employeeList; }
	 */

}
