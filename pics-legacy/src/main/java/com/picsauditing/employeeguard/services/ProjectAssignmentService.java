package com.picsauditing.employeeguard.services;

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

	public Map<Project, Map<Employee, Set<AccountSkill>>> getEmployeeSkillsForProjectsUnderSite(final int siteId) {
		List<Project> siteProjects = projectEntityService.getAllProjectsForSite(siteId);
		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getEmployeesByProject(siteProjects);
		Map<Role, Set<Employee>> roleEmployees = getEmployeesByRole(siteId, siteProjects);

		return getProjectSkillsForEmployees(siteId, projectEmployees, roleEmployees);
	}

	private Map<Project, Map<Employee, Set<AccountSkill>>> getProjectSkillsForEmployees(final int siteId,
																						final Map<Project, Set<Employee>> projectEmployees,
																						final Map<Role, Set<Employee>> roleEmployees) {
		// Project Required Skills
		// Project Role Skills
		// Site + Corporate Role Skills
		List<Project> projects = projectEntityService.getAllProjectsForSite(siteId); // FIXME pass in from the method above
		Map<Project, Set<AccountSkill>> projectRequiredSkill = skillEntityService.getRequiredSkillsForProjects(projects);
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(projects);
		Map<Role, Set<AccountSkill>> projectRoleSkills = skillEntityService.getSkillsForRoles(Utilities.mergeCollectionOfCollections(projectRoles.values()));
		Set<AccountSkill> siteAndCorporateRequiredSkills = skillEntityService.getSiteRequiredSkills(siteId, accountService.getTopmostCorporateAccountIds(siteId));

		Map<Project, Map<Employee, Set<AccountSkill>>> projectSkillsForEmployee = new HashMap<>();
		for (Project project : projects) {
			if (projectRoles.containsKey(project)) {
				for (Role role : projectRoles.get(project)) {
					if (roleEmployees.containsKey(role)) {
						for (final Employee employee : roleEmployees.get(role)) {
							if (employeeBelongsToProject(projectEmployees, project, employee)) {
								final Set<AccountSkill> projectSkills = new HashSet<>(siteAndCorporateRequiredSkills);

								if (projectRequiredSkill.containsKey(project)) {
									projectSkills.addAll(projectRequiredSkill.get(project));
								}

								if (projectRoleSkills.containsKey(role)) {
									projectSkills.addAll(projectRoleSkills.get(role));
								}

								if (!projectSkillsForEmployee.containsKey(project)) {
									projectSkillsForEmployee.put(project, new HashMap<Employee, Set<AccountSkill>>());
								}

								if (!projectSkillsForEmployee.get(project).containsKey(employee)) {
									projectSkillsForEmployee.get(project).put(employee, new HashSet<AccountSkill>());
								}

								projectSkillsForEmployee.get(project).get(employee).addAll(projectSkills);
							}
						}
					}
				}
			}
		}


		return projectSkillsForEmployee;
	}

	private boolean employeeBelongsToProject(Map<Project, Set<Employee>> projectEmployees, Project project, Employee employee) {
		return projectEmployees.containsKey(project) && projectEmployees.get(project).contains(employee);
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
