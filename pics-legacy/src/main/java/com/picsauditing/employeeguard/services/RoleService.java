package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.engine.SkillEngine;
import com.picsauditing.employeeguard.util.ExtractorUtil;
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
	private RoleEmployeeDAO roleEmployeeDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;
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

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId, final Collection<Integer> contractorIds) {
		List<SiteAssignment> siteAssignments = siteAssignmentDAO.findBySiteIdAndContractorIds(siteId, contractorIds);
		Map<Employee, Set<Role>> employeeSiteRoles = Utilities.convertToMapOfSets(siteAssignments, new Utilities.EntityKeyValueConvertable<SiteAssignment, Employee, Role>() {
			@Override
			public Employee getKey(SiteAssignment siteAssignment) {
				return siteAssignment.getEmployee();
			}

			@Override
			public Role getValue(SiteAssignment siteAssignment) {
				return siteAssignment.getRole();
			}
		});

		Map<Role, Set<AccountSkill>> roleSkills = Utilities.convertToMapOfSets(accountSkillRoleDAO
				.findSkillsByRoles(Utilities.flattenCollectionOfCollection(employeeSiteRoles.values())),

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

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (Employee employee : employeeSiteRoles.keySet()) {
			if (!employeeSkills.containsKey(employee)) {
				employeeSkills.put(employee, new HashSet<AccountSkill>());
			}

			for (Role role : employeeSiteRoles.get(employee)) {
				employeeSkills.get(employee).addAll(roleSkills.get(role));
			}
		}

		return employeeSkills;
	}

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSiteRole(final int corporateRoleId,
																		 final int siteId,
																		 final Collection<Employee> employees) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		List<SiteSkill> siteRequiredSiteSkills = siteSkillDAO.findByAccountId(siteId);
		List<SiteSkill> corporateRequiredSiteSkills = siteSkillDAO.findByAccountIds(corporateIds);

		List<AccountSkill> siteSkills = ExtractorUtil.extractList(siteRequiredSiteSkills, SiteSkill.SKILL_EXTRACTOR);
		List<AccountSkill> corporateSkills = ExtractorUtil.extractList(corporateRequiredSiteSkills, SiteSkill.SKILL_EXTRACTOR);

		Role corporateRole = getRole(String.valueOf(corporateRoleId));

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (Employee employee : employees) {
			employeeSkills.put(employee, new HashSet<AccountSkill>());
			employeeSkills.get(employee).addAll(getSkillsFromCorporateRole(corporateRole));
			employeeSkills.get(employee).addAll(siteSkills);
			employeeSkills.get(employee).addAll(corporateSkills);
		}

		return employeeSkills;
	}

	private Set<AccountSkill> getSkillsFromSiteRoles(int siteId, Employee employee, Map<Role, Role> siteRoleToCorporateRole) {
		Set<AccountSkill> employeeRequiredSkills = new HashSet<>();
		for (RoleEmployee roleEmployee : employee.getRoles()) {
			if (roleEmployee.getRole().getAccountId() == siteId) {
				Role corporateRole = siteRoleToCorporateRole.get(roleEmployee.getRole());

				if (corporateRole != null) {
					employeeRequiredSkills.addAll(getSkillsFromCorporateRole(corporateRole));
				}
			}
		}
		return employeeRequiredSkills;
	}

	private List<AccountSkill> getSkillsFromCorporateRole(Role corporateRole) {
		if (corporateRole == null) {
			return Collections.emptyList();
		}

		return ExtractorUtil.extractList(corporateRole.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);
	}

	private Set<AccountSkill> getSkillsFromSiteProjectRoles(int siteId, Employee employee) {
		Set<AccountSkill> employeeRequiredSkills = new HashSet<>();
		for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
			if (projectRoleEmployee.getProjectRole().getProject().getAccountId() == siteId) {
				Role corporateRole = projectRoleEmployee.getProjectRole().getRole();
				employeeRequiredSkills.addAll(getSkillsFromCorporateRole(corporateRole));
			}
		}
		return employeeRequiredSkills;
	}

	public Set<Role> getEmployeeRolesForSite(final int siteId, final Employee employee) {
		return new HashSet<>(roleDAO.findSiteRolesForEmployee(siteId, employee));
	}

	public Map<Role, Set<Employee>> getRoleAssignments(final int contractorId, final int siteId) {
		return Utilities.convertToMapOfSets(siteAssignmentDAO.findBySiteIdAndContractorId(siteId, contractorId),
				new Utilities.EntityKeyValueConvertable<SiteAssignment, Role, Employee>() {
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

	public void assignEmployeeToRole(final int siteId, final int corporateRoleId, final Employee employee, final EntityAuditInfo auditInfo) {
		createSiteAssignment(siteId, corporateRoleId, employee, auditInfo);
		List<Integer> corporateIds = Collections.unmodifiableList(accountService.getTopmostCorporateAccountIds(siteId));
		updateEmployeeSkillsForRole(siteId, corporateIds, corporateRoleId, employee, auditInfo);
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

	private RoleEmployee buildRoleEmployee(final Role siteRole, final Employee employee, final int userId) {
		RoleEmployee roleEmployee = roleEmployeeDAO.findByEmployeeAndRole(employee, siteRole);

		if (roleEmployee != null) {
			return roleEmployee;
		}

		return new RoleEmployeeBuilder()
				.role(siteRole)
				.employee(employee)
				.createdBy(userId)
				.createdDate(DateBean.today())
				.build();
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

		accountSkillEmployeeDAO.deleteByIds(Utilities.getIdsFromCollection(employeeSkillsToRemove,
				new Utilities.Identitifable<AccountSkillEmployee, Integer>() {
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

	private void removeRequiredCorporateSkillsFromEmployee(Employee employee, List<Integer> corporateIds) {
		List<AccountSkillEmployee> accountSkillEmployees =
				accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(employee.getId(), corporateIds);
		accountSkillEmployeeDAO.delete(accountSkillEmployees);
	}

	private Set<Integer> getOtherEmployeeAssignedSiteIds(List<Integer> childSiteIds, int siteId, Employee employee) {
		if (CollectionUtils.isEmpty(childSiteIds)) {
			return Collections.emptySet();
		}

		childSiteIds.remove(new Integer(siteId));
		Set<Integer> otherSitesEmployeeIsAssignedTo = new HashSet<>();
		for (int childSiteId : childSiteIds) {
			if (isEmployeeAssignedToSite(employee, childSiteId)) {
				otherSitesEmployeeIsAssignedTo.add(childSiteId);
			}
		}

		return otherSitesEmployeeIsAssignedTo;
	}

	private boolean isEmployeeAssignedToSite(Employee employee, int childSiteId) {
		if (CollectionUtils.isEmpty(employee.getRoles())) {
			return false;
		}

		for (RoleEmployee roleEmployee : employee.getRoles()) {
			if (roleEmployee.getRole().getAccountId() == childSiteId) {
				return true;
			}
		}

		return false;
	}
}
