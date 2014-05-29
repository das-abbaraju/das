package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

// TODO replace this class with the new entity service
@Deprecated
public class SkillService {

	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	@Deprecated
	private AccountSkillProfileService accountSkillProfileService;
	@Autowired
	private AccountSkillGroupDAO accountSkillGroupDAO;
	@Autowired
	private AccountSkillRoleDAO accountSkillRoleDAO;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectSkillDAO projectSkillDAO;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;
	@Autowired
	private SkillEntityService skillEntityService;

	@Deprecated
	public AccountSkill getSkill(final String id) {
		return skillEntityService.find(NumberUtils.toInt(id));
	}

	public AccountSkill getSkill(final String id, final int accountId) {
		return accountSkillDAO.findSkillByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<AccountSkill> getSkills(List<Integer> skillIds) {
		return accountSkillDAO.findByIds(skillIds);
	}

	public List<AccountSkill> getSkillsForAccount(int accountId) {
		return accountSkillDAO.findByAccount(accountId);
	}

	public List<AccountSkill> getRequiredSkillsForSite(int siteId) {
		List<SiteSkill> requiredByAccount = siteSkillDAO.findByAccountId(siteId);
		return ExtractorUtil.extractList(requiredByAccount, SiteSkill.SKILL_EXTRACTOR);
	}

	public List<AccountSkill> getRequiredSkillsForSiteAndCorporates(int siteId) {
		List<Integer> siteAndCorporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		siteAndCorporateIds.add(siteId);
		List<SiteSkill> requiredByAccount = getSiteRequiredSkills(siteAndCorporateIds);
		return ExtractorUtil.extractList(requiredByAccount, SiteSkill.SKILL_EXTRACTOR);
	}

	public Map<AccountModel, List<AccountSkill>> getSiteRequiredSkills(int accountId) {
		List<Integer> childOperators = accountService.getChildOperatorIds(accountId);
		List<SiteSkill> requiredByAccounts = getSiteRequiredSkills(childOperators);
		Map<Integer, List<SiteSkill>> siteIdToSkill = extractSiteIdToSiteSkills(requiredByAccounts);

		List<AccountModel> accounts = accountService.getAccountsByIds(siteIdToSkill.keySet());
		Map<AccountModel, List<AccountSkill>> siteRequiredSkills = new TreeMap<>();

		for (AccountModel account : accounts) {
			List<SiteSkill> siteSkills = siteIdToSkill.get(account.getId());
			siteRequiredSkills.put(account, ExtractorUtil.extractList(siteSkills, SiteSkill.SKILL_EXTRACTOR));
		}

		return siteRequiredSkills;
	}

	private Map<Integer, List<SiteSkill>> extractSiteIdToSiteSkills(List<SiteSkill> requiredByAccounts) {
		return PicsCollectionUtil.convertToMapOfLists(requiredByAccounts, new PicsCollectionUtil.MapConvertable<Integer, SiteSkill>() {
			@Override
			public Integer getKey(SiteSkill entity) {
				return entity.getSiteId();
			}
		});
	}

	public List<AccountSkill> getOptionalSkillsForAccount(int accountId) {
		return accountSkillDAO.findOptionalSkillsByAccount(accountId);
	}

	public List<AccountSkill> getOptionalSkillsForAccounts(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.findOptionalSkillsByAccounts(accountIds);
	}

	public List<AccountSkill> getSkillsForAccounts(final List<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.findByAccounts(accountIds);
	}

	public List<AccountSkill> getSkillsForProfile(final Profile profile) {
		return accountSkillDAO.findByProfile(profile);
	}

	@Deprecated
	public AccountSkill save(AccountSkill accountSkill, int accountId, int appUserId) {
		accountSkill.setAccountId(accountId);

		//setPersistedEntitiesOnJoinTables(accountSkill, accountId);

    EntityAuditInfo created = new EntityAuditInfo.Builder().appUserId(appUserId).timestamp(new Date()).build();

		return skillEntityService.save(accountSkill, created);
	}

/*
	private void setPersistedEntitiesOnJoinTables(AccountSkill accountSkill, int accountId) {
		List<String> groupNames = new ArrayList<>();
		for (AccountSkillGroup accountSkillGroup : accountSkill.getGroups()) {
			groupNames.add(accountSkillGroup.getGroup().getName());
		}

		if (CollectionUtils.isEmpty(groupNames)) {
			return;
		}

		List<Group> persistedGroups = accountGroupDAO.findGroupByAccountIdAndNames(accountId, groupNames);
		for (Group persistedGroup : persistedGroups) {
			for (AccountSkillGroup accountSkillGroup : accountSkill.getGroups()) {
				if (persistedGroup.getName().equals(accountSkillGroup.getGroup().getName())) {
					accountSkillGroup.setGroup(persistedGroup);
				}
			}
		}
	}
*/

	@Deprecated
	public AccountSkill update(AccountSkill updatedAccountSkill, String id, int accountId, int appUserId) {
		updatedAccountSkill.setId(NumberUtils.toInt(id));
		EntityAuditInfo updated = new EntityAuditInfo.Builder()
				.appUserId(appUserId)
				.timestamp(new Date())
				.build();

		AccountSkill accountSkillInDatabase = skillEntityService.update(updatedAccountSkill, updated);

		if (SessionInfoProviderFactory.getSessionInfoProvider().getPermissions().isContractor()) {
			updateAccountSkillGroups(accountSkillInDatabase, updatedAccountSkill, appUserId);

			// If we're making this skill required then we can't associate this skill with groups
			if (accountSkillInDatabase.getRuleType().isRequired()) {
				accountSkillInDatabase.getGroups().clear();
			}
		} else {
			updateAccountSkillRoles(accountSkillInDatabase, updatedAccountSkill, appUserId);
		}

		EntityHelper.setUpdateAuditFields(accountSkillInDatabase, updated);

		return accountSkillDAO.save(accountSkillInDatabase);
	}

	private void updateAccountSkillGroups(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {
		List<AccountSkillGroup> accountSkillGroups = getLinkedGroups(accountSkillInDatabase, updatedSkill, appUserId);
		accountSkillInDatabase.getGroups().clear();
		accountSkillInDatabase.getGroups().addAll(accountSkillGroups);
	}

	private void updateAccountSkillRoles(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {
		List<AccountSkillRole> accountSkillRoles = getLinkedRoles(accountSkillInDatabase, updatedSkill, appUserId);
		accountSkillInDatabase.getRoles().clear();
		accountSkillInDatabase.getRoles().addAll(accountSkillRoles);
	}

	public void setRequiredSkillsForSite(List<AccountSkill> requiredSkills, String id, int appUserID) {
		List<SiteSkill> newSiteSkills = new ArrayList<>();
		int siteId = NumberUtils.toInt(id);
		Date now = new Date();

		for (AccountSkill requiredSkill : requiredSkills) {
			SiteSkill siteSkill = new SiteSkill(siteId, requiredSkill);
			newSiteSkills.add(siteSkill);
			EntityHelper.setCreateAuditFields(siteSkill, appUserID, now);
		}

		List<SiteSkill> existingSiteSkills = siteSkillDAO.findByAccountId(siteId);
		BaseEntityCallback<SiteSkill> skillCallback = new BaseEntityCallback<>(appUserID, now);
		newSiteSkills = IntersectionAndComplementProcess.intersection(newSiteSkills, existingSiteSkills, SiteSkill.COMPARATOR, skillCallback);

		siteSkillDAO.save(newSiteSkills);
		siteSkillDAO.delete(skillCallback.getRemovedEntities());
	}

	private List<AccountSkillGroup> getLinkedGroups(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {

    List<AccountSkillGroup> accountSkillGroupsWithGroupEntitiesFromDB = new ArrayList<>();
    List<Integer> groupIds = getGroupIds(updatedSkill.getGroups());
    List<Group> groups = accountGroupDAO.findGroupByAccountIdAndIds(updatedSkill.getAccountId(), groupIds);
    for (Group group : groups) {
      accountSkillGroupsWithGroupEntitiesFromDB.add(new AccountSkillGroup(group, accountSkillInDatabase));
    }

		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(accountSkillGroupsWithGroupEntitiesFromDB,
				accountSkillInDatabase.getGroups(), AccountSkillGroup.COMPARATOR, callback);

		return accountSkillGroups;
	}

	private List<AccountSkillRole> getLinkedRoles(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {

    List<AccountSkillRole> accountSkillRolesWithRoleEntitiesFromDB = new ArrayList<>();
    List<Integer> roleIds = getRoleIds(updatedSkill.getRoles());
    List<Role> roles = roleDAO.findRoleByAccountIdsAndIds(Arrays.asList(updatedSkill.getAccountId()), roleIds);
    for (Role role : roles) {
      accountSkillRolesWithRoleEntitiesFromDB.add(new AccountSkillRole(role, accountSkillInDatabase));
    }

		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountSkillRole> accountSkillRoles = IntersectionAndComplementProcess.intersection(accountSkillRolesWithRoleEntitiesFromDB,
				accountSkillInDatabase.getRoles(), AccountSkillRole.COMPARATOR, callback);


		return accountSkillRoles;
	}

	private List<Integer> getGroupIds(List<AccountSkillGroup> AccountSkillGroups) {
		if (CollectionUtils.isEmpty(AccountSkillGroups)) {
			return Collections.emptyList();
		}

		List<Integer> groupIds = new ArrayList<>();
		for (AccountSkillGroup AccountSkillGroup : AccountSkillGroups) {
			groupIds.add(AccountSkillGroup.getGroup().getId());
		}

		return groupIds;
	}

	private List<Integer> getRoleIds(List<AccountSkillRole> accountSkillRoles) {
		if (CollectionUtils.isEmpty(accountSkillRoles)) {
			return Collections.emptyList();
		}

		List<Integer> roleIds = new ArrayList<>();
		for (AccountSkillRole accountSkillRole : accountSkillRoles) {
			roleIds.add(accountSkillRole.getRole().getId());
		}

		return roleIds;
	}

	@Deprecated
	public void delete(String id, int accountId, int appUserId) {
		skillEntityService.deleteById(NumberUtils.toInt(id));
	}

	@Deprecated
	public List<AccountSkill> search(String searchTerm, int accountId) {
		return skillEntityService.search(searchTerm, accountId);
	}

	public List<AccountSkill> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.search(searchTerm, accountIds);
	}

	public Map<AccountSkill, Set<Role>> getProjectRoleSkillsMap(final Employee employee) {
		List<AccountSkillRole> projectSkillRoles = accountSkillRoleDAO.findProjectRoleSkillsByEmployee(employee);

		return PicsCollectionUtil.convertToMapOfSets(projectSkillRoles,
				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillRole, AccountSkill, Role>() {

					@Override
					public AccountSkill getKey(AccountSkillRole entity) {
						return entity.getSkill();
					}

					@Override
					public Role getValue(AccountSkillRole entity) {
						return entity.getRole();
					}
				});
	}

	public Map<AccountSkill, Set<Integer>> getCorporateSkillsForProjects(final Collection<Project> projects) {
		return getSiteSkillsForProjects(getCorporateIds(getAccountIds(projects)));
	}


	public Map<AccountSkill, Set<Integer>> getSiteSkillsForProjects(final Collection<Project> projects) {
		return getSiteSkillsForProjects(getAccountIds(projects));
	}

	public Map<AccountSkill, Set<Project>> getProjectRequiredSkillsMap(final Collection<Project> projects) {
		List<ProjectSkill> projectSkills = projectSkillDAO.findByProjects(projects);

		return PicsCollectionUtil.convertToMapOfSets(projectSkills, new PicsCollectionUtil.EntityKeyValueConvertable<ProjectSkill, AccountSkill, Project>() {
			@Override
			public AccountSkill getKey(ProjectSkill entity) {
				return entity.getSkill();
			}

			@Override
			public Project getValue(ProjectSkill entity) {
				return entity.getProject();
			}
		});
	}

	public Map<AccountSkill, Set<Group>> getSkillGroups(final List<Group> groups) {
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyMap();
		}

		List<AccountSkillGroup> accountSkillGroups = accountSkillGroupDAO.findByGroups(groups);

		return PicsCollectionUtil.convertToMapOfSets(accountSkillGroups,
				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillGroup, AccountSkill, Group>() {

					@Override
					public AccountSkill getKey(AccountSkillGroup accountSkillGroup) {
						return accountSkillGroup.getSkill();
					}

					@Override
					public Group getValue(AccountSkillGroup accountSkillGroup) {
						return accountSkillGroup.getGroup();
					}
				});
	}

	private Map<AccountSkill, Set<Integer>> getSiteSkillsForProjects(final Set<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyMap();
		}

		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountIds(accountIds);

		return getSkillMapFromSiteSkills(siteSkills);
	}

	private Set<Integer> getAccountIds(Collection<Project> projects) {
		return PicsCollectionUtil.getIdsFromCollection(projects, new PicsCollectionUtil.Identitifable<Project, Integer>() {

			@Override
			public Integer getId(Project element) {
				return Integer.valueOf(element.getAccountId());
			}
		});
	}

	private Set<Integer> getCorporateIds(final Set<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptySet();
		}

		Set<Integer> corporateIds = new HashSet<>();
		for (int accountId : accountIds) {
			corporateIds.addAll(accountService.getTopmostCorporateAccountIds(accountId));
		}

		return corporateIds;
	}

	private Map<AccountSkill, Set<Integer>> getSkillMapFromSiteSkills(List<SiteSkill> siteSkills) {
		return PicsCollectionUtil.convertToMapOfSets(siteSkills, new PicsCollectionUtil.EntityKeyValueConvertable<SiteSkill, AccountSkill, Integer>() {
			@Override
			public AccountSkill getKey(SiteSkill siteSkill) {
				return siteSkill.getSkill();
			}

			@Override
			public Integer getValue(SiteSkill siteSkill) {
				return siteSkill.getSiteId();
			}
		});
	}

	public Map<AccountSkill, Set<Integer>> getSiteAssignmentSkills(final Employee employee) {
		List<SiteAssignment> siteAssignmentsForEmployee = siteAssignmentDAO.findByEmployee(employee);

		Map<AccountSkill, Set<Integer>> siteAssignmentSkills = new HashMap<>();
		for (SiteAssignment siteAssignment : siteAssignmentsForEmployee) {
			for (AccountSkillRole accountSkillRole : siteAssignment.getRole().getSkills()) {
				PicsCollectionUtil.addToMapOfKeyToSet(siteAssignmentSkills, accountSkillRole.getSkill(), siteAssignment.getSiteId());
			}
		}

		return siteAssignmentSkills;
	}

	public List<AccountSkill> getParentSiteRequiredSkills(int accountId) {
		List<Integer> parentIds = accountService.getTopmostCorporateAccountIds(accountId);
		List<SiteSkill> requiredSkills = siteSkillDAO.findByAccountIds(parentIds);
		return ExtractorUtil.extractList(requiredSkills, SiteSkill.SKILL_EXTRACTOR);
	}

	private List<SiteSkill> getSiteRequiredSkills(final List<Integer> parentIds) {
		if (CollectionUtils.isEmpty(parentIds)) {
			return Collections.emptyList();
		}

		return siteSkillDAO.findByAccountIds(parentIds);
	}

	public List<AccountSkill> getSkillsForRole(final Role role) {
		return accountSkillDAO.findByRoles(Arrays.asList(role));
	}

	public Map<Project, Set<AccountSkill>> getAllProjectSkillsForEmployeeProjectRoles(final int siteId,
																					  final Map<Project, Set<Role>> projectRoleMap) {
		if (MapUtils.isEmpty(projectRoleMap)) {
			return Collections.emptyMap();
		}

		Map<Role, Set<AccountSkill>> roleSkillMap = getSkillsForRoles(siteId,
				PicsCollectionUtil.extractAndFlattenValuesFromMap(projectRoleMap));

		Map<Project, Set<AccountSkill>> projectSkillMap = new HashMap<>();

		for (Project project : projectRoleMap.keySet()) {
			if (!projectSkillMap.containsKey(project)) {
				projectSkillMap.put(project, new HashSet<AccountSkill>());
			}

			for (Role role : projectRoleMap.get(project)) {
				if (roleSkillMap.containsKey(role)) {
					projectSkillMap.get(project).addAll(roleSkillMap.get(role));
				}
			}
		}

		return appendProjectRequiredSkills(projectSkillMap, getProjectsRequiredSkills(projectRoleMap.keySet()));
	}

	private Map<Project, Set<AccountSkill>> getProjectsRequiredSkills(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectSkillDAO.findByProjects(projects),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectSkill, Project, AccountSkill>() {
					@Override
					public Project getKey(ProjectSkill projectSkill) {
						return projectSkill.getProject();
					}

					@Override
					public AccountSkill getValue(ProjectSkill projectSkill) {
						return projectSkill.getSkill();
					}
				});
	}

