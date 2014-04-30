package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Deprecated
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

    public List<ProjectRole> getProjectRolesByProjectsAndRole(List<Integer> projectIds, Role role) {
        return projectRoleDAO.findByProjectsAndRole(projectIds, role);
    }

    public List<ProjectRoleEmployee> getProjectRolesForContractor(final Project project, final int accountId) {
        return projectRoleDAO.findByProjectAndContractor(project, accountId);
    }

    public Map<Role, Set<AccountSkill>> getRolesAndSkillsForProject(final Project project) {
        List<ProjectRole> projectRoles = projectRoleDAO.findByProject(project);

        Map<Role, Set<AccountSkill>> roleSkills = new HashMap<>();
        for (ProjectRole projectRole : projectRoles) {
            Role role = projectRole.getRole();
            if (!roleSkills.containsKey(role)) {
                roleSkills.put(role, new HashSet<AccountSkill>());
            }

            roleSkills.get(role).addAll(getSkillsFromAccountSkillGroup(role.getSkills()));
        }

        return roleSkills;
    }

    private Set<AccountSkill> getSkillsFromAccountSkillGroup(final List<AccountSkillRole> accountSkillGroups) {
        Set<AccountSkill> accountSkills = new HashSet<>();
        for (AccountSkillRole accountSkillRole : accountSkillGroups) {
            accountSkills.add(accountSkillRole.getSkill());
        }

        return accountSkills;
    }

    public Map<Employee, Set<Role>> getEmployeeProjectRoleAssignment(final Project project) {
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO
                .findByProject(project);

        Map<Employee, Set<Role>> employeeProjectRoleAssignment = new HashMap<>();
        for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
            Employee employee = projectRoleEmployee.getEmployee();
            if (!employeeProjectRoleAssignment.containsKey(employee)) {
                employeeProjectRoleAssignment.put(employee, new HashSet<Role>());
            }

            employeeProjectRoleAssignment.get(employee).add(projectRoleEmployee.getProjectRole().getRole());
        }

        return employeeProjectRoleAssignment;
    }

    public Map<AccountModel, Set<Employee>> getEmployeesAssignedToProjectRole(final Project project,
                                                                              final Role role,
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

    public List<ProjectRoleEmployee> getProjectRolesForContractor(final int accountId) {
        return projectRoleEmployeeDAO.findByAccountId(accountId);
    }

    public Map<Employee, Set<Role>> getEmployeeProjectAndSiteRolesByAccount(final int accountId) {
        List<Employee> employees = employeeDAO.findByAccount(accountId);

        Map<Employee, Set<Role>> employeeRoles = new HashMap<>();
        for (Employee employee : employees) {
            for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
                PicsCollectionUtil.addToMapOfKeyToSet(employeeRoles, employee, projectRoleEmployee.getProjectRole().getRole());
            }

            for (SiteAssignment siteAssignment : employee.getSiteAssignments()) {
                PicsCollectionUtil.addToMapOfKeyToSet(employeeRoles, employee, siteAssignment.getRole());
            }
        }

        return employeeRoles;
    }
}