package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class GroupService {

	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private AccountGroupEmployeeService accountGroupEmployeeService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private EmployeeDAO employeeDAO;

	public AccountGroup getGroup(String id, int accountId) {
		return accountGroupDAO.findGroupByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<AccountGroup> getGroupsForAccount(int accountId) {
		return accountGroupDAO.findByAccount(accountId);
	}

	public List<AccountGroup> getGroupsForAccounts(final List<Integer> accountIds) {
		return accountGroupDAO.findByAccounts(accountIds);
	}

	public AccountGroup save(AccountGroup accountGroup, int accountId, int appUserId) {
		accountGroup.setAccountId(accountId);

		Date createdDate = new Date();
		EntityHelper.setCreateAuditFields(accountGroup, appUserId, createdDate);
		EntityHelper.setCreateAuditFields(accountGroup.getSkills(), appUserId, createdDate);
		EntityHelper.setCreateAuditFields(accountGroup.getEmployees(), appUserId, createdDate);

		accountGroup = accountGroupDAO.save(accountGroup);
		accountSkillEmployeeService.linkEmployeesToSkill(accountGroup, appUserId);
		return accountGroup;
	}

	public AccountGroup update(AccountGroup accountGroup, String id, int accountId, int appUserId) {
		AccountGroup accountGroupToUpdate = getGroup(id, accountId);
		updateGroup(accountGroupToUpdate, accountGroup, appUserId);

		accountGroup.setUpdatedBy(appUserId);
		accountGroup.setUpdatedDate(new Date());

		accountSkillEmployeeService.linkEmployeesToSkill(accountGroup, appUserId);

		return accountGroupDAO.save(accountGroupToUpdate);
	}

	public AccountGroup update(GroupNameSkillsForm groupNameSkillsForm, String id, int accountId, int appUserId) {
		AccountGroup accountGroupInDatabase = getGroup(id, accountId);
		accountGroupInDatabase.setName(groupNameSkillsForm.getName());
		accountGroupInDatabase = accountGroupDAO.save(accountGroupInDatabase);

		List<AccountSkillGroup> newAccountSkillGroups = new ArrayList<>();

		if (ArrayUtils.isNotEmpty(groupNameSkillsForm.getSkills())) {
			List<AccountSkill> skills = accountSkillDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupNameSkillsForm.getSkills())));
			for (AccountSkill accountSkill : skills) {
				newAccountSkillGroups.add(new AccountSkillGroup(accountGroupInDatabase, accountSkill));
			}
		}

		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(
				newAccountSkillGroups,
				accountGroupInDatabase.getSkills(),
				AccountSkillGroup.COMPARATOR,
				new BaseEntityCallback(appUserId, new Date()));

		accountGroupInDatabase.setSkills(accountSkillGroups);
		accountSkillEmployeeService.linkEmployeesToSkill(accountGroupInDatabase, appUserId);

		return accountGroupDAO.save(accountGroupInDatabase);
	}

	public AccountGroup update(GroupEmployeesForm groupEmployeesForm, String id, int accountId, int appUserId) {
		AccountGroup accountGroupInDatabase = getGroup(id, accountId);

		AccountGroup updatedAccountGroup = new AccountGroup(accountGroupInDatabase);

		if (groupEmployeesForm != null && ArrayUtils.isNotEmpty(groupEmployeesForm.getEmployees())) {
			List<Employee> employees = employeeDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupEmployeesForm.getEmployees())));
			for (Employee employee : employees) {
				updatedAccountGroup.getEmployees().add(new AccountGroupEmployee(employee, updatedAccountGroup));
			}
		}

		List<AccountGroupEmployee> accountGroupEmployees = IntersectionAndComplementProcess.intersection(
				updatedAccountGroup.getEmployees(),
				accountGroupInDatabase.getEmployees(),
				AccountGroupEmployee.COMPARATOR,
				new BaseEntityCallback(appUserId, new Date()));

		updatedAccountGroup.setEmployees(accountGroupEmployees);
		updatedAccountGroup.setSkills(accountGroupInDatabase.getSkills());

		accountSkillEmployeeService.linkEmployeesToSkill(updatedAccountGroup, appUserId);

		return accountGroupDAO.save(updatedAccountGroup);
	}

	private void updateGroup(AccountGroup accountGroupInDatabase, AccountGroup updatedAccountGroup, int appUserId) {
		accountGroupInDatabase.setName(updatedAccountGroup.getName());
		accountGroupInDatabase.setDescription(updatedAccountGroup.getDescription());

		GroupToSkillManager groupToSkillManager = new GroupToSkillManager();
		GroupToEmployeeManager groupToEmployeeManager = new GroupToEmployeeManager();

		groupToSkillManager.updateAccountSkillGroups(accountGroupInDatabase, updatedAccountGroup, appUserId);
		groupToEmployeeManager.updateAccountGroupEmployees(accountGroupInDatabase, updatedAccountGroup, appUserId);
	}

	public void delete(String id, int accountId, int appUserId) {
		AccountGroup accountGroup = getGroup(id, accountId);

		Date deletedDate = new Date();
		EntityHelper.softDelete(accountGroup, appUserId, deletedDate);
		EntityHelper.softDelete(accountGroup.getEmployees(), appUserId, deletedDate);
		EntityHelper.softDelete(accountGroup.getSkills(), appUserId, deletedDate);

		accountGroupDAO.save(accountGroup);
	}

	public List<AccountGroup> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return accountGroupDAO.search(searchTerm, accountId);
	}

	public List<AccountGroup> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountGroupDAO.search(searchTerm, accountIds);
	}

	public Map<Integer, List<AccountGroup>> getMapOfAccountToGroupListByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyMap();
		}

		List<AccountGroupEmployee> accountGroupEmployees = accountGroupEmployeeService.findByProfile(profile);
		Map<Integer, List<AccountGroup>> map = new HashMap<>();

		for (AccountGroupEmployee accountGroupEmployee : accountGroupEmployees) {
			int accountId = accountGroupEmployee.getEmployee().getAccountId();
			if (map.get(accountId) == null) {
				map.put(accountId, new ArrayList<AccountGroup>());
			}

			map.get(accountId).add(accountGroupEmployee.getGroup());
		}

		return map;
	}

	public AccountGroup getGroup(final String id) {
		return accountGroupDAO.find(NumberUtils.toInt(id));
	}
}
