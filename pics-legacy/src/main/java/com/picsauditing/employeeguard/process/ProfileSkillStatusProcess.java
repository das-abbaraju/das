package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProfileSkillStatusProcess {

	@Autowired
	private AccountService accountService;
	@Autowired
	private GroupEntityService groupEntityService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;

	// Required Skills contains all the group skills, Site and Corporate Required Skills, Skills from Roles
	// the person is assigned to at the Site

	// Only Project required skills and skills for roles under a project show up listed under the project

	public ProfileSkillData buildProfileSkillData(final Profile profile) {
		ProfileSkillData profileSkillData = new ProfileSkillData();

		Set<Project> projects = findProjects(profile);
		Set<Role> roles = findRoles(profile);

		profileSkillData = addAccountInformation(profileSkillData, profile);

		return profileSkillData;
	}

	private ProfileSkillData addAccountInformation(final ProfileSkillData profileSkillData, final Profile profile) {
		Set<Integer> contractorIds = allContractors(profile);
		Set<Integer> siteIds = allSites(profile);
		Set<Integer> corporateIds = new HashSet<>(accountService.getTopmostCorporateAccountIds(siteIds));

		Map<Integer, AccountModel> contractorAccounts = accountService.getIdToAccountModelMap(contractorIds);

		Set<Integer> siteAndCorporateIds = new HashSet<>(siteIds);
		siteAndCorporateIds.addAll(corporateIds);
		Map<Integer, AccountModel> siteAndCorporateAccounts = accountService.getIdToAccountModelMap(siteAndCorporateIds);

		profileSkillData.setContractorAccounts(contractorAccounts);
		profileSkillData.setSiteAndCorporateAccounts(siteAndCorporateAccounts);
		profileSkillData.setAllAccounts(PicsCollectionUtil.mergeMaps(contractorAccounts, siteAndCorporateAccounts));

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

	private Map<Project, Set<AccountSkill>> allProjectSkills(final Set<Project> projects,
															 final Map<Project, Set<AccountSkill>> projectRequiredSkills,
															 final Map<Project, Set<Role>> projectRoles,
															 final Map<Role, Set<AccountSkill>> roleSkills) {

		Map<Project, Set<AccountSkill>> allProjectSkills = new HashMap<>();
		allProjectSkills = PicsCollectionUtil.mergeMapOfSets(allProjectSkills, projectRequiredSkills);
		allProjectSkills = PicsCollectionUtil.mergeMapOfSets(allProjectSkills, getProjectRoleSkills(projectRoles, roleSkills));

		return PicsCollectionUtil.addKeys(allProjectSkills, projects);
	}

	private Map<Project, Set<AccountSkill>> addProjectRequiredSkills(final Map<Project, Set<AccountSkill>> allProjectSkills,
																	 final Map<Project, Set<AccountSkill>> projectRequiredSkills) {
		return PicsCollectionUtil.mergeMapOfSets(allProjectSkills, projectRequiredSkills);
	}

	private Map<Project, Set<AccountSkill>> getProjectRoleSkills(final Map<Project, Set<Role>> projectRoles,
																 final Map<Role, Set<AccountSkill>> roleSkills) {
		return PicsCollectionUtil.reduceMapOfCollections(projectRoles, roleSkills);
	}

	private Map<Project, Set<AccountSkill>> getProjectRequiredSkills(final Set<Project> projects) {
		Map<Project, Set<AccountSkill>> projectRequiredSkills = skillEntityService.getRequiredSkillsForProjects(projects);

		return PicsCollectionUtil.addKeys(projectRequiredSkills, projects);
	}

	private Map<Role, Set<AccountSkill>> getRoleRequiredSkills(final Set<Role> roles) {
		Map<Role, Set<AccountSkill>> roleSkillsMap = skillEntityService.getSkillsForRoles(roles);

		return PicsCollectionUtil.addKeys(roleSkillsMap, roles);
	}

	private Map<Project, Set<Role>> getProjectRoles(final Set<Project> projects) {
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(projects);

		return PicsCollectionUtil.addKeys(projectRoles, projects);
	}

	private Map<Group, Set<AccountSkill>> getGroupSkills(final Profile profile) {
		return groupEntityService.getGroupSkillsForProfile(profile);
	}

	private Map<Integer, Set<AccountSkill>> contractorRequiredSkills(final Collection<Integer> contractorIds) {
		return skillEntityService.getRequiredSkillsForContractor(contractorIds);
	}
}
