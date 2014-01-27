package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SkillService {
	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillGroupDAO accountSkillGroupDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeSkillAssigner employeeSkillAssigner;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectSkillDAO projectSkillDAO;
	@Autowired
	private ProjectSkillRoleDAO projectSkillRoleDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	public AccountSkill getSkill(String id) {
		return accountSkillDAO.find(NumberUtils.toInt(id));
	}

	public AccountSkill getSkill(String id, int accountId) {
		return accountSkillDAO.findSkillByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<AccountSkill> getSkills(List<Integer> skillIds) {
		return accountSkillDAO.findByIds(skillIds);
	}

	public List<AccountSkill> getSkillsForAccount(int accountId) {
		return accountSkillDAO.findByAccount(accountId);
	}

	public List<AccountSkill> getRequiredSkills(int accountId) {
		List<SiteSkill> requiredByAccount = siteSkillDAO.findByAccountId(accountId);
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

	public AccountSkill save(AccountSkill accountSkill, int accountId, int appUserId) {
		accountSkill.setAccountId(accountId);

		setPersistedEntitiesOnJoinTables(accountSkill, accountId);

		Date createdDate = new Date();
		EntityHelper.setCreateAuditFields(accountSkill, appUserId, createdDate);
		EntityHelper.setCreateAuditFields(accountSkill.getGroups(), appUserId, createdDate);

		accountSkill = accountSkillDAO.save(accountSkill);
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

	public AccountSkill update(AccountSkill updatedAccountSkill, String id, int accountId, int appUserId) {
		AccountSkill accountSkillInDatabase = getSkill(id, accountId);

		updateAccountSkillInDatabase(accountSkillInDatabase, updatedAccountSkill, appUserId);

		if (accountSkillInDatabase.getRuleType().isRequired()) {
			// If we're making this skill required then we can't associate this skill with groups
			accountSkillInDatabase.getGroups().clear();
		}

		EntityHelper.setUpdateAuditFields(accountSkillInDatabase, appUserId, new Date());

		accountSkillEmployeeService.linkEmployeesToSkill(accountSkillInDatabase, appUserId);

		return accountSkillDAO.save(accountSkillInDatabase);
	}

	private void updateAccountSkillInDatabase(AccountSkill accountSkillInDatabase, AccountSkill updatedAccountSkill, int appUserId) {
		accountSkillInDatabase.setSkillType(updatedAccountSkill.getSkillType());
		accountSkillInDatabase.setRuleType(updatedAccountSkill.getRuleType());
		accountSkillInDatabase.setName(updatedAccountSkill.getName());
		accountSkillInDatabase.setIntervalType(updatedAccountSkill.getIntervalType());
		accountSkillInDatabase.setIntervalPeriod(updatedAccountSkill.getIntervalPeriod());
		accountSkillInDatabase.setDescription(updatedAccountSkill.getDescription());

		updateAccountSkillGroups(accountSkillInDatabase, updatedAccountSkill, appUserId);
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

	public void delete(String id, int accountId, int appUserId) {
		AccountSkill accountSkill = getSkill(id, accountId);

		EntityHelper.softDelete(accountSkill, appUserId);
		EntityHelper.softDelete(accountSkill.getGroups(), appUserId);
		EntityHelper.softDelete(accountSkill.getEmployees(), appUserId);

		accountSkillDAO.delete(accountSkill);
	}

	public List<AccountSkill> search(String searchTerm, int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return accountSkillDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

	public List<AccountSkill> search(String searchTerm, List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return accountSkillDAO.search(searchTerm, accountIds);
	}

	public Set<AccountSkill> findAllSkillsRequiredForEmployee(final Employee employee) {
		Set<AccountSkill> allRequiredSkills = new TreeSet<>();
		allRequiredSkills.addAll(findSkillsByEmployeeGroups(employee.getGroups()));

		findAndAddSkillsByEmployeeProjects(employee, allRequiredSkills);
		allRequiredSkills.addAll(findByEmployeeAccount(employee));

		List<SiteSkill> associatedAccountSkills = getSiteSkills(employee);
		allRequiredSkills.addAll(ExtractorUtil.extractList(associatedAccountSkills, SiteSkill.SKILL_EXTRACTOR));

		return allRequiredSkills;
	}

	private List<SiteSkill> getSiteSkills(Employee employee) {
		List<Project> projects = projectService.getProjectsForEmployee(employee);
		List<Integer> siteIds = ExtractorUtil.extractList(projects, Project.ACCOUNT_ID_EXTRACTOR);

		Set<Integer> associatedAccounts = new HashSet<>();
		associatedAccounts.addAll(siteIds);

		for (int siteId : siteIds) {
			associatedAccounts.addAll(accountService.getTopmostCorporateAccountIds(siteId));
		}

		return siteSkillDAO.findByAccountIds(new ArrayList<>(associatedAccounts));
	}

	private List<AccountSkill> findSkillsByEmployeeGroups(List<GroupEmployee> groupEmployees) {
		return accountSkillDAO.findByGroups(ExtractorUtil.extractList(groupEmployees, GroupEmployee.GROUP_EXTRACTOR));
	}

	private void findAndAddSkillsByEmployeeProjects(Employee employee, Set<AccountSkill> allRequiredSkills) {
		List<Project> projects = projectService.getProjectsForEmployee(employee);
		for (Project project : projects) {
			allRequiredSkills.addAll(projectService.getRequiredSkills(project));
		}

		for (ProjectRoleEmployee projectRoleEmployee : employee.getRoles()) {
			for (AccountSkillGroup accountSkillGroup : projectRoleEmployee.getProjectRole().getRole().getSkills()) {
				allRequiredSkills.add(accountSkillGroup.getSkill());
			}
		}
	}

	private List<AccountSkill> findByEmployeeAccount(Employee employee) {
		return accountSkillDAO.findRequiredByAccount(employee.getAccountId());
	}

	public Map<AccountSkill, Set<Group>> getProjectRoleSkillsMap(final Employee employee) {
		List<ProjectSkillRole> projectSkillRoles = projectSkillRoleDAO.findByEmployee(employee);
		return Utilities.convertToMapOfSets(projectSkillRoles, new Utilities.EntityKeyValueConvertable<ProjectSkillRole, AccountSkill, Group>() {

			@Override
			public AccountSkill getKey(ProjectSkillRole entity) {
				return entity.getProjectSkill().getSkill();
			}

			@Override
			public Group getValue(ProjectSkillRole entity) {
				return entity.getProjectRole().getRole();
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
		// List<Group> directlyAssignedJobRoles = accountGroupDAO.findJobRolesForEmployee(employee);

		return Collections.emptyMap();
	}
}
