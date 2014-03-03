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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AssignmentService {

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

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId) {
		List<Employee> employeesAssignedToSite = getSiteContractorEmployees(siteId);

		Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployees(employeesAssignedToSite, siteId);
		Set<Project> allProjects = Utilities.extractAndFlattenValuesFromMap(employeeProjects);
		Map<Project, Set<AccountSkill>> projectSkills = skillEntityService.getRequiredSkillsForProjects(allProjects);

		Map<Employee, Set<Role>> employeeRoles = getAllEmployeeRolesForSite(siteId);
		Set<Role> allRoles = Utilities.extractAndFlattenValuesFromMap(employeeRoles);
		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(allRoles);

		Map<Employee, Set<AccountSkill>> employeeSkillsForProjects = getKeyToSetOfValues(employeeProjects, projectSkills);
		Map<Employee, Set<AccountSkill>> employeeSkillsForRole = getKeyToSetOfValues(employeeRoles, roleSkills);

		return Utilities.mergeValuesOfMapOfSets(employeeSkillsForProjects, employeeSkillsForRole);
	}

	private <KEY, ENTITY, VALUE> Map<KEY, Set<VALUE>> getKeyToSetOfValues(Map<KEY, Set<ENTITY>> keyToEntities, Map<ENTITY, Set<VALUE>> entityToValues) {
		Map<KEY, Set<VALUE>> keyValues = new HashMap<>();
		for (Map.Entry<KEY, Set<ENTITY>> keyEntityEntry : keyToEntities.entrySet()) {
			KEY key = keyEntityEntry.getKey();

			if (!keyValues.containsKey(key)) {
				keyValues.put(key, new HashSet<VALUE>());
			}

			for (ENTITY entity : keyEntityEntry.getValue()) {
				keyValues.get(key).addAll(entityToValues.get(entity));
			}
		}

		return keyValues;
	}

	public Set<Employee> getEmployeesAssignedToSite(final int siteId) {
		List<Employee> employeesAssignedToSite = getSiteContractorEmployees(siteId);

		final Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployees(employeesAssignedToSite, siteId);
		final Map<Employee, Set<Role>> employeeRoles = roleEntityService.getSiteRolesForEmployees(employeesAssignedToSite, siteId);

		return new HashSet<Employee>() {{
			addAll(employeeProjects.keySet());
			addAll(employeeRoles.keySet());
		}};
	}

	private List<Employee> getSiteContractorEmployees(int siteId) {
		List<Integer> contractorIds = accountService.getContractorIds(siteId);
		return employeeEntityService.getEmployeesAssignedToSite(contractorIds, siteId);
	}

	public Map<Employee, Set<Role>> getAllEmployeeRolesForSite(final int siteId) {
		List<Employee> employeesAssignedToSite = getSiteContractorEmployees(siteId);

		final Map<Employee, Set<Role>> employeeProjectRoles = roleEntityService.getProjectRolesForEmployees(employeesAssignedToSite, siteId);
		final Map<Employee, Set<Role>> employeeRoles = roleEntityService.getSiteRolesForEmployees(employeesAssignedToSite, siteId);

		return Utilities.mergeValuesOfMapOfSets(employeeProjectRoles, employeeRoles);
	}

}
