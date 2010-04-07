package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;

public class ManageEmployees extends PicsActionSupport {

	private EmployeeDAO employeeDAO;

	private List<Employee> employees;

	private Employee employee;
	private int employeeID;

	public ManageEmployees(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		employee = employeeDAO.find(employeeID);

		return SUCCESS;
	}

	public List<Employee> getEmployees() {
		if (employees == null) {
			employees = employeeDAO.findAll();
		}

		return employees;
	}

	public Employee getEmployee() {
		return employee;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}
}
