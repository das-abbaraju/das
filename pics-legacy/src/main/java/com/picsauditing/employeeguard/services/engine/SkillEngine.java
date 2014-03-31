package com.picsauditing.employeeguard.services.engine;

import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectCompanyDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.SkillAssignmentHelper;
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
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private GroupEntityService groupEntityService;
	@Autowired
	private ProjectCompanyDAO projectCompanyDAO;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private SkillAssignmentHelper skillAssignmentHelper;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;

	public void updateSiteSkillsForEmployee(final Employee employee, final int siteId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		List<Integer> childSiteIds = accountService.getChildOperatorIds(corporateIds);
		Set<Integer> otherEmployeeAssignedSiteIds = getOtherEmployeeAssignedSiteIds(childSiteIds, siteId, employee);

		if (CollectionUtils.isEmpty(otherEmployeeAssignedSiteIds)) {
			removeRequiredCorporateSkillsFromEmployee(employee, corporateIds);
		} else {
			final int contractorId = employee.getAccountId();
			// All of the projects the contractor has been assigned to that is not related to the site
			List<ProjectCompany> projectCompanies = projectCompanyDAO.findByContractorExcludingSite(contractorId, siteId);

//			Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
			Set<AccountSkill> requiredSkills = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(projectCompanies, employee);
			Set<AccountSkillEmployee> deletableSkills = skillAssignmentHelper.filterNoLongerNeededEmployeeSkills(employee, contractorId, requiredSkills);

			accountSkillEmployeeDAO.deleteByIds(PicsCollectionUtil.getIdsFromCollection(deletableSkills,
					new PicsCollectionUtil.Identitifable<AccountSkillEmployee, Integer>() {
						@Override
						public Integer getId(AccountSkillEmployee accountSkillEmployee) {
							return accountSkillEmployee.getId();
						}
					}));
		}
	}

	private void removeRequiredCorporateSkillsFromEmployee(Employee employee, List<Integer> corporateIds) {
		List<AccountSkillEmployee> accountSkillEmployees =
				accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(employee.getId(), corporateIds);
		accountSkillEmployeeDAO.delete(accountSkillEmployees);
	}

	private Set<Integer> getOtherEmployeeAssignedSiteIds(List<Integer> childSiteIds, int siteId, Employee employee) {
		if (CollectionUtils.isEmpty(childSiteIds)) {
			return Collections.emptySet();
		}

		childSiteIds.remove(new Integer(siteId));
		Set<Integer> otherSitesEmployeeIsAssignedTo = new HashSet<>();
		for (int childSiteId : childSiteIds) {
			if (isEmployeeAssignedToSite(employee, childSiteId)) {
				otherSitesEmployeeIsAssignedTo.add(childSiteId);
			}
		}

		return otherSitesEmployeeIsAssignedTo;
	}

	private boolean isEmployeeAssignedToSite(Employee employee, int childSiteId) {
		if (CollectionUtils.isEmpty(employee.getRoles())) {
			return false;
		}

		for (RoleEmployee roleEmployee : employee.getRoles()) {
			if (roleEmployee.getRole().getAccountId() == childSiteId) {
				return true;
			}
		}

		return false;
	}

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
