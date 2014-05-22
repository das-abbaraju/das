package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.RoleProjectsForm;
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
	private AccountSkillProfileService accountSkillProfileService;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private RoleDAO roleDAO;

	public Group getGroup(String id, int accountId) {
		return accountGroupDAO.findGroupByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<Group> getGroupsForAccount(int accountId) {
		return accountGroupDAO.findByAccount(accountId);
	}

	public List<Group> getGroupsForAccounts(final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountGroupDAO.findByAccounts(accountIds);
	}

	public List<Group> getEmployeeGroups(final Employee employee) {
		return accountGroupDAO.findGroupsForEmployee(employee);
	}

	public List<Group> getGroupAssignmentsForEmployee(final Employee employee) {
		return accountGroupDAO.findEmployeeGroupAssignments(employee);
	}

	public Group save(Group group, int accountId, int appUserId) {
		group.setAccountId(accountId);

		Date createdDate = new Date();
		EntityHelper.setCreateAuditFields(group, appUserId, createdDate);
		EntityHelper.setCreateAuditFields(group.getSkills(), appUserId, createdDate);
		EntityHelper.setCreateAuditFields(group.getEmployees(), appUserId, createdDate);

		return accountGroupDAO.save(group);
	}

	public Group update(Group group, String id, int accountId, int appUserId) {
		Group groupToUpdate = getGroup(id, accountId);
		updateGroup(groupToUpdate, group, appUserId);

		group.setUpdatedBy(appUserId);
		group.setUpdatedDate(new Date());

		return accountGroupDAO.save(groupToUpdate);
	}

	public Group update(GroupNameSkillsForm groupNameSkillsForm, String id, int accountId, int appUserId) {
		Group groupInDatabase = getGroup(id, accountId);
		groupInDatabase.setName(groupNameSkillsForm.getName());
		groupInDatabase = accountGroupDAO.save(groupInDatabase);

		List<AccountSkillGroup> newAccountSkillGroups = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(groupNameSkillsForm.getSkills())) {
			List<AccountSkill> skills = accountSkillDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupNameSkillsForm.getSkills())));
			for (AccountSkill accountSkill : skills) {
				newAccountSkillGroups.add(new AccountSkillGroup(groupInDatabase, accountSkill));
			}
		}

		Date timestamp = new Date();
		List<AccountSkillGroup> accountSkillGroups = IntersectionAndComplementProcess.intersection(
				newAccountSkillGroups,
				groupInDatabase.getSkills(),
				AccountSkillGroup.COMPARATOR,
				new BaseEntityCallback(appUserId, timestamp));

		groupInDatabase.getSkills().clear();
		groupInDatabase.getSkills().addAll(accountSkillGroups);
		return accountGroupDAO.save(groupInDatabase);
	}

	public Group update(GroupEmployeesForm groupEmployeesForm, String id, int accountId, int appUserId) {
		Group groupInDatabase = getGroup(id, accountId);

		Group updatedGroup = new Group(groupInDatabase);

		if (groupEmployeesForm != null && ArrayUtils.isNotEmpty(groupEmployeesForm.getEmployees())) {
			List<Employee> employees = employeeDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupEmployeesForm.getEmployees())));
			for (Employee employee : employees) {
				updatedGroup.getEmployees().add(new GroupEmployee(employee, updatedGroup));
			}
		}

		List<GroupEmployee> groupEmployees = IntersectionAndComplementProcess.intersection(
				updatedGroup.getEmployees(),
				groupInDatabase.getEmployees(),
				GroupEmployee.COMPARATOR,
				new BaseEntityCallback(appUserId, new Date()));

    updatedGroup.getEmployees().clear();
    updatedGroup.getEmployees().addAll(groupEmployees);
    updatedGroup.getSkills().clear();
    updatedGroup.getSkills().addAll(groupInDatabase.getSkills());

		return accountGroupDAO.save(updatedGroup);
	}


	private void updateGroup(Group groupInDatabase, Group updatedGroup, int appUserId) {
		groupInDatabase.setName(updatedGroup.getName());
		groupInDatabase.setDescription(updatedGroup.getDescription());

		GroupToSkillManager groupToSkillManager = new GroupToSkillManager();
		GroupToEmployeeManager groupToEmployeeManager = new GroupToEmployeeManager();

		groupToSkillManager.updateAccountSkillGroups(groupInDatabase, updatedGroup, appUserId);
		groupToEmployeeManager.updateAccountGroupEmployees(groupInDatabase, updatedGroup, appUserId);
	}

	public void delete(String id, int accountId, int appUserId) {
		Group group = getGroup(id, accountId);
		EntityHelper.softDelete(group, appUserId);
		accountGroupDAO.delete(group);
	}

	public List<Group> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return accountGroupDAO.search(searchTerm, accountId);
	}

	public List<Group> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return accountGroupDAO.search(searchTerm, accountIds);
	}

	public Map<Integer, List<Group>> getMapOfAccountToGroupListByProfile(final Profile profile) {
		if (profile == null) {
			return Collections.emptyMap();
		}

		List<GroupEmployee> groupEmployees = accountGroupEmployeeService.findByProfile(profile);
		Map<Integer, List<Group>> map = new HashMap<>();

		for (GroupEmployee groupEmployee : groupEmployees) {
			int accountId = groupEmployee.getEmployee().getAccountId();
			if (map.get(accountId) == null) {
				map.put(accountId, new ArrayList<Group>());
			}

			map.get(accountId).add(groupEmployee.getGroup());
		}

		return map;
	}

	public Group getGroup(final String id) {
		return accountGroupDAO.find(NumberUtils.toInt(id));
	}
}
