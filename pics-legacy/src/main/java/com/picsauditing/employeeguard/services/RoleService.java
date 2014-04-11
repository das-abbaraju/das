package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
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
	private RoleAssignmentHelper roleAssignmentHelper;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private SkillEngine skillEngine;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;
	@Autowired
	private SkillUsageLocator skillUsageLocator;

	public Role getRole(final String id, final int accountId) {
		return roleDAO.findRoleByAccount(NumberUtils.toInt(id), accountId);
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

		for (SiteAssignment siteAssignment : roleInDatabase.getSiteAssignments()) {
			accountSkillEmployeeService.linkEmployeeToSkills(siteAssignment.getEmployee(), appUserId, timestamp);
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
//		EntityHelper.setCreateAuditFields(role., appUserId, createdDate);

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

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId, final Collection<Integer> contractorIds) {
		List<SiteAssignment> siteAssignments = siteAssignmentDAO.findBySiteIdAndContractorIds(siteId, contractorIds);
		Map<Employee, Set<Role>> employeeSiteRoles = PicsCollectionUtil.convertToMapOfSets(siteAssignments, new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Employee, Role>() {
			@Override
			public Employee getKey(SiteAssignment siteAssignment) {
				return siteAssignment.getEmployee();
			}

			@Override
			public Role getValue(SiteAssignment siteAssignment) {
				return siteAssignment.getRole();
			}
		});

		Map<Role, Set<AccountSkill>> roleSkills = PicsCollectionUtil.convertToMapOfSets(accountSkillRoleDAO
				.findSkillsByRoles(PicsCollectionUtil.flattenCollectionOfCollection(employeeSiteRoles.values())),

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

	public Set<Role> getEmployeeRolesForSite(final int siteId, final Employee employee) {
		return new HashSet<>(roleDAO.findSiteRolesForEmployee(siteId, employee));
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

	public void assignEmployeeToRole(final int siteId, final int roleId, final Employee employee, final EntityAuditInfo auditInfo) {
		createSiteAssignment(siteId, roleId, employee, auditInfo);
		List<Integer> corporateIds = Collections.unmodifiableList(accountService.getTopmostCorporateAccountIds(siteId));
		updateEmployeeSkillsForRole(siteId, corporateIds, roleId, employee, auditInfo);
	}

	private void createSiteAssignment(final int siteId, final int roleId, final Employee employee, final EntityAuditInfo auditInfo) {

		SiteAssignment siteAssignment = new SiteAssignment();
		siteAssignment.setEmployee(employee);
		siteAssignment.setRole(roleDAO.find(roleId));
		siteAssignment.setSiteId(siteId);
		EntityHelper.setCreateAuditFields(siteAssignment, auditInfo);

		siteAssignmentDAO.save(siteAssignment);
	}

	private void updateEmployeeSkillsForRole(final int siteId, final List<Integer> corporateIds,
	                                         final int corporateRoleId, final Employee employee, final EntityAuditInfo auditInfo) {
		Set<AccountSkill> allSkillsForJobRole = new HashSet<>();
		allSkillsForJobRole.addAll(accountSkillDAO.findByCorporateRoleId(corporateRoleId));
		allSkillsForJobRole.addAll(accountSkillDAO.findRequiredByAccounts(corporateIds));
		allSkillsForJobRole.addAll(accountSkillDAO.findRequiredByAccount(siteId));

		List<AccountSkill> employeeSkills = accountSkillDAO.findByEmployee(employee);
		List<AccountSkillEmployee> accountSkillEmployees = buildAccountSkillEmployee(employee, allSkillsForJobRole,
				employeeSkills, auditInfo);
		accountSkillEmployeeService.save(accountSkillEmployees);
	}

	private List<AccountSkillEmployee> buildAccountSkillEmployee(final Employee employee,
	                                                             final Set<AccountSkill> allSkillsForJobRole,
	                                                             final List<AccountSkill> employeeSkills,
	                                                             final EntityAuditInfo auditInfo) {
		if (CollectionUtils.isEmpty(allSkillsForJobRole)) {
			return Collections.emptyList();
		}

		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
		for (AccountSkill accountSkill : allSkillsForJobRole) {
			if (!employeeSkills.contains(accountSkill)) {
				accountSkillEmployees.add(new AccountSkillEmployeeBuilder()
						.accountSkill(accountSkill)
						.employee(employee)
						.createdBy(auditInfo.getAppUserId())
						.createdDate(auditInfo.getTimestamp())
						.startDate(auditInfo.getTimestamp())
						.build());
			}
		}

		return accountSkillEmployees;
	}

	public void unassignEmployeeFromRole(final Employee employee, final int roleId, final int siteId) {
		Role corporateRole = roleDAO.find(roleId);

		roleAssignmentHelper.deleteProjectRolesFromEmployee(employee, corporateRole);
		roleAssignmentHelper.deleteSiteRoleFromEmployee(employee, corporateRole, siteId);

		List<AccountSkillEmployee> employeeSkillsToRemove = new ArrayList<>(employee.getSkills());
		SkillUsage skillUsage = skillUsageLocator.getSkillUsagesForEmployee(employee);

		List<AccountSkillEmployee> employeeSkillsToKeep = accountSkillEmployeeDAO.findByEmployeeAndSkills(employee, new ArrayList<>(skillUsage.allSkills()));
		employeeSkillsToRemove.removeAll(employeeSkillsToKeep);

		accountSkillEmployeeDAO.deleteByIds(PicsCollectionUtil.getIdsFromCollection(employeeSkillsToRemove,
				new PicsCollectionUtil.Identitifable<AccountSkillEmployee, Integer>() {

					@Override
					public Integer getId(AccountSkillEmployee accountSkillEmployee) {
						return accountSkillEmployee.getId();
					}
				}));
	}

	public void unassignEmployeeFromSite(final Employee employee, final int siteId) {
		roleAssignmentHelper.deleteProjectRolesFromEmployee(employee.getId(), siteId);
		roleAssignmentHelper.deleteSiteRolesFromEmployee(employee.getId(), siteId);

		skillEngine.updateSiteSkillsForEmployee(employee, siteId);
	}
}
