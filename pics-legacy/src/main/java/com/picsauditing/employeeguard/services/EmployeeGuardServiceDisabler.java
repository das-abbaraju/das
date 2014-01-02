package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Project;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EmployeeGuardServiceDisabler {

	@Autowired
	private AccountGroupDAO groupDAO;
	@Autowired
	private AccountSkillDAO skillDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProjectDAO projectDAO;

	public void removeAccount(int accountId) {
		List<Employee> employees = employeeDAO.findByAccount(accountId);
		employeeDAO.delete(employees);

		List<AccountSkill> skills = skillDAO.findByAccount(accountId);
		skillDAO.delete(skills);

		List<Group> groups = groupDAO.findByAccount(accountId);
		groupDAO.delete(groups);

		List<Project> projects = projectDAO.findByAccount(accountId);
		projectDAO.delete(projects);
	}
}
