package com.picsauditing.employeeguard.services;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.calculator.ExpirationCalculator;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Deprecated
public class AccountSkillEmployeeService {

	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private ProfileDocumentService profileDocumentService;

	public List<AccountSkillEmployee> findByProfile(final Profile profile) {
		return accountSkillEmployeeDAO.findByProfile(profile);
	}

	public List<AccountSkillEmployee> findByEmployeesAndSkills(final List<Employee> employees, final List<AccountSkill> accountSkills) {
		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		return accountSkillEmployeeDAO.findByEmployeesAndSkills(employees, accountSkills);
	}

	public AccountSkillEmployee linkProfileDocumentToEmployeeSkill(final AccountSkillEmployee accountSkillEmployee, final ProfileDocument profileDocument) {
		accountSkillEmployee.setProfileDocument(profileDocument);
		accountSkillEmployee.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillEmployee));
		return accountSkillEmployeeDAO.save(accountSkillEmployee);
	}

	public void linkProfileDocumentToEmployeeSkills(final List<AccountSkillEmployee> accountSkillEmployees,
													final ProfileDocument profileDocument) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			accountSkillEmployee.setProfileDocument(profileDocument);
			accountSkillEmployee.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillEmployee));
		}

		accountSkillEmployeeDAO.save(accountSkillEmployees);
	}

	public AccountSkillEmployee getAccountSkillEmployeeForProfileAndSkill(Profile profile, AccountSkill skill) {
		return accountSkillEmployeeDAO.findByProfileAndSkill(profile, skill);
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
			ProfileDocument document = profileDocumentService.getDocument(skillDocumentForm.getDocumentId());
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

	public void update(final AccountSkill accountSkill, final Profile profile, final SkillDocumentForm skillDocumentForm) {
		SkillType skillType = accountSkill.getSkillType();

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO
				.findBySkillAndProfile(accountSkill, profile);

		accountSkillEmployees = addNewAccountSkillEmployees(accountSkillEmployees, profile, accountSkill);

		if (skillType.isCertification()) {
			ProfileDocument document = profileDocumentService.getDocument(skillDocumentForm.getDocumentId());
			linkProfileDocumentToEmployeeSkills(accountSkillEmployees, document);
		} else if (skillType.isTraining()) {
			if (skillDocumentForm != null && skillDocumentForm.isVerified()) {
				for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
					accountSkillEmployee.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillEmployee));
				}
			} else {
				for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
					accountSkillEmployee.setEndDate(null);
				}
			}

			accountSkillEmployeeDAO.save(accountSkillEmployees);
		}
	}

	public List<AccountSkillEmployee> addNewAccountSkillEmployees(final List<AccountSkillEmployee> accountSkillEmployees,
																  final Profile profile,
																  final AccountSkill accountSkill) {
		List<AccountSkillEmployee> allAccountSkillEmployees = new ArrayList<>(accountSkillEmployees);
		for (Employee employee : profile.getEmployees()) {
			if (!foundAccountSkillEmployee(employee, allAccountSkillEmployees)) {
				allAccountSkillEmployees.add(new AccountSkillEmployeeBuilder()
						.employee(employee)
						.accountSkill(accountSkill)
						.createdBy(1)
						.createdDate(DateBean.today())
						.startDate(DateBean.today())
						.build());
			}
		}

		return allAccountSkillEmployees;
	}

	private boolean foundAccountSkillEmployee(final Employee employee, List<AccountSkillEmployee> accountSkillEmployees) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			if (accountSkillEmployee.getEmployee().equals(employee)) {
				return true;
			}
		}

		return false;
	}

	public void update(AccountSkillEmployee accountSkillEmployee, final ProfileDocument document) {
		AccountSkill skill = accountSkillEmployee.getSkill();
		SkillType skillType = skill.getSkillType();

		if (skillType.isCertification()) {
			linkProfileDocumentToEmployeeSkill(accountSkillEmployee, document);
			accountSkillEmployeeDAO.save(accountSkillEmployee);
		}
	}

	public List<AccountSkillEmployee> getAccountSkillEmployeeForProjectAndContractor(final Project project,
																					 final int accountId) {
		return accountSkillEmployeeDAO.findByProjectAndContractor(project, accountId);
	}

	public List<AccountSkillEmployee> getSkillsForAccount(final int accountId) {
		return accountSkillEmployeeDAO.findByEmployeeAccount(accountId);
	}

	public Map<Employee, Set<AccountSkillEmployee>> getSkillMapForAccountAndRole(final int accountId, final int roleId) {
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

		return new AccountSkillEmployeeBuilder().build();
	}
}
