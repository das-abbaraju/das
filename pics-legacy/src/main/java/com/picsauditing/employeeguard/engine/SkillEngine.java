package com.picsauditing.employeeguard.engine;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SkillEngine {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private GroupEntityService groupEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;


	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsMapForAccount(final Collection<Employee> employees,
	                                                                       final AccountModel accountModel) {

		Map<Employee, Set<AccountSkill>> employeeGroupSkills =
				skillEntityService.getGroupSkillsForEmployees(groupEntityService.getEmployeeGroups(employees));
		Map<Employee, Set<AccountSkill>> employeeRoleSkills = getRoleSkillsForEmployees(employees, accountModel);
		Map<Employee, Set<AccountSkill>> employeeProjectSkills = getProjectSkillsForEmployees(employees, accountModel);

		Map<Employee, Set<AccountSkill>> employeeGroupRoleSkills = PicsCollectionUtil.mergeMapOfSets(employeeGroupSkills, employeeRoleSkills);
		return PicsCollectionUtil.mergeMapOfSets(employeeGroupRoleSkills, employeeProjectSkills);
	}

	public Map<Project, Map<Employee, Set<AccountSkill>>> getEmployeeSkillsMapForProjects(final Collection<Employee> employees,
	                                                                                      final Collection<Project> projects) {

		Map<Employee, Set<AccountSkill>> employeeProjectSkills = getProjectSkillsForEmployees(employees, projects);
		Map<Employee, Set<AccountSkill>> employeeProjectRoleSkills = getEmployeeProjectRoleSkills(employees, projects);
		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getEmployeesByProjects(projects);

		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = new HashMap<>();
		for (Project project : projects) {
			projectEmployeeSkills.put(project, new HashMap<Employee, Set<AccountSkill>>());

			if (projectEmployees.containsKey(project)) {
				for (Employee employee : projectEmployees.get(project)) {
					if (!employees.contains(employee)) {
						continue;
					}

					projectEmployeeSkills.get(project).put(employee, new HashSet<AccountSkill>());

					if (employeeProjectSkills.containsKey(employee)) {
						projectEmployeeSkills.get(project).get(employee).addAll(employeeProjectSkills.get(employee));
					}

					if (employeeProjectRoleSkills.containsKey(employee)) {
						projectEmployeeSkills.get(project).get(employee).addAll(employeeProjectRoleSkills.get(employee));
					}
				}
			}
		}

		return projectEmployeeSkills;
	}

	private Map<Employee, Set<AccountSkill>> getProjectSkillsForEmployees(final Collection<Employee> employees,
	                                                                      final Collection<Project> projects) {
		Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployees(employees);
		Map<Project, Set<AccountSkill>> projectRequiredSkills =
				skillEntityService.getRequiredSkillsForProjects(projects);

		return getEmployeeSkillsMap(employees, employeeProjects, projectRequiredSkills);
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeProjectRoleSkills(final Collection<Employee> employees,
	                                                                      final Collection<Project> projects) {
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(projects);
		Map<Role, Set<AccountSkill>> roleSkills =
				skillEntityService.getSkillsForRoles(PicsCollectionUtil.mergeCollectionOfCollections(projectRoles.values()));
		Map<Employee, Set<Role>> employeeRoles = roleEntityService.findByProjectsAndEmployees(projects, employees);
		return getEmployeeSkillsMap(employees, employeeRoles, roleSkills);
	}

	private Map<Employee, Set<AccountSkill>> getProjectSkillsForEmployees(final Collection<Employee> employees,
	                                                                      final AccountModel accountModel) {
		Map<Employee, Set<Project>> employeeProjects = getEmployeeProjects(employees, accountModel);
		Map<Project, Set<AccountSkill>> projectRequiredSkills =
				skillEntityService.getRequiredSkillsForProjects(PicsCollectionUtil.extractAndFlattenValuesFromMap(employeeProjects));

		return getEmployeeSkillsMap(employees, employeeProjects, projectRequiredSkills);
	}

	private Map<Employee, Set<AccountSkill>> getRoleSkillsForEmployees(final Collection<Employee> employees,
	                                                                   final AccountModel accountModel) {
		Map<Employee, Set<Role>> employeeRoles = getEmployeeSiteAssignments(employees, accountModel);
		Map<Role, Set<AccountSkill>> roleRequiredSkills =
				skillEntityService.getSkillsForRoles(PicsCollectionUtil.extractAndFlattenValuesFromMap(employeeRoles));

		return getEmployeeSkillsMap(employees, employeeRoles, roleRequiredSkills);
	}

	private <ENTITY> Map<Employee, Set<AccountSkill>> getEmployeeSkillsMap(final Collection<Employee> employees,
	                                                                       final Map<Employee, Set<ENTITY>> employeeToEntities,
	                                                                       final Map<ENTITY, Set<AccountSkill>> entitySkills) {
		if (MapUtils.isEmpty(employeeToEntities) || MapUtils.isEmpty(entitySkills)) {
			return Collections.emptyMap();
		}

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (Employee employee : employees) {
			if (!employeeSkills.containsKey(employee)) {
				employeeSkills.put(employee, new HashSet<AccountSkill>());
			}

			if (employeeToEntities.containsKey(employee)) {
				for (ENTITY entity : employeeToEntities.get(employee)) {
					if (CollectionUtils.isNotEmpty(entitySkills.get(entity))) {
						employeeSkills.get(employee).addAll(entitySkills.get(entity));
					}
				}
			}
		}

		return employeeSkills;
	}

	private Map<Employee, Set<Project>> getEmployeeProjects(final Collection<Employee> employees,
	                                                        final AccountModel accountModel) {
		switch (accountModel.getAccountType()) {

			case CONTRACTOR:
				return projectEntityService.getProjectsForEmployees(employees);

			case CORPORATE:
				Collection<Integer> siteIds = accountService.getChildOperatorIds(accountModel.getId());
				return projectEntityService.getProjectsForEmployeesBySiteIds(employees, siteIds);

			case OPERATOR:
				return projectEntityService.getProjectsForEmployeesBySiteId(employees, accountModel.getId());

			default:
				throw new IllegalArgumentException("Invalid account type");
		}
	}

	private Map<Employee, Set<Role>> getEmployeeSiteAssignments(final Collection<Employee> employees,
	                                                            final AccountModel accountModel) {
		int id = accountModel.getId();

		switch (accountModel.getAccountType()) {
			case CONTRACTOR:
				return getEmployeesToRolesForContractor(id);

			case CORPORATE:
				Collection<Integer> siteIds = accountService.getChildOperatorIds(id);
				return roleEntityService.getProjectRolesForEmployees(employees, siteIds);

			case OPERATOR:
				return roleEntityService.getProjectRolesForEmployees(employees, id);

			default:
				throw new IllegalArgumentException("Invalid account type");
		}
	}

	private Map<Employee, Set<Role>> getEmployeesToRolesForContractor(final int id) {
		List<Integer> siteIds = accountService.getOperatorIdsForContractor(id);
		Map<Role, Set<Employee>> rolesToEmployees = employeeEntityService.getEmployeesBySiteRoles(siteIds);
		Collection<Project> projects = projectEntityService.getProjectsBySiteIds(siteIds);
		Map<Role, Set<Employee>> projectRolesToEmployees = employeeEntityService.getEmployeesByProjectRoles(projects);

		Map<Role, Set<Employee>> allRolesToEmployees = PicsCollectionUtil.mergeMapOfSets(rolesToEmployees, projectRolesToEmployees);
		return PicsCollectionUtil.invertMapOfSet(allRolesToEmployees);
	}
}
