package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectRoleService {

	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProjectRoleDAO projectRoleDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private ProjectService projectService;

	/**
	 * Includes skills attached directly on projects as well as site and corporate required skills
	 *
	 * @param projectRole
	 * @return
	 */
	public List<AccountSkill> getRequiredSkills(final ProjectRole projectRole) {
		List<AccountSkill> accountSkills = new ArrayList<>();

		for (AccountSkillRole accountSkillRole : projectRole.getRole().getSkills()) {
			accountSkills.add(accountSkillRole.getSkill());
		}

		accountSkills.addAll(projectService.getRequiredSkills(projectRole.getProject()));

		return ListUtil.removeDuplicatesAndSort(accountSkills);
	}

	public List<ProjectRole> getRolesForEmployee(Employee employee) {
		return projectRoleDAO.findByEmployee(employee);
	}

	public List<ProjectRole> getRolesForProfile(Profile profile) {
		return projectRoleDAO.findByProfile(profile);
	}

	public List<ProjectRole> getProjectRolesByProjectsAndRole(List<Integer> projectIds, Group role) {
		return projectRoleDAO.findByProjectsAndRole(projectIds, role);
	}

	public List<ProjectRoleEmployee> getProjectRolesForContractor(final Project project, final int accountId) {
		return projectRoleDAO.findByProjectAndContractor(project, accountId);
	}

	public List<ProjectRoleEmployee> getProjectRolesForContractor(final int accountId) {
		return projectRoleEmployeeDAO.findByAccountId(accountId);
	}

	public Map<Employee, Set<Role>> getEmployeeProjectAndSiteRolesByAccount(final int accountId) {
		List<Employee> employees = employeeDAO.findByAccount(accountId);

		Map<Employee, Set<Role>> employeeRoles = new HashMap<>();
		for (Employee employee : employees) {
			for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
				Utilities.addToMapOfKeyToSet(employeeRoles, employee, projectRoleEmployee.getProjectRole().getRole());
			}

			for (RoleEmployee roleEmployee : employee.getRoles()) {
				Utilities.addToMapOfKeyToSet(employeeRoles, employee, roleEmployee.getRole());
			}
		}

		return employeeRoles;
	}
}