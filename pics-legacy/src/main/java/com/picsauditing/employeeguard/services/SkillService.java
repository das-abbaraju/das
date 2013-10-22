package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SkillService {
	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;

	public AccountSkill getSkill(String id) {
		return accountSkillDAO.find(NumberUtils.toInt(id));
	}

	public AccountSkill getSkill(String id, int accountId) {
		return accountSkillDAO.findSkillByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<AccountSkill> getSkillsForAccount(int accountId) {
		return accountSkillDAO.findByAccount(accountId);
	}

	public List<AccountSkill> getOptionalSkillsForAccount(int accountId) {
		return accountSkillDAO.findOptionalSkillsByAccount(accountId);
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

		List<AccountGroup> persistedGroups = accountGroupDAO.findGroupByAccountIdAndNames(accountId, groupNames);
		for (AccountGroup persistedGroup : persistedGroups) {
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

		EntityHelper.setUpdateAuditFields(updatedAccountSkill, appUserId, new Date());

		accountSkillEmployeeService.linkEmployeesToSkill(updatedAccountSkill, appUserId);

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

	private List<AccountSkillGroup> getLinkedGroups(final AccountSkill accountSkillInDatabase, final AccountSkill updatedSkill, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback(appUserId, new Date());
		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(updatedSkill.getGroups(),
				accountSkillInDatabase.getGroups(), AccountSkillGroup.COMPARATOR, callback);

		List<String> groupNames = getGroupNames(accountSkillGroups);

		if (CollectionUtils.isNotEmpty(groupNames)) {
			List<AccountGroup> accountGroups = accountGroupDAO.findGroupByAccountIdAndNames(updatedSkill.getAccountId(), groupNames);

			for (AccountSkillGroup AccountSkillGroup : accountSkillGroups) {
				AccountGroup group = AccountSkillGroup.getGroup();
				int index = accountGroups.indexOf(group);
				if (index >= 0) {
					AccountSkillGroup.setGroup(accountGroups.get(index));
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

		Date deletedDate = new Date();
		EntityHelper.softDelete(accountSkill, appUserId, deletedDate);
		EntityHelper.softDelete(accountSkill.getGroups(), appUserId, deletedDate);
		EntityHelper.softDelete(accountSkill.getEmployees(), appUserId, deletedDate);

		accountSkillDAO.save(accountSkill);
	}

	public List<AccountSkill> search(String searchTerm, int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return accountSkillDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

}
