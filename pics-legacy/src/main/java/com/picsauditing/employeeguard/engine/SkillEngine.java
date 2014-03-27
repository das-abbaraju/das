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

	public Map<Integer, Set<AccountSkill>> getEmployeeSkillsForSites(final Map<Integer, Set<Employee>> siteAssignments,
	                                                                 final Map<Integer, Employee> contractorEmployee) {
//		if (CollectionUtils.isEmpty(sites) || CollectionUtils.isEmpty(employeeProjects)) {
//			return Collections.emptyMap();
//		}
//
//		Map<Integer, Set<AccountSkill>> employeeSiteSkills = new HashMap<>();
//		for (Integer siteId : sites) {
//			employeeSiteSkills.put(siteId, getEmployeeSkillsForSite(siteId, employeeProjects));
//		}
//
//		return employeeSiteSkills;

		return Collections.emptyMap();
	}

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsMapForAccount(final Collection<Employee> employees,
	                                                                       final AccountModel accountModel) {

		Map<Employee, Set<AccountSkill>> employeeGroupSkills =
				skillEntityService.getGroupSkillsForEmployees(groupEntityService.getEmployeeGroups(employees));
		Map<Employee, Set<AccountSkill>> employeeRoleSkills = getRoleSkillsForEmployees(employees, accountModel);
		Map<Employee, Set<AccountSkill>> employeeProjectSkills = getProjectSkillsForEmployees(employees, accountModel);

		Map<Employee, Set<AccountSkill>> employeeGroupRoleSkills = PicsCollectionUtil.mergeMapOfSets(employeeGroupSkills, employeeRoleSkills);
		return PicsCollectionUtil.mergeMapOfSets(employeeGroupRoleSkills, employeeProjectSkills);
	}

	private Map<Employee, Set<AccountSkill>> getProjectSkillsForEmployees(final Collection<Employee> employees,
	                                                                      final AccountModel accountModel) {

		Map<Employee, Set<Project>> employeeProjects = getProjectSkills(employees, accountModel);
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

	private Map<Employee, Set<Project>> getProjectSkills(final Collection<Employee> employees,
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

	private Map<Employee, Set<Role>> getEmployeesToRolesForContractor(int id) {
		List<Integer> siteIds = accountService.getOperatorIdsForContractor(id);
		Map<Role, Set<Employee>> rolesToEmployees = employeeEntityService.getEmployeesBySiteRoles(siteIds);
		Collection<Project> projects = projectEntityService.getProjectsBySiteIds(siteIds);
		Map<Role, Set<Employee>> projectRolesToEmployees = employeeEntityService.getEmployeesByProjectRoles(projects);

		Map<Role, Set<Employee>> allRolesToEmployees = PicsCollectionUtil.mergeMapOfSets(rolesToEmployees, projectRolesToEmployees);
		return PicsCollectionUtil.invertMapOfSet(allRolesToEmployees);
	}

	public Map<Project, Map<Employee, Set<AccountSkill>>> getProjectEmployeeSkills(final Collection<Employee> employees) {

		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getAllProjectsByEmployees(employees);

		Map<Project, Set<AccountSkill>> projectRequiredSkills = getAllRequiredSkillsForProjects(projectEmployees.keySet());
		Set<Project> projects = projectEmployees.keySet();
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(projects);

		Map<Employee, Set<Role>> employeeRoles = roleEntityService.findByProjectsAndEmployees(projects, employees);
		Set<Role> roles = PicsCollectionUtil.mergeCollectionOfCollections(employeeRoles.values());
		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(roles);

		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = new HashMap<>();
		for (Project project : projects) {
			for (Employee employee : projectEmployees.get(project)) {
				Set<AccountSkill> requiredSkills = new HashSet<>();
				Set<Role> rolesForEmployee = employeeRoles.get(employee);

				requiredSkills.addAll(projectRequiredSkills.get(project));

				for (Role role : projectRoles.get(project)) {
					if (CollectionUtils.isNotEmpty(rolesForEmployee) && rolesForEmployee.contains(role)) {
						requiredSkills.addAll(roleSkills.get(role));
					}
				}

				projectEmployeeSkills.get(project).put(employee, requiredSkills);
			}
		}

		return projectEmployeeSkills;
	}

	private Map<Project, Set<AccountSkill>> getAllRequiredSkillsForProjects(final Collection<Project> projects) {
		Map<Project, Set<AccountSkill>> projectSkills = skillEntityService.getRequiredSkillsForProjects(projects);
		// site and corporate required skills
		Set<Integer> siteIds = getSiteIdsFromProjects(projects);
		Map<Integer, Set<Integer>> siteToCorporates = accountService.getSiteToCorporatesMap(siteIds);
		Map<Project, Set<AccountSkill>> projectSiteCorporateRequiredSkills =
				skillEntityService.getSiteRequiredSkillsByProjects(projects, siteToCorporates);

		return PicsCollectionUtil.mergeMapOfSets(projectSkills, projectSiteCorporateRequiredSkills);
	}

	private Set<Integer> getSiteIdsFromProjects(final Collection<Project> projects) {
		return PicsCollectionUtil.extractPropertyToSet(
				projects,
				new PicsCollectionUtil.PropertyExtractor<Project, Integer>() {
					@Override
					public Integer getProperty(Project project) {
						return project.getAccountId();
					}
				});
	}
}
