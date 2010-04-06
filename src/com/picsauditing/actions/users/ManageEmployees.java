package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;

public class ManageEmployees extends PicsActionSupport {

	private EmployeeDAO employeeDAO;

	private List<Employee> employees;

	public ManageEmployees(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	@Override
	public String execute() throws Exception {

		return SUCCESS;
	}

	public List<Employee> getEmployees() {
		if (employees == null) {
			employees = employeeDAO.findAll();
		}

		return employees;
	}

}
