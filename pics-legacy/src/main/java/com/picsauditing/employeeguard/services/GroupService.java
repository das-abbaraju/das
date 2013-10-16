package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
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

		AccountGroup updatedAccountGroup = new AccountGroup(accountGroupInDatabase);
		updatedAccountGroup.setName(groupNameSkillsForm.getName());

		if (ArrayUtils.isNotEmpty(groupNameSkillsForm.getSkills())) {
			List<AccountSkill> skills = accountSkillDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupNameSkillsForm.getSkills())));
			for (AccountSkill accountSkill : skills) {
				updatedAccountGroup.getSkills().add(new AccountSkillGroup(updatedAccountGroup, accountSkill));
			}
		}

		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(
				updatedAccountGroup.getSkills(),
				accountGroupInDatabase.getSkills(),
                AccountSkillGroup.COMPARATOR,
				new BaseEntityCallback(appUserId, new Date()));

		updatedAccountGroup.setSkills(accountSkillGroups);
		updatedAccountGroup.setEmployees(accountGroupInDatabase.getEmployees());

		accountSkillEmployeeService.linkEmployeesToSkill(updatedAccountGroup, appUserId);

		return accountGroupDAO.save(updatedAccountGroup);
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

	public List<AccountGroup> search(String searchTerm, int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return accountGroupDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

	public Map<Integer, List<AccountGroup>> getMapOfAccountToGroupListByProfile(final Profile profile) {
		if (profile != null) {
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

		return Collections.emptyMap();
	}
}
