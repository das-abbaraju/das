package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

// For now, a place to put some simple, common operations between processes
class ProcessHelper {

	@Autowired
	private AccountService accountService;
	@Autowired
	private GroupEntityService groupEntityService;
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

	public Map<Project, Set<AccountSkill>> addProjectRequiredSkills(final Map<Project, Set<AccountSkill>> allProjectSkills,
																	final Map<Project, Set<AccountSkill>> projectRequiredSkills) {
		return PicsCollectionUtil.mergeMapOfSets(allProjectSkills, projectRequiredSkills);
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

	public Map<AccountModel, Set<AccountSkill>> buildSiteRequiredSkillsMap(final Map<AccountModel, Set<AccountModel>> parentSitesMap) {
		Map<AccountModel, Set<AccountSkill>> siteRequiredSkillsMap = new HashMap<>();
		for (AccountModel site : parentSitesMap.keySet()) {
			Set<AccountSkill> requiredSkills = skillEntityService.getSiteAndCorporateRequiredSkills(site.getId(),
					PicsCollectionUtil.getIdsFromCollection(parentSitesMap.get(site),

							new PicsCollectionUtil.Identitifable<AccountModel, Integer>() {

								@Override
								public Integer getId(AccountModel accountModel) {
									return accountModel.getId();
								}
							}));

			siteRequiredSkillsMap.put(site, requiredSkills);
		}

		// Add Role Required Skills

		return siteRequiredSkillsMap;
	}

//	Map<Project, Set<Role>> projectRoles = getProjectRoles(projects);
//	Map<Integer, Set<Role>> siteAssignmentRoles;
//	Map<Integer, Map<Project, Set<Role>>> siteProjectRoles; // Could be transformed from

	public Map<AccountModel, Set<AccountModel>> buildParentAccountMapForSites(final Map<Integer, AccountModel> sites) {
		return accountService.getSiteParentAccounts(sites.keySet());
	}

	public Map<Integer, Set<Role>> siteRolesNotInProjects(final Map<Integer, Set<Project>> siteProjects,
														  final Map<Integer, Set<Role>> siteRoles,
														  final Map<Project, Set<Role>> projectRoles) {

		Map<Integer, Set<Role>> rolesNotInProjects = new HashMap<>();
		for (Integer siteId : siteProjects.keySet()) {
			Set<Role> rolesNotInProject = new HashSet<>(siteRoles.get(siteId));
			for (Project project : siteProjects.get(siteId)) {
				if (!projectRoles.containsKey(project)) {
					continue;
				}

				rolesNotInProject.removeAll(projectRoles.get(project));
			}

			rolesNotInProjects.put(siteId, rolesNotInProject);
		}

		return rolesNotInProjects;
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
		Map<Project, Set<AccountSkill>> allSkillsRequiredForProjects = new HashMap<>(projectSkills);
		for (Project project : projects) {
			if (!allSkillsRequiredForProjects.containsKey(project)) {
				allSkillsRequiredForProjects.put(project, new HashSet<AccountSkill>());
			}

			allSkillsRequiredForProjects.get(project).addAll(siteRequiredSkills.get(siteAccounts.get(project.getAccountId())));
		}

		return allSkillsRequiredForProjects;
	}

	public Map<AccountModel, Set<AccountSkill>> allSkillsForAllSite(final Map<AccountModel, Set<Project>> accountProjects,
																	final Map<Project, Set<AccountSkill>> allSkillsForProjects,
																	final Map<AccountModel, Set<AccountSkill>> siteRequiredSkills) {
		Map<AccountModel, Set<AccountSkill>> projectSkillsByAccount =
				PicsCollectionUtil.reduceMapOfCollections(accountProjects, allSkillsForProjects);

		return PicsCollectionUtil.mergeMapOfSets(siteRequiredSkills, projectSkillsByAccount);
	}
}
