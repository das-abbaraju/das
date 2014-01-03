package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.services.calculator.ExpirationCalculator;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AccountSkillEmployeeService {

	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private SkillService skillService;

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
		List<AccountSkillEmployee> newAccountSkillEmployees = getNewAccountSkillEmployeesForEmployee(employee, appUserId, timestamp);
		accountSkillEmployeeDAO.save(newAccountSkillEmployees);
	}

	public void addNewAccountSkillEmployees(final Employee employee, final List<AccountSkill> addedSkills, final int appUserId) {
		Date now = new Date();

		List<AccountSkillEmployee> newAccountSkillEmployees = createNewAccountSkillEmployeesForEmployee(addedSkills, now, employee);
		List<AccountSkillEmployee> existingAccountSkillEmployees = accountSkillEmployeeDAO.findByEmployeeAndSkills(employee, addedSkills);
		newAccountSkillEmployees = IntersectionAndComplementProcess.intersection(
				newAccountSkillEmployees,
				existingAccountSkillEmployees,
				AccountSkillEmployee.COMPARATOR,
				new BaseEntityCallback<AccountSkillEmployee>(appUserId, now));

		accountSkillEmployeeDAO.save(newAccountSkillEmployees);
	}

	public void addNewAccountSkillEmployees(final List<Employee> employees, final List<AccountSkill> addedSkills, final int appUserId) {
		for (Employee employee : employees) {
			addNewAccountSkillEmployees(employee, addedSkills, appUserId);
		}
	}

	public void deleteAccountSkillEmployees(final Employee employee, final List<AccountSkill> deletedSkills, final int appUserId) {
		Set<AccountSkill> skillsRequiredForEmployee = skillService.findAllSkillsRequiredForEmployee(employee);
		deletedSkills.removeAll(skillsRequiredForEmployee);

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO.findByEmployeeAndSkills(employee, deletedSkills);
		EntityHelper.softDelete(accountSkillEmployees, appUserId);
		accountSkillEmployeeDAO.delete(accountSkillEmployees);
	}

	public void deleteAccountSkillEmployees(final List<Employee> employees, final List<AccountSkill> deletedSkills, final int appUserId) {
		for (Employee employee : employees) {
			deleteAccountSkillEmployees(employee, deletedSkills, appUserId);
		}
	}

	private List<AccountSkillEmployee> createNewAccountSkillEmployeesForEmployee(Collection<AccountSkill> skills, Date timestamp, Employee employee) {
		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();

		for (AccountSkill skill : skills) {
			AccountSkillEmployee skillEmployee = new AccountSkillEmployee(skill, employee);
			skillEmployee.setStartDate(timestamp);
			accountSkillEmployees.add(skillEmployee);
		}

		return accountSkillEmployees;
	}

	private void saveAccountSkillEmployees(List<AccountSkillEmployee> newAccountSkillEmployees,
	                                       List<AccountSkillEmployee> existingAccountSkillEmployees, int appUserId, Date timestamp) {
		List<AccountSkillEmployee> employeeSkills = IntersectionAndComplementProcess.intersection(newAccountSkillEmployees,
				existingAccountSkillEmployees, AccountSkillEmployee.COMPARATOR, new BaseEntityCallback(appUserId, timestamp));

		accountSkillEmployeeDAO.save(employeeSkills);
	}

	private List<AccountSkillEmployee> getNewAccountSkillEmployeesForEmployee(final Employee employee, final int appUserId, Date timestamp) {
		List<AccountSkill> skillsToKeep = new ArrayList<>(skillService.findAllSkillsRequiredForEmployee(employee));
		List<AccountSkillEmployee> newAccountSkillEmployees = createNewAccountSkillEmployeesForEmployee(skillsToKeep, timestamp, employee);
		List<AccountSkillEmployee> existingAccountSkillEmployees = employee.getSkills();

		newAccountSkillEmployees = IntersectionAndComplementProcess.intersection(
				newAccountSkillEmployees,
				existingAccountSkillEmployees,
				AccountSkillEmployee.COMPARATOR,
				new BaseEntityCallback<AccountSkillEmployee>(appUserId, timestamp));

		return newAccountSkillEmployees;
	}

	private List<Group> getGroups(List<GroupEmployee> groupEmployees) {
		List<Group> groups = new ArrayList<>();
		for (GroupEmployee groupEmployee : groupEmployees) {
			groups.add(groupEmployee.getGroup());
		}

		return groups;
	}

	public void linkEmployeesToSkill(final Group group, final int userId) {
		List<AccountSkillEmployee> newAccountSkillEmployees = getNewAccountSkillEmployees(group);
		List<AccountSkillEmployee> existingAccountSkillEmployees = getExistingAccountSkillEmployees(group);
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

	private List<AccountSkillEmployee> getExistingAccountSkillEmployees(final Group group) {
		List<AccountSkill> skills = new ArrayList<>();
		for (AccountSkillGroup skillGroup : group.getSkills()) {
			skills.add(skillGroup.getSkill());
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

	public List<AccountSkillEmployee> getAccountSkillEmployeeForAccountAndSkills(final int accountId, final List<AccountSkill> accountSkills) {
		return accountSkillEmployeeDAO.findByEmployeeAccountAndSkills(accountId, accountSkills);
	}

	public List<AccountSkillEmployee> getAccountSkillEmployeeForProjectAndContractor(Project project, int accountId) {
		return accountSkillEmployeeDAO.findByProjectAndContractor(project, accountId);
	}
}