	private Map<Project, Set<AccountSkill>> appendProjectRequiredSkills(final Map<Project, Set<AccountSkill>> projectSkillMap,
																		final Map<Project, Set<AccountSkill>> projectRequiredSkillsMap) {
		if (MapUtils.isEmpty(projectRequiredSkillsMap) || MapUtils.isEmpty(projectSkillMap)) {
			return projectSkillMap;
		}

		for (Project project : projectSkillMap.keySet()) {
			if (projectRequiredSkillsMap.containsKey(project)) {
				projectSkillMap.get(project).addAll(projectRequiredSkillsMap.get(project));
			}
		}

		return projectSkillMap;
	}

	/**
	 * Returns a map of Corporate Skills with
	 *
	 * @param siteId
	 * @param corporateRoles
	 * @return
	 */
	public Map<Role, Set<AccountSkill>> getSkillsForRoles(final int siteId, final Collection<Role> corporateRoles) {
		if (CollectionUtils.isEmpty(corporateRoles)) {
			return Collections.emptyMap();
		}

		Map<Role, Set<AccountSkill>> roleSkillsMap = PicsCollectionUtil.convertToMapOfSets(
				accountSkillRoleDAO.findSkillsByRoles(corporateRoles),
				new PicsCollectionUtil.EntityKeyValueConvertable<AccountSkillRole, Role, AccountSkill>() {

					@Override
					public Role getKey(AccountSkillRole accountSkillRole) {
						return accountSkillRole.getRole();
					}

					@Override
					public AccountSkill getValue(AccountSkillRole accountSkillRole) {
						return accountSkillRole.getSkill();
					}
				});

		roleSkillsMap = populateRoleSkillsMapIfEmpty(corporateRoles, roleSkillsMap);

		return appendSiteAndCorporateSkills(roleSkillsMap, getRequiredSkillsForSiteAndCorporates(siteId));
	}

	private Map<Role, Set<AccountSkill>> populateRoleSkillsMapIfEmpty(final Collection<Role> corporateRoles,
																	  final Map<Role, Set<AccountSkill>> roleSkillsMap) {
		if (MapUtils.isNotEmpty(roleSkillsMap)) {
			return roleSkillsMap;
		}

		Map<Role, Set<AccountSkill>> newRoleSkillMap = new HashMap<>();
		for (Role corporateRole : corporateRoles) {
			newRoleSkillMap.put(corporateRole, new HashSet<AccountSkill>());
		}

		return newRoleSkillMap;
	}

	private <E> Map<E, Set<AccountSkill>> appendSiteAndCorporateSkills(final Map<E, Set<AccountSkill>> entitySkillMap,
																	   final List<AccountSkill> siteAndCorporateRequiredSkills) {
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
}
