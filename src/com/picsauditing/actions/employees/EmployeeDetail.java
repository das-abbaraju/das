package com.picsauditing.actions.employees;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.Strings;

public class EmployeeDetail extends AccountActionSupport implements Preparable {

	private EmployeeDAO employeeDAO;

	protected Account account;

	protected Employee employee;

	public EmployeeDetail(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	@Override
	public void prepare() throws Exception {

		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}

	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getSsn() {
		if (employee != null)
			return Strings.maskSSN(employee.getSsn());

		return null;
	}
}
