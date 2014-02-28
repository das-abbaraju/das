package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
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
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	@Deprecated
	private AccountSkillEmployeeService accountSkillEmployeeService;
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
	private RoleDAO roleDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	@Autowired
	private com.picsauditing.employeeguard.services.entity.SkillService skillEntityService;

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
		List<SiteSkill> requiredByAccount = siteSkillDAO.findByAccountIds(siteAndCorporateIds);
		return ExtractorUtil.extractList(requiredByAccount, SiteSkill.SKILL_EXTRACTOR);
	}

	public Map<AccountModel, List<AccountSkill>> getSiteRequiredSkills(int accountId) {
		List<Integer> childOperators = accountService.getChildOperatorIds(accountId);
		List<SiteSkill> requiredByAccounts = siteSkillDAO.findByAccountIds(childOperators);
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
		return Utilities.convertToMapOfLists(requiredByAccounts, new Utilities.MapConvertable<Integer, SiteSkill>() {
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
		return accountSkillDAO.findOptionalSkillsByAccounts(accountIds);
	}

	public List<AccountSkill> getSkillsForAccounts(List<Integer> accountIds) {
		return accountSkillDAO.findByAccounts(accountIds);
	}

	public List<AccountSkill> getSkillsForProfile(final Profile profile) {
		return accountSkillDAO.findByProfile(profile);
	}

	@Deprecated
	public AccountSkill save(AccountSkill accountSkill, int accountId, int appUserId) {
		accountSkill.setAccountId(accountId);

		setPersistedEntitiesOnJoinTables(accountSkill, accountId);

		EntityAuditInfo created = new EntityAuditInfo.Builder().appUserId(appUserId).timestamp(new Date()).build();
		EntityHelper.setCreateAuditFields(accountSkill.getGroups(), created);

		accountSkill = skillEntityService.save(accountSkill, created);
		// FIXME Move this to SkillEngine
		accountSkillEmployeeService.linkEmployeesToSkill(accountSkill, appUserId);
		return accountSkill;
	}

	private void setPersistedEntitiesOnJoinTables(AccountSkill accountSkill, int accountId) {
		List<String> groupNames = new ArrayList<>();
		for (AccountSkillGroup accountSkillGroup : accountSkill.getGroups()) {
			groupNames.add(accountSkillGroup.getGroup().getName());
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

	@Deprecated
	public AccountSkill update(AccountSkill updatedAccountSkill, String id, int accountId, int appUserId) {
		updatedAccountSkill.setId(NumberUtils.toInt(id));
		EntityAuditInfo updated = new EntityAuditInfo.Builder()
				.appUserId(appUserId)
				.timestamp(new Date())
				.build();

		AccountSkill accountSkillInDatabase = skillEntityService.update(updatedAccountSkill, updated);
		updateAccountSkillGroups(accountSkillInDatabase, updatedAccountSkill, appUserId);

		// If we're making this skill required then we can't associate this skill with groups
		if (accountSkillInDatabase.getRuleType().isRequired()) {
			accountSkillInDatabase.getGroups().clear();
		}

		EntityHelper.setUpdateAuditFields(accountSkillInDatabase, updated);

		accountSkillEmployeeService.linkEmployeesToSkill(accountSkillInDatabase, appUserId);

		return accountSkillDAO.save(accountSkillInDatabase);
	}

	private void updateAccountSkillGroups(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {
		List<AccountSkillGroup> accountSkillGroups = getLinkedGroups(accountSkillInDatabase, updatedSkill, appUserId);
		accountSkillInDatabase.getGroups().clear();
		accountSkillInDatabase.getGroups().addAll(accountSkillGroups);
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

		List<Project> affectedProjects = projectService.getProjectsForAccount(siteId);
		List<Employee> affectedEmployees = employeeService.getEmployeesByProjects(affectedProjects);

		for (Employee employee : affectedEmployees) {
			accountSkillEmployeeService.linkEmployeeToSkills(employee, appUserID, now);
		}
	}

	private List<AccountSkillGroup> getLinkedGroups(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(updatedSkill.getGroups(),
				accountSkillInDatabase.getGroups(), AccountSkillGroup.COMPARATOR, callback);

		List<String> groupNames = getGroupNames(accountSkillGroups);

		if (CollectionUtils.isNotEmpty(groupNames)) {
			List<Group> groups = accountGroupDAO.findGroupByAccountIdAndNames(updatedSkill.getAccountId(), groupNames);

			for (AccountSkillGroup accountSkillGroup : accountSkillGroups) {
				Group group = accountSkillGroup.getGroup();
				int index = groups.indexOf(group);
				if (index >= 0) {
					accountSkillGroup.setGroup(groups.get(index));
				}
			}
		}

		accountSkillGroups.addAll(callback.getRemovedEntities());

		return accountSkillGroups;
	}

	private List<String> getGroupNames(List<AccountSkillGroup> AccountSkillGroups) {
		if (CollectionUtils.isEmpty(AccountSkillGroups)) {
			return Collections.emptyList();
		}

		List<String> groupNames = new ArrayList<>();
		for (AccountSkillGroup AccountSkillGroup : AccountSkillGroups) {
			groupNames.add(AccountSkillGroup.getGroup().getName());
		}

		return groupNames;
	}

	@Deprecated
	public void delete(String id, int accountId, int appUserId) {
		skillEntityService.deleteById(NumberUtils.toInt(id));
	}

	@Deprecated
	public List<AccountSkill> search(String searchTerm, int accountId) {
		return skillEntityService.search(searchTerm, accountId);
	}

	public List<AccountSkill> search(String searchTerm, List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.search(searchTerm, accountIds);
	}

	public Map<AccountSkill, Set<Role>> getProjectRoleSkillsMap(final Employee employee) {
		List<AccountSkillRole> projectSkillRoles = accountSkillRoleDAO.findProjectRoleSkillsByEmployee(employee);
		return Utilities.convertToMapOfSets(projectSkillRoles, new Utilities.EntityKeyValueConvertable<AccountSkillRole, AccountSkill, Role>() {
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

	public Map<AccountSkill, Set<Integer>> getCorporateSkillsForProjects(final List<Project> projects) {
		return getSiteSkillsForProjects(getCorporateIds(getAccountIds(projects)));
	}


	public Map<AccountSkill, Set<Integer>> getSiteSkillsForProjects(final List<Project> projects) {
		return getSiteSkillsForProjects(getAccountIds(projects));
	}

	public Map<AccountSkill, Set<Project>> getProjectRequiredSkillsMap(final List<Project> projects) {
		List<ProjectSkill> projectSkills = projectSkillDAO.findByProjects(projects);

		return Utilities.convertToMapOfSets(projectSkills, new Utilities.EntityKeyValueConvertable<ProjectSkill, AccountSkill, Project>() {
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
		List<AccountSkillGroup> accountSkillGroups = accountSkillGroupDAO.findByGroups(groups);
		return Utilities.convertToMapOfSets(accountSkillGroups, new Utilities.EntityKeyValueConvertable<AccountSkillGroup, AccountSkill, Group>() {
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
		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountIds(accountIds);
		return getSkillMapFromSiteSkills(siteSkills);
	}

	private Set<Integer> getAccountIds(List<Project> projects) {
		return Utilities.getIdsFromCollection(projects, new Utilities.Identitifable<Project, Integer>() {

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
		return Utilities.convertToMapOfSets(siteSkills, new Utilities.EntityKeyValueConvertable<SiteSkill, AccountSkill, Integer>() {
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
		Map<Role, Role> siteToCorporateRoles = getSiteToCorporateRoles(employee);

		Map<AccountSkill, Set<Integer>> siteAssignmentSkills = new HashMap<>();
		for (Map.Entry<Role, Role> entry : siteToCorporateRoles.entrySet()) {
			Role siteRole = entry.getKey();
			Role corporateRole = entry.getValue();

			List<AccountSkill> corporateSkills = ExtractorUtil.extractList(corporateRole.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);

			for (AccountSkill accountSkill : corporateSkills) {
				Utilities.addToMapOfKeyToSet(siteAssignmentSkills, accountSkill, siteRole.getAccountId());
				Utilities.addToMapOfKeyToSet(siteAssignmentSkills, accountSkill, corporateRole.getAccountId());
			}
		}

		return siteAssignmentSkills;
	}

	private Map<Role, Role> getSiteToCorporateRoles(Employee employee) {
		Set<Integer> siteIds = new HashSet<>();
		for (RoleEmployee roleEmployee : employee.getRoles()) {
			siteIds.add(roleEmployee.getRole().getAccountId());
		}

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteIds);
		return roleDAO.findSiteToCorporateRoles(corporateIds, siteIds);
	}

	public List<AccountSkill> getParentSiteRequiredSkills(int accountId) {
		List<Integer> parentIds = accountService.getTopmostCorporateAccountIds(accountId);
		List<SiteSkill> requiredSkills = siteSkillDAO.findByAccountIds(parentIds);
		return ExtractorUtil.extractList(requiredSkills, SiteSkill.SKILL_EXTRACTOR);
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
				Utilities.extractAndFlattenValuesFromMap(projectRoleMap));

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

		return Utilities.convertToMapOfSets(projectSkillDAO.findByProjects(projects),
				new Utilities.EntityKeyValueConvertable<ProjectSkill, Project, AccountSkill>() {
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

		Map<Role, Set<AccountSkill>> roleSkillsMap = Utilities.convertToMapOfSets(accountSkillRoleDAO.findSkillsByRoles(corporateRoles),
				new Utilities.EntityKeyValueConvertable<AccountSkillRole, Role, AccountSkill>() {

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
