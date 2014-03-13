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
import com.picsauditing.employeeguard.services.processor.ProjectAssignmentDataSet;
import com.picsauditing.employeeguard.services.processor.ProjectAssignmentProcess;
import com.picsauditing.employeeguard.services.processor.RoleAssignmentProcess;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		Map<Project, Set<AccountSkill>> projectRequiredSkill = skillEntityService
				.getRequiredSkillsForProjects(siteProjects);
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(siteProjects);
		Map<Role, Set<AccountSkill>> projectRoleSkills = skillEntityService
				.getSkillsForRoles(Utilities.mergeCollectionOfCollections(projectRoles.values()));
		Set<AccountSkill> siteAndCorporateRequiredSkills = skillEntityService
				.getSiteRequiredSkills(siteId, accountService.getTopmostCorporateAccountIds(siteId));

		ProjectAssignmentDataSet dataSet = new ProjectAssignmentDataSet.Builder()
				.projectEmployees(projectEmployees)
				.projectRequiredSkills(projectRequiredSkill)
				.projectRoles(projectRoles)
				.projectRoleSkills(projectRoleSkills)
				.projects(siteProjects)
				.roleEmployees(roleEmployees)
				.siteAndCorporateRequiredSkills(siteAndCorporateRequiredSkills)
				.build();

		return new ProjectAssignmentProcess().getProjectSkillsForEmployees(dataSet);
	}

	/**
	 * Retrieves a map of employees to both site and project roles for the specific site
	 *
	 * @param siteId
	 * @param projects
	 * @return
	 */
	public Map<Role, Set<Employee>> getEmployeesByRole(final int siteId, final Collection<Project> projects) {
		return new RoleAssignmentProcess().getCorporateRoleEmployees(
				employeeEntityService.getEmployeesByProjectRoles(projects),
				employeeEntityService.getEmployeesBySiteRoles(siteId),
				roleEntityService.getSiteToCorporateRoles(siteId, accountService.getTopmostCorporateAccountIds(siteId)));
	}
}