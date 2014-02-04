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
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.GenericPredicate;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId, final int contractorId) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		// find roles by this site that duplicate roles with the above corporate ids
		Map<Role, Role> siteRoleToCorporateRole = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);

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

	public void assignEmployeeToSite(final int siteId, final int corporateRoleId, final Employee employee,
	                                 final int userId) {
		List<Integer> corporateIds = Collections.unmodifiableList(accountService.getTopmostCorporateAccountIds(siteId));
		assignEmployeeToRole(siteId, corporateIds, corporateRoleId, employee, userId);
		updateEmployeeSkillsForRole(siteId, corporateIds, corporateRoleId, employee, userId);
	}

	private void assignEmployeeToRole(final int siteId, final List<Integer> corporateIds, final int roleId,
	                                  final Employee employee, final int userId) {
		Role siteRole = roleDAO.findSiteRoleByCorporateRole(corporateIds, siteId, roleId);
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
						.build());
			}
		}

		return accountSkillEmployees;
	}

	@Transactional
	public void unassignEmployeeFromSite(Employee employee, final int siteId) {
		roleAssignmentHelper.deleteProjectRolesFromEmployee(employee.getId(), siteId);
		roleAssignmentHelper.deleteSiteRolesFromEmployee(employee.getId(), siteId);

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		List<Integer> otherSiteIds = getOtherSiteIds(corporateIds, siteId);
		if (CollectionUtils.isEmpty(otherSiteIds)) {
            removeRequiredCorporateSkillsFromEmployee(employee, corporateIds);
		} else {
			final int contractorId = employee.getAccountId();
            // All of the projects the contractor has been assigned to that is not related to the site
			List<ProjectCompany> projectCompanies = projectCompanyDAO.findByContractorExcludingSite(contractorId, siteId);

            Map<Role, Role> siteToCorporateRoles = roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
			Set<AccountSkill> requiredSkills = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(projectCompanies, employee, siteToCorporateRoles);
			Set<AccountSkillEmployee> deletableSkills = skillAssignmentHelper.filterNoLongerNeededEmployeeSkills(employee, contractorId, requiredSkills);

			accountSkillEmployeeDAO.delete(new ArrayList<>(deletableSkills));
		}
	}

    private void removeRequiredCorporateSkillsFromEmployee(Employee employee, List<Integer> corporateIds) {
        List<AccountSkillEmployee> accountSkillEmployees =
                accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(employee.getId(), corporateIds);
        accountSkillEmployeeDAO.delete(accountSkillEmployees);
    }

//	private void deleteProjectRolesFromEmployee(int employeeId, int siteId) {
//		List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByEmployeeAndSiteId(employeeId, siteId);
//		projectRoleEmployeeDAO.delete(projectRoleEmployees);
//	}

//	private void deleteSiteRolesFromEmployee(int employeeId, int siteId) {
//		List<RoleEmployee> roleEmployees = roleEmployeeDAO.findByEmployeeAndSiteId(employeeId, siteId);
//		roleEmployeeDAO.delete(roleEmployees);
//	}

	private List<Integer> getOtherSiteIds(List<Integer> corporateIds, int siteId) {
		List<Integer> otherSiteIds = accountService.getChildOperatorIds(corporateIds);
		Iterator<Integer> siteIdIterator = otherSiteIds.iterator();

		while (siteIdIterator.hasNext()) {
			int otherSiteId = siteIdIterator.next();
			if (siteId == otherSiteId) {
				siteIdIterator.remove();
			}
		}

		return otherSiteIds;
	}

//	private Set<AccountSkill> getRequiredSkillsFromProjectsAndSiteRoles(List<ProjectCompany> projectCompanies, Employee employee, Map<Role, Role> siteToCorporateRoles) {
//		Set<AccountSkill> required = new HashSet<>();
//
//		for (ProjectCompany projectCompany : projectCompanies) {
//			Project project = projectCompany.getProject();
//
//			required.addAll(getProjectSkills(project));
//			required.addAll(getProjectRoleSkills(project));
//		}
//
//		List<Integer> siteIds = getSiteIdsFromProjects(projectCompanies);
//
//        required.addAll(getSiteSkills(new HashSet<>(siteIds)));
//        required.addAll(getSiteRoleSkills(employee, siteIds, siteToCorporateRoles));
//
//		return required;
//	}

//	private Set<AccountSkill> getProjectSkills(Project project) {
//        Set<AccountSkill> requiredSkills = new HashSet<>();
//
//		for (ProjectSkill projectSkill : project.getSkills()) {
//			requiredSkills.add(projectSkill.getSkill());
//		}
//
//        return requiredSkills;
//	}

//	private Set<AccountSkill> getProjectRoleSkills(Project project) {
//        Set<AccountSkill> requiredSkills = new HashSet<>();
//
//		for (ProjectRole projectRole : project.getRoles()) {
//			for (AccountSkillRole accountSkillRole : projectRole.getRole().getSkills()) {
//				requiredSkills.add(accountSkillRole.getSkill());
//			}
//		}
//
//        return requiredSkills;
//	}

//	private Set<AccountSkill> getSiteSkills(Set<Integer> siteIds) {
//        Set<AccountSkill> requiredSkills = new HashSet<>();
//		Set<Integer> siteAndCorporateIds = new HashSet<>();
//
//		for (Integer siteId : siteIds) {
//			siteAndCorporateIds.addAll(accountService.getTopmostCorporateAccountIds(siteId));
//		}
//
//		siteAndCorporateIds.addAll(siteIds);
//		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountIds(siteAndCorporateIds);
//		for (SiteSkill siteSkill : siteSkills) {
//			requiredSkills.add(siteSkill.getSkill());
//		}
//
//        return requiredSkills;
//	}

//	private Set<AccountSkill> getSiteRoleSkills(Employee employee, List<Integer> siteIds, Map<Role, Role> siteToCorporateRoles) {
//        Set<AccountSkill> requiredSkills = new HashSet<>();
//		List<RoleEmployee> siteRoles = roleEmployeeDAO.findByEmployeeAndSiteIds(employee.getId(), siteIds);
//
//		for (RoleEmployee roleEmployee : siteRoles) {
//            Role corporateRole = siteToCorporateRoles.get(roleEmployee.getRole());
//
//			for (AccountSkillRole accountSkillRole : corporateRole.getSkills()) {
//				requiredSkills.add(accountSkillRole.getSkill());
//			}
//		}
//
//        return requiredSkills;
//	}

//	private List<Integer> getSiteIdsFromProjects(List<ProjectCompany> projectCompanies) {
//		return ExtractorUtil.extractList(projectCompanies, new Extractor<ProjectCompany, Integer>() {
//			@Override
//			public Integer extract(ProjectCompany projectCompany) {
//				return projectCompany.getProject().getAccountId();
//			}
//		});
//	}

//	private Set<AccountSkillEmployee> filterNoLongerNeededEmployeeSkills(Employee employee,
//	                                                                     final int contractorId,
//	                                                                     final Set<AccountSkill> requiredSkills) {
//		HashSet<AccountSkillEmployee> accountSkillEmployees = new HashSet<>(employee.getSkills());
//
//		CollectionUtils.filter(accountSkillEmployees, new GenericPredicate<AccountSkillEmployee>() {
//			@Override
//			public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
//				boolean notContractorSkill = accountSkillEmployee.getSkill().getAccountId() != contractorId;
//				boolean notRequiredSkill = !requiredSkills.contains(accountSkillEmployee.getSkill());
//				return notContractorSkill && notRequiredSkill;
//			}
//		});
//
//		return accountSkillEmployees;
//	}
}
