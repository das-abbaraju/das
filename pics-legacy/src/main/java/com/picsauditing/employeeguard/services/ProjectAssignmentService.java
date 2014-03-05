package com.picsauditing.employeeguard.services;

import com.google.common.collect.Table;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.springframework.beans.factory.annotation.Autowired;

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

	public Table<Project, Employee, Set<AccountSkill>> getEmployeeSkillsForProjectsUnderSite(final int siteId) {
		List<Project> siteProjects = projectEntityService.getAllProjectsForSite(siteId);
		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getEmployeesByProject(siteProjects);

		List<Integer> contractorIds = accountService.getContractorIds(siteId);
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		Map<Role, Role> siteToCorporateRoles = roleEntityService.getSiteToCorporateRoles(siteId, corporateIds);
		Map<Role, Set<Employee>> roleEmployees = employeeEntityService.getEmployeesByRole(siteId, contractorIds, siteProjects, siteToCorporateRoles);

		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(siteProjects);

		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(roleEmployees.keySet());

		return null;
	}

}
