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
import org.apache.commons.collections.CollectionUtils;
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
		Set<Employee> employeesAssignedToSite = getEmployeesAssignedToSite(siteId);

		Map<Employee, Set<AccountSkill>> employeeSkillsForProjects = getEmployeeSkillsForProjects(siteId, employeesAssignedToSite);
		Map<Employee, Set<AccountSkill>> employeeSkillsForRole = getEmployeeSkillsForRoles(siteId);

		Map<Employee, Set<AccountSkill>> allEmployeeRequiredSkills = Utilities.mergeValuesOfMapOfSets(employeeSkillsForProjects, employeeSkillsForRole);
		List<Integer> accountIdsInHierarchy = accountService.getTopmostCorporateAccountIds(siteId);

		return appendSiteAndCorporateSkills(allEmployeeRequiredSkills,
				skillEntityService.getSiteRequiredSkills(siteId, accountIdsInHierarchy));
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkillsForProjects(int siteId, Set<Employee> employeesAssignedToSite) {
		Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployees(employeesAssignedToSite, siteId);
		Set<Project> allProjects = Utilities.extractAndFlattenValuesFromMap(employeeProjects);
		Map<Project, Set<AccountSkill>> projectSkills = skillEntityService.getRequiredSkillsForProjects(allProjects);
		return getKeyToSetOfValues(employeeProjects, projectSkills);
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkillsForRoles(int siteId) {
		Map<Employee, Set<Role>> employeeRoles = getAllEmployeeRolesForSite(siteId);
		Set<Role> allRoles = Utilities.extractAndFlattenValuesFromMap(employeeRoles);
		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(allRoles);
		return getKeyToSetOfValues(employeeRoles, roleSkills);
	}

	private <KEY, ENTITY, VALUE> Map<KEY, Set<VALUE>> getKeyToSetOfValues(Map<KEY, Set<ENTITY>> keyToEntities, Map<ENTITY, Set<VALUE>> entityToValues) {
		Map<KEY, Set<VALUE>> keyValues = new HashMap<>();
		for (Map.Entry<KEY, Set<ENTITY>> keyEntityEntry : keyToEntities.entrySet()) {
			KEY key = keyEntityEntry.getKey();

			keyValues.put(key, new HashSet<VALUE>());

			for (ENTITY entity : keyEntityEntry.getValue()) {
				if (entityToValues.containsKey(entity)) {
					keyValues.get(key).addAll(entityToValues.get(entity));
				}
			}
		}

		return keyValues;
	}

	private <E> Map<E, Set<AccountSkill>> appendSiteAndCorporateSkills(final Map<E, Set<AccountSkill>> entitySkillMap,
																	   final Collection<AccountSkill> siteAndCorporateRequiredSkills) {
		if (CollectionUtils.isEmpty(siteAndCorporateRequiredSkills)) {
			return entitySkillMap;
		}

		for (E entity : entitySkillMap.keySet()) {
			if (entitySkillMap.containsKey(entity)) {
				entitySkillMap.get(entity).addAll(siteAndCorporateRequiredSkills);
			}
		}

		return entitySkillMap;
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

	public Map<Project, Set<Employee>> getEmployeesAssignedToProjects(final int siteId) {
		List<Project> projects = projectEntityService.getAllProjectsForSite(siteId);
		return employeeEntityService.getEmployeesByProject(projects);
	}

}