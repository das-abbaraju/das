package com.picsauditing.actions.employees;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobRole;

@SuppressWarnings("serial")
public class ManageEmployeeRole extends ManageEmployees {
	@Autowired
	protected EmployeeRoleDAO employeeRoleDAO;
	@Autowired
	protected JobRoleDAO jobRoleDAO;

	private EmployeeRole employeeRole;
	private JobRole jobRole;

	public String add() {
		if (employee != null && jobRole != null) {
			EmployeeRole employeeRole = new EmployeeRole();
			employeeRole.setEmployee(employee);
			employeeRole.setJobRole(jobRole);
			employeeRole.setAuditColumns(permissions);

			if (!employee.getEmployeeRoles().contains(employeeRole)) {
				employee.getEmployeeRoles().add(employeeRole);
				employeeRoleDAO.save(employeeRole);
				addNote("Added " + jobRole.getName() + " job role");
			} else {
				addActionError("Employee already has " + jobRole.getName() + " as a Job Role");
			}
		}

		return SUCCESS;
	}

	public String remove() {
		if (employeeRole != null) {
			employee = employeeRole.getEmployee();
			account = employee.getAccount();

			employee.getEmployeeRoles().remove(employeeRole);
			employeeRoleDAO.remove(employeeRole);
			addNote("Removed " + employeeRole.getJobRole().getName() + " job role");
		}

		return SUCCESS;
	}

	public EmployeeRole getEmployeeRole() {
		return employeeRole;
	}

	public void setEmployeeRole(EmployeeRole employeeRole) {
		this.employeeRole = employeeRole;
	}

	public JobRole getJobRole() {
		return jobRole;
	}

	public void setJobRole(JobRole jobRole) {
		this.jobRole = jobRole;
	}
}
