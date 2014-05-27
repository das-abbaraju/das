package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.entity.*;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProcessHelper {

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

	public Map<Project, Set<AccountSkill>> allProjectSkills(final Set<Project> projects,
															final Map<Project, Set<AccountSkill>> projectRequiredSkills,
															final Map<Project, Set<Role>> projectRoles,
															final Map<Role, Set<AccountSkill>> roleSkills) {

		Map<Project, Set<AccountSkill>> allProjectSkills = new HashMap<>();
		allProjectSkills = PicsCollectionUtil.mergeMapOfSets(allProjectSkills, projectRequiredSkills);
		allProjectSkills = PicsCollectionUtil.mergeMapOfSets(allProjectSkills, getProjectRoleSkills(projectRoles, roleSkills));

		return PicsCollectionUtil.addKeys(allProjectSkills, projects);
	}

	public Map<Project, Set<AccountSkill>> getProjectRoleSkills(final Map<Project, Set<Role>> projectRoles,
																final Map<Role, Set<AccountSkill>> roleSkills) {
		return PicsCollectionUtil.reduceMapOfCollections(projectRoles, roleSkills);
	}

	public Map<Project, Set<AccountSkill>> getProjectRequiredSkills(final Set<Project> projects) {
		Map<Project, Set<AccountSkill>> projectRequiredSkills = skillEntityService.getRequiredSkillsForProjects(projects);

		return PicsCollectionUtil.addKeys(projectRequiredSkills, projects);
	}

	public Map<Role, Set<AccountSkill>> getRoleSkills(final Set<Role> roles) {
		Map<Role, Set<AccountSkill>> roleSkillsMap = skillEntityService.getSkillsForRoles(roles);

		return PicsCollectionUtil.addKeys(roleSkillsMap, roles);
	}

	public Map<Project, Set<Role>> getProjectRoles(final Set<Project> projects) {
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(projects);

		return PicsCollectionUtil.addKeys(projectRoles, projects);
	}

	public Map<Group, Set<AccountSkill>> getGroupSkills(final Profile profile) {
		return groupEntityService.getGroupSkillsForProfile(profile);
	}

	public Map<Integer, Set<AccountSkill>> contractorRequiredSkills(final Collection<Integer> contractorIds) {
		return skillEntityService.getRequiredSkillsForContractor(contractorIds);
	}

	public Map<Integer, Set<Role>> siteRolesNotInProjects(final Map<Integer, Set<Project>> siteProjects,
														  final Map<Integer, Set<Role>> siteRoles,
														  final Map<Project, Set<Role>> projectRoles) {

		Map<Integer, Set<Role>> prepareRolesNotInProjects = PicsCollectionUtil.copyMapOfSets(siteRoles);
		for (Integer siteId : siteProjects.keySet()) {
			Set<Role> prepareRolesNotInProject = prepareRolesNotInProjects.get(siteId);
			for (Project project : siteProjects.get(siteId)) {
				if (!projectRoles.containsKey(project)) {
					continue;
				}

				prepareRolesNotInProject.removeAll(projectRoles.get(project));
			}

		}

		return prepareRolesNotInProjects;
	}

	public Map<Integer, Set<Role>> getRolesBySite(final Collection<Employee> employees) {
		return roleEntityService.getSiteRolesForEmployees(employees);
	}

	public Map<Integer, Set<Project>> getProjectsBySite(final Set<Project> projects) {
		return PicsCollectionUtil.convertToMapOfSets(projects,

				new PicsCollectionUtil.MapConvertable<Integer, Project>() {

					@Override
					public Integer getKey(Project project) {
						return project.getAccountId();
					}
				});
	}

	public Map<AccountModel, Set<Project>> getSiteProjects(final Map<Integer, AccountModel> siteAccounts,
														   final Map<Integer, Set<Project>> siteProjects) {
		Map<AccountModel, Set<Project>> siteProjectsMap = new HashMap<>();
		for (Integer siteId : siteAccounts.keySet()) {
			siteProjectsMap.put(siteAccounts.get(siteId), siteProjects.get(siteId));
		}

		return siteProjectsMap;
	}

	public Map<Project, Set<AccountSkill>> aggregateAllSkillsForProjects(final Set<Project> projects,
																		 final Map<Integer, AccountModel> siteAccounts,
																		 final Map<Project, Set<AccountSkill>> projectSkills,
																		 final Map<AccountModel, Set<AccountSkill>> siteRequiredSkills) {
		Map<Project, Set<AccountSkill>> allSkillsRequiredForProjects = copyProjectSkills(projectSkills);
		for (Project project : projects) {
			if (!allSkillsRequiredForProjects.containsKey(project)) {
				allSkillsRequiredForProjects.put(project, new HashSet<AccountSkill>());
			}

			allSkillsRequiredForProjects.get(project).addAll(siteRequiredSkills.get(siteAccounts.get(project.getAccountId())));
		}

		return allSkillsRequiredForProjects;
	}

	private Map<Project, Set<AccountSkill>> copyProjectSkills(final Map<Project, Set<AccountSkill>> projectSkills) {
		Map<Project, Set<AccountSkill>> projectSkillsCopy = new HashMap<>();
		for (Project project : projectSkills.keySet()) {
			projectSkillsCopy.put(project, new HashSet<>(projectSkills.get(project)));
		}

		return projectSkillsCopy;
	}

	public Map<AccountModel, Set<AccountSkill>> allSkillsForAllSite(final Map<AccountModel, Set<Project>> accountProjects,
																	final Map<Project, Set<AccountSkill>> allSkillsForProjects,
																	final Map<AccountModel, Set<AccountSkill>> siteRequiredSkills) {
		Map<AccountModel, Set<AccountSkill>> projectSkillsByAccount =
				PicsCollectionUtil.reduceMapOfCollections(accountProjects, allSkillsForProjects);

		return PicsCollectionUtil.mergeMapOfSets(siteRequiredSkills, projectSkillsByAccount);
	}

	public Map<Employee, Set<AccountSkill>> getAllSkillsForEmployees(final int contractorId,
																	 final Collection<Employee> employees,
																	 final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		employeeSkills = PicsCollectionUtil.mergeMapOfSets(employeeSkills, groupSkills(employees));
		employeeSkills = PicsCollectionUtil.mergeMapOfSets(employeeSkills, contractorRequiredSkills(contractorId, employees));
		employeeSkills = PicsCollectionUtil.mergeMapOfSets(employeeSkills, projectRequiredSkills(employees));
		employeeSkills = PicsCollectionUtil.mergeMapOfSets(employeeSkills, siteAssignmentRoleSkills(employees));
		employeeSkills = PicsCollectionUtil.mergeMapOfSets(employeeSkills, siteAndCorporateRequiredSkills(employees, siteHierarchy));

		return employeeSkills;
	}

	public Map<Employee, Set<AccountSkill>> groupSkills(final Collection<Employee> employees) {
		Map<Employee, Set<Group>> employeeGroups = groupEntityService.getEmployeeGroups(employees);

		return PicsCollectionUtil.addKeys(skillEntityService.getGroupSkillsForEmployees(employeeGroups), employees);
	}

	public Map<Employee, Set<AccountSkill>> contractorRequiredSkills(final int contractorId,
																	 final Collection<Employee> employees) {
		Set<AccountSkill> contractorRequiredSkills = skillEntityService.getRequiredSkillsForContractor(contractorId);

		Map<Employee, Set<AccountSkill>> employeeContractorRequiredSkills = new HashMap<>();
		for (Employee employee : employees) {
			employeeContractorRequiredSkills.put(employee, new HashSet<>(contractorRequiredSkills));
		}

		return employeeContractorRequiredSkills;
	}

	public Map<Employee, Set<AccountSkill>> projectRequiredSkills(final Collection<Employee> employees) {
		Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployees(employees);
		Map<Project, Set<AccountSkill>> requiredSkillsForProjects = skillEntityService
				.getRequiredSkillsForProjects(PicsCollectionUtil.flattenCollectionOfCollection(employeeProjects.values()));

		return PicsCollectionUtil.addKeys(
				PicsCollectionUtil.reduceMapOfCollections(employeeProjects, requiredSkillsForProjects),
				employees);
	}

	public Map<Employee, Set<AccountSkill>> siteAssignmentRoleSkills(final Collection<Employee> employees) {
		Map<Employee, Set<Role>> employeeSiteRoles = roleEntityService.getEmployeeSiteRoles(employees);
		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService
				.getSkillsForRoles(PicsCollectionUtil.flattenCollectionOfCollection(employeeSiteRoles.values()));

		return PicsCollectionUtil.addKeys(
				PicsCollectionUtil.reduceMapOfCollections(employeeSiteRoles, roleSkills),
				employees);
	}

	public Map<Employee, Set<AccountSkill>> siteAndCorporateRequiredSkills(final Collection<Employee> employees,
																		   final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		Map<Employee, Set<Integer>> employeeSiteAssignments = employeeEntityService.getEmployeeSiteAssignments(employees);

		// TODO: rewrite and call siteAndCorporateRequiredSkills
		Map<Integer, Set<AccountSkill>> siteAndCorporateRequiredSkills = new HashMap<>();
		for (AccountModel accountModel : siteHierarchy.keySet()) {
			if (CollectionUtils.isEmpty(siteHierarchy.get(accountModel))) {
				continue;
			}

			int siteId = accountModel.getId();
			Set<AccountSkill> requiredSkills = skillEntityService.getSiteAndCorporateRequiredSkills(siteId,
					PicsCollectionUtil.getIdsFromCollection(siteHierarchy.get(accountModel),
							new PicsCollectionUtil.Identitifable<AccountModel, Integer>() {

								@Override
								public Integer getId(AccountModel accountModel) {
									return accountModel.getId();
								}
							}));

			siteAndCorporateRequiredSkills.put(siteId, requiredSkills);
		}

		return PicsCollectionUtil.addKeys(
				PicsCollectionUtil.reduceMapOfCollections(employeeSiteAssignments, siteAndCorporateRequiredSkills),
				employees);
	}

	public Map<AccountModel, Set<Group>> contractorGroups(final Collection<Employee> employees,
														  final Map<Integer, AccountModel> contractorAccounts) {
		Map<AccountModel, Integer> contractors = PicsCollectionUtil.invertMap(contractorAccounts);
		Map<Integer, Set<Group>> contractorGroups = contractorGroups(employees);
		return PicsCollectionUtil.reduceMapsForPairKeyMap(contractors, contractorGroups);
	}

	public Map<Integer, Set<Group>> contractorGroups(final Collection<Employee> employees) {
		return groupEntityService.getGroupsByContractorId(employees);
	}

	public Map<AccountModel, Set<Role>> siteRoles(final Collection<Employee> employees,
												  final Map<Integer, AccountModel> siteAccounts) {
		Map<AccountModel, Integer> sites = PicsCollectionUtil.invertMap(siteAccounts);
		Map<Integer, Set<Role>> siteRoles = siteAssignmentRoles(employees);
		return PicsCollectionUtil.reduceMapsForPairKeyMap(sites, siteRoles);
	}

	public Map<Integer, Set<Role>> siteAssignmentRoles(final Collection<Employee> employees) {
		return roleEntityService.getSiteRolesForEmployees(employees);
	}

	public Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills(final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills = new HashMap<>();
		for (AccountModel accountModel : siteHierarchy.keySet()) {
			if (CollectionUtils.isEmpty(siteHierarchy.get(accountModel))) {
				continue;
			}

			int siteId = accountModel.getId();
			Set<AccountSkill> requiredSkills = skillEntityService.getSiteAndCorporateRequiredSkills(siteId,
					PicsCollectionUtil.getIdsFromCollection(siteHierarchy.get(accountModel),
							new PicsCollectionUtil.Identitifable<AccountModel, Integer>() {

								@Override
								public Integer getId(AccountModel accountModel) {
									return accountModel.getId();
								}
							}));

			siteAndCorporateRequiredSkills.put(accountModel, requiredSkills);
		}

		return siteAndCorporateRequiredSkills;
	}

	public Map<AccountModel, Set<Role>> siteAssignmentRoles(final Map<AccountModel, Integer> sites) {
		Map<Integer, Set<Role>> siteRoles = roleEntityService.getSiteAssignmentRoles(sites.values());

		return PicsCollectionUtil.reduceMapsForPairKeyMap(sites, siteRoles);
	}

	public Map<AccountModel, Map<Employee, Set<Role>>> siteEmployeeRoles(final Map<Integer, AccountModel> sites) {
		Map<Integer, Map<Employee, Set<Role>>> siteEmployeeRoleMap = roleEntityService.getSiteEmployeeRoles(sites.keySet());

		Map<AccountModel, Map<Employee, Set<Role>>> results = new HashMap<>();
		for (Integer siteId : siteEmployeeRoleMap.keySet()) {
			AccountModel accountModel = sites.get(siteId);
			results.put(accountModel, siteEmployeeRoleMap.get(siteId));
		}

		return results;
	}
}
