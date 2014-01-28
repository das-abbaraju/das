package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectRoleService {

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

		for (AccountSkillGroup accountSkillGroup : projectRole.getRole().getSkills()) {
			accountSkills.add(accountSkillGroup.getSkill());
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

	public List<ProjectRole> getProjectRolesByProjectsAndRole(List<Integer> projectIds, AccountGroup role) {
		return projectRoleDAO.findByProjectsAndRole(projectIds, role);
	}

    public List<ProjectRoleEmployee> getProjectRolesForContractor(Project project, int accountId) {
        return projectRoleDAO.findByProjectAndContractor(project, accountId);
    }

    public Map<AccountGroup, Set<AccountSkill>> getRolesAndSkillsForProject(final Project project) {
        List<ProjectRole> projectRoles = projectRoleDAO.findByProject(project);

        Map<AccountGroup, Set<AccountSkill>> roleSkills = new HashMap<>();
        for (ProjectRole projectRole : projectRoles) {
            AccountGroup role = projectRole.getRole();
            if (!roleSkills.containsKey(role)) {
                roleSkills.put(role, new HashSet<AccountSkill>());
            }

            roleSkills.get(role).addAll(getSkillsFromAccountSkillGroup(role.getSkills()));
        }

        return roleSkills;
    }

    private Set<AccountSkill> getSkillsFromAccountSkillGroup(final List<AccountSkillGroup> accountSkillGroups) {
        Set<AccountSkill> accountSkills = new HashSet<>();
        for (AccountSkillGroup accountSkillGroup : accountSkillGroups) {
            accountSkills.add(accountSkillGroup.getSkill());
        }

        return accountSkills;
    }

    public Map<Employee, Set<AccountGroup>> getEmployeeProjectRoleAssignment(final Project project) {
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO
                .findByProject(project);

        Map<Employee, Set<AccountGroup>> employeeProjectRoleAssignment = new HashMap<>();
        for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
            Employee employee = projectRoleEmployee.getEmployee();
            if (!employeeProjectRoleAssignment.containsKey(employee)) {
                employeeProjectRoleAssignment.put(employee, new HashSet<AccountGroup>());
            }

            employeeProjectRoleAssignment.get(employee).add(projectRoleEmployee.getProjectRole().getRole());
        }

        return employeeProjectRoleAssignment;
    }

    public Map<AccountModel, Set<Employee>> getEmployeesAssignedToProjectRole(final Project project,
                                                                          final AccountGroup role,
                                                                          final Map<Integer, AccountModel> accountModels) {
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByProjectAndRole(project, role);

        Map<AccountModel, Set<Employee>> contractorEmployees = new HashMap<>();
        for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
            Employee employee = projectRoleEmployee.getEmployee();
            AccountModel accountModel = accountModels.get(employee.getAccountId());
            if (!contractorEmployees.containsKey(accountModel)) {
                contractorEmployees.put(accountModel, new HashSet<Employee>());
            }

            contractorEmployees.get(accountModel).add(projectRoleEmployee.getEmployee());
        }

        return contractorEmployees;
    }
}
