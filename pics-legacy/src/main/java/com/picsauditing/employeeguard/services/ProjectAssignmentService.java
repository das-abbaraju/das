package com.picsauditing.employeeguard.services;

import com.google.common.collect.Table;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.processor.ProjectAssignmentProcess;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectAssignmentService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;

	public Table<Project, Employee, Set<AccountSkill>> getEmployeeSkillsForProjectsUnderSite(final int siteId) {
		List<Project> siteProjects = projectEntityService.getAllProjectsForSite(siteId);
		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getEmployeesByProject(siteProjects);



//		List<Integer> contractorIds = accountService.getContractorIds(siteId);
//		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
//		Map<Role, Role> siteToCorporateRoles = roleEntityService.getSiteToCorporateRoles(siteId, corporateIds);
//		Map<Role, Set<Employee>> roleEmployees = getEmployeesByRole(siteId, siteProjects);
//
//		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(siteProjects);
//
//		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(roleEmployees.keySet());

		return null;
	}

	private Map<Employee, Set<AccountSkill>> getProjectSkillsForEmployees(final Collection<Employee> employees,
																		  final int siteId,
																		  final Map<Role, Set<Employee>> roleEmployees) {
		// Project Required Skills
		// Project Role Skills
		// Site + Corporate Role Skills
		List<Project> projects = projectEntityService.getAllProjectsForSite(siteId);
		Map<Project, Set<AccountSkill>> projectRequiredSkill = skillEntityService.getRequiredSkillsForProjects(projects);
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(projects);
		Map<Role, Set<AccountSkill>> projectRoleSkills = skillEntityService.getSkillsForRoles(Utilities.mergeCollectionOfCollections(projectRoles.values()));

		Map<Employee, Set<AccountSkill>> projectSkillsForEmployee = new HashMap<>();

		Set<AccountSkill> siteAndCorporateRequiredSkills = skillEntityService.getSiteRequiredSkills(siteId, accountService.getTopmostCorporateAccountIds(siteId));
		for (Employee employee : employees) {
			projectSkillsForEmployee.put(employee, new HashSet<>(siteAndCorporateRequiredSkills));
		}

		for (Project project : projects) {
			Set<AccountSkill> projectSkills = new HashSet<>();

			if (projectRequiredSkill.containsKey(project)) {
				projectSkills.addAll(projectRequiredSkill.get(project));
			}

			if (projectRoles.containsKey(project)) {
				for (Role role : projectRoles.get(project)) {
					if (projectRoleSkills.containsKey(role)) {
						projectSkills.addAll(projectRoleSkills.get(role));
					}

					if (roleEmployees.containsKey(role)) {
						for (Employee employee : roleEmployees.get(role)) {
							projectSkillsForEmployee.get(employee).addAll(projectSkills);
						}
					}
				}
			}
		}


		return projectSkillsForEmployee;


//		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
//		for (Employee employee : employeesToRole.keySet()) {
//			/*
//				if employee is in a project then all required project skills belong to the employee
//
//				always get the site and corporate required skills
//
//				always get all the skills for the role
//
//			 */
//		}
//
//		return employeeSkills;
	}

	/**
	 * Retrieves a map of employees to both site and project roles for the specific site
	 *
	 * @param siteId
	 * @param projects
	 * @return
	 */
	public Map<Role, Set<Employee>> getEmployeesByRole(final int siteId, final Collection<Project> projects) {
		return ProjectAssignmentProcess.getCorporateRoleEmployees(
				employeeEntityService.getEmployeesByProjectRoles(projects),
				employeeEntityService.getEmployeesBySiteRoles(siteId),
				roleEntityService.getSiteToCorporateRoles(siteId, accountService.getTopmostCorporateAccountIds(siteId)));
	}

}
