package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.forms.contractor.RoleNameSkillsForm;
import com.picsauditing.employeeguard.services.engine.SkillEngine;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Deprecated
public class RoleService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private AccountSkillRoleDAO accountSkillRoleDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private SkillEngine skillEngine;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;

	public Role getRole(final String id, final int accountId) {
		return roleDAO.findRoleByAccount(NumberUtils.toInt(id), accountId);
	}

	public List<Role> getRolesForAccount(final int accountId) {
		return getRolesForAccounts(Arrays.asList(accountId));
	}

	public List<Role> getRolesForAccounts(final List<Integer> accountIds) {
		return roleDAO.findByAccounts(accountIds);
	}

	public Role update(RoleNameSkillsForm roleNameSkillsForm, String id, int accountId, int appUserId) {
		Role roleInDatabase = getRole(id, accountId);
		roleInDatabase.setName(roleNameSkillsForm.getName());
		roleInDatabase = roleDAO.save(roleInDatabase);

		List<AccountSkillRole> newAccountSkillRoles = new ArrayList<>();
		if (ArrayUtils.isNotEmpty(roleNameSkillsForm.getSkills())) {
			List<AccountSkill> skills = accountSkillDAO.findByIds(Arrays.asList(ArrayUtils.toObject(roleNameSkillsForm.getSkills())));
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

    roleInDatabase.getSkills().clear();
    roleInDatabase.getSkills().addAll(accountSkillGroups);

		roleInDatabase = roleDAO.save(roleInDatabase);

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

		return roleDAO.save(role);
	}

	public Role update(GroupEmployeesForm roleEmployeesForm, String id, int accountId, int userId) {
		return null;
	}

	public Role getRole(final String id) {
		return roleDAO.find(NumberUtils.toInt(id));
	}

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId, final Collection<Integer> contractorIds) {
		if (CollectionUtils.isEmpty(contractorIds)) {
			return Collections.emptyMap();
		}

		List<SiteAssignment> siteAssignments = siteAssignmentDAO.findBySiteIdAndContractorIds(siteId, contractorIds);

		Map<Employee, Set<Role>> employeeSiteRoles = PicsCollectionUtil.convertToMapOfSets(siteAssignments,
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Employee, Role>() {
					@Override
					public Employee getKey(SiteAssignment siteAssignment) {
						return siteAssignment.getEmployee();
					}

					@Override
					public Role getValue(SiteAssignment siteAssignment) {
						return siteAssignment.getRole();
					}
				});

		Set<Role> siteRoles = PicsCollectionUtil.flattenCollectionOfCollection(employeeSiteRoles.values());

		Map<Role, Set<AccountSkill>> roleSkills = getSiteRoleSkills(siteRoles);

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (Employee employee : employeeSiteRoles.keySet()) {
			if (!employeeSkills.containsKey(employee)) {
				employeeSkills.put(employee, new HashSet<AccountSkill>());
			}

			for (Role role : employeeSiteRoles.get(employee)) {
				if (roleSkills.containsKey(role)) {
					employeeSkills.get(employee).addAll(roleSkills.get(role));
				}
			}
		}

		return employeeSkills;
	}

	private Map<Role, Set<AccountSkill>> getSiteRoleSkills(final Set<Role> siteRoles) {
		if (CollectionUtils.isEmpty(siteRoles)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				accountSkillRoleDAO.findSkillsByRoles(siteRoles),

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
	}

	public Map<Role, Set<Employee>> getRoleAssignments(final int contractorId, final int siteId) {
		return PicsCollectionUtil.convertToMapOfSets(siteAssignmentDAO.findBySiteIdAndContractorId(siteId, contractorId),
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Role, Employee>() {

					@Override
					public Role getKey(SiteAssignment siteAssignment) {
						return siteAssignment.getRole();
					}

					@Override
					public Employee getValue(SiteAssignment siteAssignment) {
						return siteAssignment.getEmployee();
					}
				});
	}
}
