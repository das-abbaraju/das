package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProfileSkillStatusProcess {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	@Autowired
	private ProcessHelper processHelper;

	// Required Skills contains all the group skills, Site and Corporate Required Skills, Skills from Roles
	// the person is assigned to at the Site

	// Only Project required skills and skills for roles under a project show up listed under the project

	public ProfileSkillData buildProfileSkillData(final Profile profile) {
		Set<Project> projects = findProjects(profile);
		Set<Role> roles = findRoles(profile);
		Map<Project, Set<Role>> projectRoles = processHelper.getProjectRoles(projects);
		Map<Role, Set<AccountSkill>> roleSkills = processHelper.getRoleSkills(roles);
		Map<Project, Set<AccountSkill>> projectRequiredSkills = processHelper.getProjectRequiredSkills(projects);

		ProfileSkillData profileSkillData = new ProfileSkillData();

		profileSkillData.setAllProjectSkills(processHelper.allProjectSkills(projects,
				projectRequiredSkills, projectRoles, roleSkills));

		profileSkillData = addAccountInformation(profileSkillData, profile);
		profileSkillData = addAccountRequiredSkills(profileSkillData, profile, projects, projectRoles, roleSkills);
		profileSkillData = addSiteProjects(profileSkillData, projects);
		profileSkillData = addProjectStatus(profileSkillData, projects, profile);
		profileSkillData = addAccountStatus(profileSkillData, profile);
		profileSkillData = addAllSkillStatuses(profileSkillData, profile);
		profileSkillData = addOverallSkillStatus(profileSkillData);

		return profileSkillData;
	}

	private ProfileSkillData addOverallSkillStatus(final ProfileSkillData profileSkillData) {
		profileSkillData.setOverallStatus(statusCalculatorService
				.calculateOverallStatus(profileSkillData.getSkillStatusMap().values()));

		return profileSkillData;
	}

	private ProfileSkillData addAllSkillStatuses(final ProfileSkillData profileSkillData, final Profile profile) {
		Map<AccountSkill, SkillStatus> allSkillStatuses = statusCalculatorService
				.getSkillStatuses(profile.getEmployees().get(0), aggregateAllSkills(profileSkillData));

		profileSkillData.setSkillStatusMap(allSkillStatuses);

		return profileSkillData;
	}

	private Set<AccountSkill> aggregateAllSkills(final ProfileSkillData profileSkillData) {
		Set<AccountSkill> allSkills = new HashSet<>();

		allSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(profileSkillData.getAllProjectSkills().values()));
		allSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(profileSkillData.getAllRequiredSkills().values()));

		return allSkills;
	}

	private ProfileSkillData addAccountStatus(final ProfileSkillData profileSkillData,
											  final Profile profile) {
		Map<AccountModel, Set<AccountSkill>> allSiteSkills =
				processHelper.allSkillsForAllSite(profileSkillData.getSiteProjects(),
						profileSkillData.getAllProjectSkills(), profileSkillData.getAllRequiredSkills());

		profileSkillData.setSiteStatuses(statusCalculatorService.getSkillStatusPerEntity(profile.getEmployees().get(0), allSiteSkills));

		return profileSkillData;
	}

	private ProfileSkillData addProjectStatus(final ProfileSkillData profileSkillData,
											  final Set<Project> projects,
											  final Profile profile) {


		Map<Project, Set<AccountSkill>> allSkillsRequiredByProject =
				processHelper.aggregateAllSkillsForProjects(projects, profileSkillData.getSiteAccounts(),
						profileSkillData.getAllProjectSkills(), profileSkillData.getAllRequiredSkills());

		profileSkillData.setProjectStatuses(statusCalculatorService
				.getSkillStatusPerEntity(profile.getEmployees().get(0), allSkillsRequiredByProject));

		return profileSkillData;
	}

	private ProfileSkillData addSiteProjects(final ProfileSkillData profileSkillData,
											 final Set<Project> projects) {
		profileSkillData.setSiteProjects(processHelper.getSiteProjects(profileSkillData.getSiteAccounts(),
				processHelper.getProjectsBySite(projects)));

		return profileSkillData;
	}

	private ProfileSkillData addAccountRequiredSkills(final ProfileSkillData profileSkillData,
													  final Profile profile,
													  final Set<Project> projects,
													  final Map<Project, Set<Role>> projectRoles,
													  final Map<Role, Set<AccountSkill>> roleSkills) {
		// Add Contractor Required Skills
		Map<AccountModel, Set<AccountSkill>> allContractorSkills = buildContractorSkillsMap(profileSkillData, profile);

		// Add Corp/Site Required Skills + Site Assignment Roles Skills not part of projects
		Map<AccountModel, Set<AccountSkill>> allSiteRequiredSkills =
				buildSiteRequiredSkillsMap(profileSkillData, profile, projects, projectRoles, roleSkills);

		profileSkillData.setAllRequiredSkills(PicsCollectionUtil.mergeMapOfSets(allContractorSkills,
				allSiteRequiredSkills));

		return profileSkillData;  //To change body of created methods use File | Settings | File Templates.
	}

	private Map<AccountModel, Set<AccountSkill>> buildSiteRequiredSkillsMap(final ProfileSkillData profileSkillData,
																			final Profile profile,
																			final Set<Project> projects,
																			final Map<Project, Set<Role>> projectRoles,
																			final Map<Role, Set<AccountSkill>> roleSkills) {
		Map<AccountModel, Set<AccountSkill>> siteRequiredSkills = findAllRequiredSkillsForEachSite(profileSkillData.getParentSites());
		Map<AccountModel, Set<AccountSkill>> roleSkillsNotForProjects =
				findAllRoleSkillsNotForProjects(profileSkillData.getSiteAccounts(), projects, projectRoles,
						roleSkills, profile);

		return PicsCollectionUtil.mergeMapOfSets(siteRequiredSkills, roleSkillsNotForProjects);
	}

	private Map<AccountModel, Set<AccountSkill>> findAllRequiredSkillsForEachSite(final Map<AccountModel, Set<AccountModel>> parentSitesMap) {
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

		return siteRequiredSkillsMap;
	}

	private Map<AccountModel, Set<AccountSkill>> findAllRoleSkillsNotForProjects(final Map<Integer, AccountModel> siteAccounts,
																				 final Set<Project> projects,
																				 final Map<Project, Set<Role>> projectRoles,
																				 final Map<Role, Set<AccountSkill>> roleSkills,
																				 final Profile profile) {
		Map<Integer, Set<Project>> siteProjects = processHelper.getProjectsBySite(projects);
		Map<Integer, Set<Role>> siteRoles = processHelper.getRolesBySite(profile.getEmployees());
		Map<Integer, Set<Role>> siteRolesNotInProjects = processHelper
				.siteRolesNotInProjects(siteProjects, siteRoles, projectRoles);

		Map<AccountModel, Set<AccountSkill>> roleSkillsNotForProjects = new HashMap<>();
		for (Integer siteId : siteRolesNotInProjects.keySet()) {
			Set<AccountSkill> skillsForRole = roleSkills.get(siteRolesNotInProjects.get(siteId));
			if (skillsForRole == null) {
				skillsForRole = new HashSet<>();
			}

			roleSkillsNotForProjects.put(siteAccounts.get(siteId), skillsForRole);
		}

		return roleSkillsNotForProjects;
	}

	private Map<AccountModel, Set<AccountSkill>> buildContractorSkillsMap(final ProfileSkillData profileSkillData,
																		  final Profile profile) {

		Map<Integer, Set<AccountSkill>> contractorSkillsForGroups = getGroupSkillsByContractorAccountId(profile);

		Map<Integer, AccountModel> contractorAccounts = profileSkillData.getContractorAccounts();
		Map<Integer, Set<AccountSkill>> contractorRequiredSkills =
				processHelper.contractorRequiredSkills(contractorAccounts.keySet());

		Map<Integer, Set<AccountSkill>> allContractorSkillsByAccountId = PicsCollectionUtil
				.mergeMapOfSets(contractorSkillsForGroups, contractorRequiredSkills);

		Map<AccountModel, Set<AccountSkill>> contractorRequiredSkill = new HashMap<>();
		for (Integer accountId : contractorAccounts.keySet()) {
			Set<AccountSkill> allContractorSkills = allContractorSkillsByAccountId.get(accountId);
			if (allContractorSkills == null) {
				allContractorSkills = new HashSet<>();
			}

			contractorRequiredSkill.put(contractorAccounts.get(accountId), allContractorSkills);
		}

		return contractorRequiredSkill;
	}

	private Map<Integer, Set<AccountSkill>> getGroupSkillsByContractorAccountId(final Profile profile) {
		Map<Group, Set<AccountSkill>> groupSkills = processHelper.getGroupSkills(profile);
		return PicsCollectionUtil.transformMap(groupSkills,

				new PicsCollectionUtil.KeyTransformable<Group, Integer>() {

					@Override
					public Integer getNewKey(Group group) {
						return group.getAccountId();
					}
				});
	}

	private ProfileSkillData addAccountInformation(final ProfileSkillData profileSkillData, final Profile profile) {
		Set<Integer> contractorIds = allContractors(profile);
		Map<Integer, AccountModel> contractorAccounts = accountService.getIdToAccountModelMap(contractorIds);

		Set<Integer> siteIds = allSites(profile);
		Map<Integer, AccountModel> siteAccounts = accountService.getIdToAccountModelMap(siteIds);

		profileSkillData.setContractorAccounts(contractorAccounts);
		profileSkillData.setSiteAccounts(siteAccounts);
		profileSkillData.setParentSites(accountService.getSiteParentAccounts(siteIds));
		profileSkillData.setAllAccounts(PicsCollectionUtil.mergeMaps(contractorAccounts, siteAccounts));

		return profileSkillData;
	}

	private Set<Integer> allContractors(final Profile profile) {
		return PicsCollectionUtil.extractPropertyToSet(profileEntityService.getEmployeesForProfile(profile),

				new PicsCollectionUtil.PropertyExtractor<Employee, Integer>() {
					@Override
					public Integer getProperty(Employee employee) {
						return employee.getAccountId();
					}
				});
	}

	private Set<Integer> allSites(final Profile profile) {
		return profileEntityService.getSiteAssignments(profile);
	}

	private Map<Integer, Set<AccountSkill>> getAllRequiredSkills(final Set<Integer> siteAndCorporateIds) {
		return skillEntityService.getSiteRequiredSkills(siteAndCorporateIds);
	}

	private Set<Project> findProjects(final Profile profile) {
		return profileEntityService.findProjectsForProfile(profile);
	}

	private Set<Role> findRoles(final Profile profile) {
		return roleEntityService.getRolesForProfile(profile);
	}

	// TODO: Move these methods to another class
}
