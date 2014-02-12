package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
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
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProjectCompanyDAO projectCompanyDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private RoleAssignmentHelper roleAssignmentHelper;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private RoleEmployeeDAO roleEmployeeDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;
	@Autowired
	private SkillAssignmentHelper skillAssignmentHelper;
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
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		// find roles by this site that duplicate roles with the above corporate ids
		Map<Role, Role> siteRoleToCorporateRole = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);

		List<Employee> employees = employeeDAO.findByAccounts(contractorIds);
		List<AccountSkill> siteSkills = ExtractorUtil.extractList(siteSkillDAO.findByAccountId(siteId), SiteSkill.SKILL_EXTRACTOR);
		List<AccountSkill> corporateSkills = ExtractorUtil.extractList(siteSkillDAO.findByAccountIds(corporateIds), SiteSkill.SKILL_EXTRACTOR);

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (Employee employee : employees) {
			employeeSkills.put(employee, new HashSet<AccountSkill>());
			employeeSkills.get(employee).addAll(getSkillsFromSiteRoles(siteId, employee, siteRoleToCorporateRole));
			employeeSkills.get(employee).addAll(getSkillsFromSiteProjectRoles(siteId, employee));
			employeeSkills.get(employee).addAll(siteSkills);
			employeeSkills.get(employee).addAll(corporateSkills);
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
				employeeRequiredSkills.addAll(getSkillsFromCorporateRole(corporateRole));
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

	public Map<Role, Set<Employee>> getRoleAssignments(final int contractorId, final int siteId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);

		List<RoleEmployee> roleEmployees = roleEmployeeDAO.findByContractorAndSiteId(contractorId, siteId);
		List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByCorporateAndContractor(corporateIds, contractorId);

		Map<Role, Set<Employee>> roleAssignments = new HashMap<>();
		for (RoleEmployee roleEmployee : roleEmployees) {
			Role corporateRole = siteToCorporateRoles.get(roleEmployee.getRole());

			Utilities.addToMapOfKeyToSet(roleAssignments, corporateRole, roleEmployee.getEmployee());
		}

		for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
			Role role = projectRoleEmployee.getProjectRole().getRole();
			Utilities.addToMapOfKeyToSet(roleAssignments, role, projectRoleEmployee.getEmployee());
		}

		return roleAssignments;
	}

	public Map<Role, Role> getSiteToCorporateRoles(int siteId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);

		return roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
	}

	public Map<Role, Role> getCorporateToSiteRoles(int siteId) {
		return Utilities.invertMap(getSiteToCorporateRoles(siteId));
	}

	public void assignEmployeeToRole(final int siteId, final int corporateRoleId, final Employee employee,
	                                 final int userId) {
		List<Integer> corporateIds = Collections.unmodifiableList(accountService.getTopmostCorporateAccountIds(siteId));
		assignEmployeeToRole(siteId, corporateIds, corporateRoleId, employee, userId);
		updateEmployeeSkillsForRole(siteId, corporateIds, corporateRoleId, employee, userId);
	}

	private void assignEmployeeToRole(final int siteId, final List<Integer> corporateIds, final int roleId,
	                                  final Employee employee, final int userId) {
		Role siteRole = roleDAO.findSiteRoleByCorporateRole(corporateIds, siteId, roleId);

		if (siteRole == null) {
			Role corporateRole = roleDAO.find(roleId);
			siteRole = new RoleBuilder()
					.accountId(siteId)
					.description(corporateRole.getDescription())
					.name(corporateRole.getName())
					.createdBy(userId)
					.createdDate(new Date())
					.build();

			roleDAO.save(siteRole);
		}

		RoleEmployee roleEmployee = buildRoleEmployee(siteRole, employee, userId);
		roleEmployeeDAO.save(roleEmployee);
	}

	private void updateEmployeeSkillsForRole(final int siteId, final List<Integer> corporateIds,
	                                         final int corporateRoleId, final Employee employee, final int userId) {
		Set<AccountSkill> allSkillsForJobRole = new HashSet<>();
		allSkillsForJobRole.addAll(accountSkillDAO.findByCorporateRoleId(corporateRoleId));
		allSkillsForJobRole.addAll(accountSkillDAO.findRequiredByAccounts(corporateIds));
		allSkillsForJobRole.addAll(accountSkillDAO.findRequiredByAccount(siteId));

		List<AccountSkill> employeeSkills = accountSkillDAO.findByEmployee(employee);
		List<AccountSkillEmployee> accountSkillEmployees = buildAccountSkillEmployee(employee, allSkillsForJobRole,
				employeeSkills, userId);
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
	                                                             final int userId) {
		if (CollectionUtils.isEmpty(allSkillsForJobRole)) {
			return Collections.emptyList();
		}

		Date createdDate = DateBean.today();
		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
		for (AccountSkill accountSkill : allSkillsForJobRole) {
			if (!employeeSkills.contains(accountSkill)) {
				accountSkillEmployees.add(new AccountSkillEmployeeBuilder()
						.accountSkill(accountSkill)
						.employee(employee)
						.createdBy(userId)
						.createdDate(createdDate)
						.startDate(createdDate)
						.build());
			}
		}

		return accountSkillEmployees;
	}

	public void unassignEmployeeFromRole(final Employee employee, final int roleId, final int siteId) {
		Role corporateRole = roleDAO.find(roleId);
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
		Map<Role, Role> corporateToSiteRoles = Utilities.invertMap(siteToCorporateRoles);

		roleAssignmentHelper.deleteProjectRolesFromEmployee(employee, corporateRole);
		roleAssignmentHelper.deleteSiteRoleFromEmployee(employee, corporateRole, corporateToSiteRoles);

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

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		List<Integer> childSiteIds = accountService.getChildOperatorIds(corporateIds);
		Set<Integer> otherEmployeeAssignedSiteIds = getOtherEmployeeAssignedSiteIds(childSiteIds, siteId, employee);

		if (CollectionUtils.isEmpty(otherEmployeeAssignedSiteIds)) {
			removeRequiredCorporateSkillsFromEmployee(employee, corporateIds);
		} else {
			final int contractorId = employee.getAccountId();
			// All of the projects the contractor has been assigned to that is not related to the site
			List<ProjectCompany> projectCompanies = projectCompanyDAO.findByContractorExcludingSite(contractorId, siteId);

			Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
			Set<AccountSkill> requiredSkills = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(projectCompanies, employee, siteToCorporateRoles);
			Set<AccountSkillEmployee> deletableSkills = skillAssignmentHelper.filterNoLongerNeededEmployeeSkills(employee, contractorId, requiredSkills);

			accountSkillEmployeeDAO.deleteByIds(Utilities.getIdsFromCollection(deletableSkills,
					new Utilities.Identitifable<AccountSkillEmployee, Integer>() {
						@Override
						public Integer getId(AccountSkillEmployee accountSkillEmployee) {
							return accountSkillEmployee.getId();
						}
					}));
		}
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
