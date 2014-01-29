package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
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

public class RoleService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	public Role getRole(final String id, final int accountId) {
		return roleDAO.findRoleByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<RoleEmployee> getSiteAssignmentsForRole(final Role role) {
		return Collections.emptyList();
	}

	public List<Role> getRolesForAccount(final int accountId) {
		return getRolesForAccounts(Arrays.asList(accountId));
	}

	public List<Role> getRolesForAccounts(final List<Integer> accountIds) {
		return roleDAO.findByAccounts(accountIds);
	}

	public Role update(GroupNameSkillsForm groupNameSkillsForm, String id, int accountId, int appUserId) {
		Role roleInDatabase = getRole(id, accountId);
		roleInDatabase.setName(groupNameSkillsForm.getName());
		roleInDatabase = roleDAO.save(roleInDatabase);

		List<AccountSkillRole> newAccountSkillRoles = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(groupNameSkillsForm.getSkills())) {
			List<AccountSkill> skills = accountSkillDAO.findByIds(Arrays.asList(ArrayUtils.toObject(groupNameSkillsForm.getSkills())));
			for (AccountSkill accountSkill : skills) {
				newAccountSkillRoles.add(new AccountSkillRole(roleInDatabase, accountSkill));
			}
		}

		Date timestamp = new Date();
		List<AccountSkillRole> accountSkillGroups = IntersectionAndComplementProcess.intersection(
				newAccountSkillRoles,
				roleInDatabase.getSkills(),
				AccountSkillRole.COMPARATOR,
				new BaseEntityCallback(appUserId, timestamp));

		roleInDatabase.setSkills(accountSkillGroups);
		roleInDatabase = roleDAO.save(roleInDatabase);

		for (RoleEmployee roleEmployee : roleInDatabase.getEmployees()) {
			accountSkillEmployeeService.linkEmployeeToSkills(roleEmployee.getEmployee(), appUserId, timestamp);
		}

		return roleInDatabase;
	}

	public List<Role> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return roleDAO.search(searchTerm, accountId);
	}

	public List<Role> search(final String searchTerm, final List<Integer> accountIds) {
		if (Strings.isEmpty(searchTerm) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return roleDAO.search(searchTerm, accountIds);
	}

	public Role save(Role role, final int accountId, final int appUserId) {
		role.setAccountId(accountId);

		Date createdDate = new Date();
		EntityHelper.setCreateAuditFields(role, appUserId, createdDate);
		EntityHelper.setCreateAuditFields(role.getSkills(), appUserId, createdDate);
		EntityHelper.setCreateAuditFields(role.getEmployees(), appUserId, createdDate);

		role = roleDAO.save(role);
		accountSkillEmployeeService.linkEmployeesToSkill(role, appUserId);
		return role;
	}

	public Role update(GroupEmployeesForm roleEmployeesForm, String id, int accountId, int userId) {
		return null;
	}

	public Role getRole(final String id) {
		return roleDAO.find(NumberUtils.toInt(id));
	}

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId, final int contractorId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		// find roles by this site that duplicate roles with the above corporate ids
		Map<Role, Role> siteRoleToCorporateRole = roleDAO.findDuplicatedRoles(corporateIds, siteId);

		List<Employee> employees = employeeDAO.findByAccount(contractorId);

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (Employee employee : employees) {
			addSkillsFromSiteRoles(siteId, employeeSkills, employee, siteRoleToCorporateRole);
			addSkillsFromSiteProjectRoles(siteId, employeeSkills, employee);
		}

		addSiteRequiredSkills(siteId, corporateIds, employeeSkills);

		return employeeSkills;
	}

	private void addSkillsFromSiteRoles(int siteId, Map<Employee, Set<AccountSkill>> employeeSkills, Employee employee, Map<Role, Role> siteRoleToCorporateRole) {
		for (RoleEmployee roleEmployee : employee.getRoles()) {
			if (roleEmployee.getRole().getAccountId() == siteId) {
				Role corporateRole = siteRoleToCorporateRole.get(roleEmployee.getRole());
				addRoleSkills(employeeSkills, employee, corporateRole.getSkills());
			}
		}
	}

	private void addSkillsFromSiteProjectRoles(int siteId, Map<Employee, Set<AccountSkill>> employeeSkills, Employee employee) {
		for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
			if (projectRoleEmployee.getProjectRole().getProject().getAccountId() == siteId) {
				addRoleSkills(employeeSkills, employee, projectRoleEmployee.getProjectRole().getRole().getSkills());
			}
		}
	}

	private void addRoleSkills(Map<Employee, Set<AccountSkill>> employeeSkills, Employee employee, List<AccountSkillRole> roleSkills) {
		for (AccountSkillRole accountSkillRole : roleSkills) {
			Utilities.addToMapOfKeyToSet(employeeSkills, employee, accountSkillRole.getSkill());
		}
	}

	private void addSiteRequiredSkills(int siteId, List<Integer> corporateIds, Map<Employee, Set<AccountSkill>> employeeSkills) {
		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountId(siteId);
		List<SiteSkill> corporateSkills = siteSkillDAO.findByAccountIds(corporateIds);

		for (Map.Entry<Employee, Set<AccountSkill>> employeeSkillSet : employeeSkills.entrySet()) {
			for (SiteSkill siteSkill : siteSkills) {
				Utilities.addToMapOfKeyToSet(employeeSkills, employeeSkillSet.getKey(), siteSkill.getSkill());
			}

			for (SiteSkill corporateSkill : corporateSkills) {
				Utilities.addToMapOfKeyToSet(employeeSkills, employeeSkillSet.getKey(), corporateSkill.getSkill());
			}
		}
	}

    public Map<Employee, Set<Role>> getEmployeeRolesForSite(final int contractorId, final int siteId) {
        return Collections.emptyMap();
    }
}
