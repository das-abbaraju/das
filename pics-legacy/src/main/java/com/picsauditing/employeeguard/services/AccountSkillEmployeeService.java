package com.picsauditing.employeeguard.services;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.calculator.ExpirationCalculator;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AccountSkillEmployeeService {

	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private SkillUsageLocator skillUsageLocator;

	public List<AccountSkillEmployee> findByProfile(final Profile profile) {
		return accountSkillEmployeeDAO.findByProfile(profile);
	}

	public List<AccountSkillEmployee> findByEmployeeAndSkills(final Employee employee, final List<AccountSkill> accountSkills) {
		return accountSkillEmployeeDAO.findByEmployeeAndSkills(employee, accountSkills);
	}

	public List<AccountSkillEmployee> findByEmployeesAndSkills(final List<Employee> employees, final List<AccountSkill> accountSkills) {
		return accountSkillEmployeeDAO.findByEmployeesAndSkills(employees, accountSkills);
	}

	public AccountSkillEmployee linkProfileDocumentToEmployeeSkill(final AccountSkillEmployee accountSkillEmployee, final ProfileDocument profileDocument) {
		accountSkillEmployee.setProfileDocument(profileDocument);
		accountSkillEmployee.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillEmployee));
		return accountSkillEmployeeDAO.save(accountSkillEmployee);
	}

	public AccountSkillEmployee getAccountSkillEmployeeForProfileAndSkill(Profile profile, AccountSkill skill) {
		return accountSkillEmployeeDAO.findByProfileAndSkill(profile, skill);
	}

	public void linkEmployeesToSkill(final AccountSkill skill, final int appUserId) {
		List<AccountSkillEmployee> newAccountSkillEmployees = getNewAccountSkillEmployees(skill);
		List<AccountSkillEmployee> existingAccountSkillEmployees = getExistingAccountSkillEmployees(skill);
		List<AccountSkillEmployee> employeeSkills = IntersectionAndComplementProcess.intersection(newAccountSkillEmployees,
				existingAccountSkillEmployees, AccountSkillEmployee.COMPARATOR, new BaseEntityCallback(appUserId, new Date()));

		accountSkillEmployeeDAO.save(employeeSkills);
	}

	/**
	 * This goes through all the employee's linked groups (through the contractor), job roles (through linked projects),
	 * and operator connections (also through projects) and calculates which skills to add, retain, or delete
	 *
	 * @param employee
	 * @param appUserId
	 * @param timestamp
	 */
	public void linkEmployeeToSkills(final Employee employee, int appUserId, Date timestamp) {
		SkillUsage skillUsage = skillUsageLocator.getSkillUsagesForEmployee(employee);

		List<AccountSkillEmployee> newSkillEmployees = buildNewAccountSkillEmployees(employee, appUserId, timestamp, skillUsage.allSkills());

		BaseEntityCallback<AccountSkillEmployee> skillEmployeeCallback = new BaseEntityCallback<>(appUserId, timestamp);
		newSkillEmployees = IntersectionAndComplementProcess.intersection(
				newSkillEmployees,
				employee.getSkills(),
				AccountSkillEmployee.COMPARATOR,
				skillEmployeeCallback);

		employee.setSkills(newSkillEmployees);
		employeeDAO.save(employee);
		accountSkillEmployeeDAO.delete(skillEmployeeCallback.getRemovedEntities());
	}

	private List<AccountSkillEmployee> buildNewAccountSkillEmployees(Employee employee, int appUserId, Date timestamp, Collection<AccountSkill> skills) {
		List<AccountSkillEmployee> newSkillEmployees = new ArrayList<>();

		for (AccountSkill accountSkill : skills) {
			AccountSkillEmployee newSkillEmployee = new AccountSkillEmployee(accountSkill, employee);
			newSkillEmployee.setStartDate(timestamp);
			EntityHelper.setCreateAuditFields(newSkillEmployee, appUserId, timestamp);
			newSkillEmployees.add(newSkillEmployee);
		}

		return newSkillEmployees;
	}

	public void linkEmployeesToSkill(final Group group, final int userId) {
		List<AccountSkillEmployee> newAccountSkillEmployees = getNewAccountSkillEmployees(group);
		List<AccountSkillEmployee> existingAccountSkillEmployees = getExistingAccountSkillEmployees(group);
		List<AccountSkillEmployee> employeeSkills = IntersectionAndComplementProcess.intersection(newAccountSkillEmployees,
				existingAccountSkillEmployees, AccountSkillEmployee.COMPARATOR, new BaseEntityCallback(userId, new Date()));

		accountSkillEmployeeDAO.save(employeeSkills);
	}

	public void linkEmployeesToSkill(final Role role, final int userId) {
		List<AccountSkillEmployee> newAccountSkillEmployees = getNewAccountSkillEmployees(role);
		List<AccountSkillEmployee> existingAccountSkillEmployees = getExistingAccountSkillEmployees(role);
		List<AccountSkillEmployee> employeeSkills = IntersectionAndComplementProcess.intersection(newAccountSkillEmployees,
				existingAccountSkillEmployees, AccountSkillEmployee.COMPARATOR, new BaseEntityCallback(userId, new Date()));

		accountSkillEmployeeDAO.save(employeeSkills);
	}

	private List<AccountSkillEmployee> getNewAccountSkillEmployees(final AccountSkill skill) {
		List<Employee> employees;
		Date now = new Date();

		if (skill.getRuleType().isRequired()) {
			// TODO we may need to apply a required skill across employees tied to a PROJECT
			employees = employeeDAO.findByAccount(skill.getAccountId());
		} else {
			//find all skill groups
			List<AccountSkillGroup> groups = skill.getGroups();
			//find all employees in groups
			Set<Integer> groupIDs = new HashSet<>();
			for (AccountSkillGroup asg : groups) {
				groupIDs.add(asg.getGroup().getId());
			}

			employees = employeeDAO.findEmployeesByGroups(groupIDs);
		}

		List<AccountSkillEmployee> employeeSkills = new ArrayList<>();
		for (Employee employee : employees) {
			AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee(skill, employee);
			accountSkillEmployee.setStartDate(now);

			employeeSkills.add(accountSkillEmployee);
		}

		return employeeSkills;
	}

	private List<AccountSkillEmployee> getExistingAccountSkillEmployees(final AccountSkill skill) {
		return accountSkillEmployeeDAO.findBySkill(skill);
	}

	private List<AccountSkillEmployee> getNewAccountSkillEmployees(final Group group) {
		List<AccountSkillGroup> skills = group.getSkills();
		List<GroupEmployee> employees = group.getEmployees();

		List<AccountSkillEmployee> skillEmployees = new ArrayList<>();

		Date now = new Date();
		for (AccountSkillGroup skillGroup : skills) {
			for (GroupEmployee groupEmployee : employees) {
				AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee(skillGroup.getSkill(), groupEmployee.getEmployee());
				accountSkillEmployee.setStartDate(now);

				skillEmployees.add(accountSkillEmployee);
			}
		}

		return skillEmployees;
	}

	private List<AccountSkillEmployee> getNewAccountSkillEmployees(final Role role) {
		List<AccountSkillRole> skills = role.getSkills();
		List<RoleEmployee> employees = role.getEmployees();

		List<AccountSkillEmployee> skillEmployees = new ArrayList<>();

		Date now = new Date();

		if (CollectionUtils.isNotEmpty(skills) && CollectionUtils.isNotEmpty(employees)) {
			for (AccountSkillRole skillRole : skills) {
				for (RoleEmployee groupEmployee : employees) {
					AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee(skillRole.getSkill(), groupEmployee.getEmployee());
					accountSkillEmployee.setStartDate(now);

					skillEmployees.add(accountSkillEmployee);
				}
			}
		}

		return skillEmployees;
	}

	private List<AccountSkillEmployee> getExistingAccountSkillEmployees(final Group group) {
		List<AccountSkill> skills = new ArrayList<>();
		for (AccountSkillGroup skillGroup : group.getSkills()) {
			skills.add(skillGroup.getSkill());
		}

		return accountSkillEmployeeDAO.findBySkills(skills);
	}

	private List<AccountSkillEmployee> getExistingAccountSkillEmployees(final Role role) {
		List<AccountSkill> skills = new ArrayList<>();
		for (AccountSkillRole skillRole : role.getSkills()) {
			skills.add(skillRole.getSkill());
		}

		return accountSkillEmployeeDAO.findBySkills(skills);
	}

	public List<AccountSkillEmployee> getSkillsForAccountAndEmployee(Employee employee) {
		return accountSkillEmployeeDAO.findByAccountAndEmployee(employee);
	}

	public void save(AccountSkillEmployee accountSkillEmployee) {
		accountSkillEmployeeDAO.save(accountSkillEmployee);
	}

	public void save(List<AccountSkillEmployee> accountSkillEmployees) {
		if (CollectionUtils.isNotEmpty(accountSkillEmployees)) {
			accountSkillEmployeeDAO.save(accountSkillEmployees);
		}
	}

	public void update(AccountSkillEmployee accountSkillEmployee, final SkillDocumentForm skillDocumentForm) {
		AccountSkill skill = accountSkillEmployee.getSkill();
		SkillType skillType = skill.getSkillType();

		if (skillType.isCertification()) {
			ProfileDocument document = profileDocumentService.getDocument(Integer.toString(skillDocumentForm.getDocumentId()));
			linkProfileDocumentToEmployeeSkill(accountSkillEmployee, document);
		} else if (skillType.isTraining()) {
			if (skillDocumentForm != null && skillDocumentForm.isVerified()) {
				accountSkillEmployee.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillEmployee));
			} else {
				accountSkillEmployee.setEndDate(null);
			}

			accountSkillEmployeeDAO.save(accountSkillEmployee);
		}
	}

	public void update(AccountSkillEmployee accountSkillEmployee, final ProfileDocument document) {
		AccountSkill skill = accountSkillEmployee.getSkill();
		SkillType skillType = skill.getSkillType();

		if (skillType.isCertification()) {
			linkProfileDocumentToEmployeeSkill(accountSkillEmployee, document);
			accountSkillEmployeeDAO.save(accountSkillEmployee);
		}
	}

	public List<AccountSkillEmployee> getAccountSkillEmployeeForProjectAndContractor(Project project, int accountId) {
		return accountSkillEmployeeDAO.findByProjectAndContractor(project, accountId);
	}

	public List<AccountSkillEmployee> getSkillsForAccount(int accountId) {
		return accountSkillEmployeeDAO.findByEmployeeAccount(accountId);
	}

	public Map<Employee, Set<AccountSkillEmployee>> getSkillMapForAccountAndRole(int accountId, int roleId) {
		List<AccountSkillEmployee> roleSkills = accountSkillEmployeeDAO.findByContractorAndRole(accountId, roleId);

		Map<Employee, Set<AccountSkillEmployee>> skillMap = new HashMap<>();
		for (AccountSkillEmployee accountSkillEmployee : roleSkills) {
			PicsCollectionUtil.addToMapOfKeyToSet(skillMap, accountSkillEmployee.getEmployee(), accountSkillEmployee);
		}

		return skillMap;
	}

	public Map<Employee, Set<AccountSkillEmployee>> getEmployeeSkillMapForContractorsAndSite(
			final Set<Integer> contractorIds,
			final int siteId,
			final List<Integer> corporateIds,
			final Map<Role, Role> siteToCorporateRoles) {
		Collection<Role> corporateRoles = siteToCorporateRoles.values();
		Set<Integer> siteAndCorporateIds = new HashSet<>(corporateIds);
		siteAndCorporateIds.add(siteId);

		Set<AccountSkillEmployee> allSiteSkills = getAllSiteSkills(contractorIds, siteId, corporateRoles, siteAndCorporateIds);

		Map<Employee, Set<AccountSkillEmployee>> employeeSkills = new HashMap<>();
		for (AccountSkillEmployee accountSkillEmployee : allSiteSkills) {
			PicsCollectionUtil.addToMapOfKeyToSet(employeeSkills, accountSkillEmployee.getEmployee(), accountSkillEmployee);
		}

		return employeeSkills;
	}

	private Set<AccountSkillEmployee> getAllSiteSkills(Set<Integer> contractorIds, int siteId, Collection<Role> corporateRoles, Set<Integer> siteAndCorporateIds) {
		Set<AccountSkillEmployee> allSiteSkills = new HashSet<>();

		allSiteSkills.addAll(accountSkillEmployeeDAO.getProjectRoleSkillsForContractorsAndSite(contractorIds, siteId));
		allSiteSkills.addAll(accountSkillEmployeeDAO.getProjectSkillsForContractorsAndSite(contractorIds, siteId));
		allSiteSkills.addAll(accountSkillEmployeeDAO.getRoleSkillsForContractorsAndRoles(contractorIds, corporateRoles));
		allSiteSkills.addAll(accountSkillEmployeeDAO.getSiteSkillsForContractorsAndSites(contractorIds, siteAndCorporateIds));

		return allSiteSkills;
	}

	public Table<Employee, AccountSkill, AccountSkillEmployee> buildTable(final List<Employee> employees, final List<AccountSkill> skills) {
		List<AccountSkillEmployee> accountSkillEmployees = findByEmployeesAndSkills(employees, skills);

		Table<Employee, AccountSkill, AccountSkillEmployee> table = TreeBasedTable.create();
		for (Employee employee : employees) {
			for (AccountSkill skill : skills) {
				table.put(employee, skill, findAccountSkillEmployeeByEmployeeAndSkill(accountSkillEmployees, employee, skill));
			}
		}

		return table;
	}

	private AccountSkillEmployee findAccountSkillEmployeeByEmployeeAndSkill(List<AccountSkillEmployee> accountSkillEmployees, Employee employee, AccountSkill skill) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			if (skill.equals(accountSkillEmployee.getSkill()) && employee.equals(accountSkillEmployee.getEmployee())) {
				return accountSkillEmployee;
			}
		}

		return new AccountSkillEmployeeBuilder().endDate(DateBean.today()).build();
	}
}
