package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

class EmployeeSkillAssigner {
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private SiteSkillDAO siteSkilLDAO;

	public List<AccountSkillEmployee> skillsToAdd(final List<AccountSkill> skillsToAdd, final List<Employee> employees, int appUserId) {
		Map<AccountSkill, List<Employee>> employeesWithoutSkill = findEmployeesWithoutSkills(skillsToAdd, employees);
		Date now = new Date();

		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
		for (Map.Entry<AccountSkill, List<Employee>> entrySet : employeesWithoutSkill.entrySet()) {
			for (Employee employee : entrySet.getValue()) {
				AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployee(entrySet.getKey(), employee);
				EntityHelper.setCreateAuditFields(accountSkillEmployee, appUserId, now);
				accountSkillEmployee.setStartDate(now);

				accountSkillEmployees.add(accountSkillEmployee);
			}
		}

		return accountSkillEmployees;
	}

	public List<AccountSkillEmployee> skillsToRemove(final List<AccountSkill> skillsToRemove, final List<Employee> employees, final List<Project> projects, final int appUserId) {
		List<AccountSkillEmployee> accountSkillEmployeesToRemove = new ArrayList<>();

		for (Employee employee : employees) {
			Set<AccountSkill> skillsRequiredByOtherAssociations = new HashSet<>();
			skillsRequiredByOtherAssociations.addAll(skillsAssociatedWithEmployeeGroups(employee));
			skillsRequiredByOtherAssociations.addAll(skillsAssociatedWithProjects(projects));
			skillsRequiredByOtherAssociations.addAll(skillsAssociatedWithProjectSites(projects));

			List<AccountSkill> remainingSkillsToRemove = new ArrayList<>(skillsToRemove);
			remainingSkillsToRemove.removeAll(skillsRequiredByOtherAssociations);

			accountSkillEmployeesToRemove.addAll(accountSkillEmployeeService.findByEmployeeAndSkills(employee, remainingSkillsToRemove));
		}

		return null;
	}

	private List<AccountSkill> skillsAssociatedWithEmployeeGroups(Employee employee) {
		List<Group> groups = ExtractorUtil.extractList(employee.getGroups(), AccountGroupEmployee.GROUP_EXTRACTOR);
		return accountSkillDAO.findByGroups(groups);
	}

	private Set<AccountSkill> skillsAssociatedWithProjects(List<Project> projects) {
		Set<AccountSkill> accountSkills = new HashSet<>();

		for (Project project : projects) {
			accountSkills.addAll(ExtractorUtil.extractList(project.getSkills(), ProjectSkill.SKILL_EXTRACTOR));
		}

		return accountSkills;
	}

	private List<AccountSkill> skillsAssociatedWithProjectSites(List<Project> projects) {
		List<Integer> accountIds = ExtractorUtil.extractList(projects, new Extractor<Project, Integer>() {
			@Override
			public Integer extract(Project project) {
				return project.getAccountId();
			}
		});

		ListUtil.removeDuplicatesAndSort(accountIds);

		List<SiteSkill> siteSkills = siteSkilLDAO.findByAccountIds(accountIds);
		return ExtractorUtil.extractList(siteSkills, SiteSkill.SKILL_EXTRACTOR);
	}

	public Map<AccountSkill, List<Employee>> findEmployeesWithoutSkills(List<AccountSkill> skills, List<Employee> employees) {
		List<AccountSkillEmployee> existingAccountSkillEmployees = accountSkillEmployeeService.findByEmployeesAndSkills(employees, skills);

		Map<AccountSkill, List<Employee>> allSkillsAndEmployees = new TreeMap<>();
		for (AccountSkill skill : skills) {
			allSkillsAndEmployees.put(skill, new ArrayList<Employee>());

			for (Employee employee : employees) {
				allSkillsAndEmployees.get(skill).add(employee);
			}
		}

		Map<AccountSkill, List<Employee>> mapOfExistingSkilLEmployees = new TreeMap<>();
		for (AccountSkillEmployee existing : existingAccountSkillEmployees) {
			AccountSkill skill = existing.getSkill();
			if (!mapOfExistingSkilLEmployees.containsKey(skill)) {
				mapOfExistingSkilLEmployees.put(skill, new ArrayList<Employee>());
			}

			mapOfExistingSkilLEmployees.get(skill).add(existing.getEmployee());
		}

		Map<AccountSkill, List<Employee>> accountSkillsMissingEmployees = new TreeMap<>();
		for (Map.Entry<AccountSkill, List<Employee>> entry : allSkillsAndEmployees.entrySet()) {
			List<Employee> employeesWithoutSkill = entry.getValue();
			employeesWithoutSkill.removeAll(mapOfExistingSkilLEmployees.get(entry.getKey()));

			accountSkillsMissingEmployees.put(entry.getKey(), employeesWithoutSkill);
		}

		return accountSkillsMissingEmployees;
	}
}
