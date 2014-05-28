package com.picsauditing.employeeguard.models.factories;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.ProjectAssignmentBreakdown;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;

import java.util.*;

public class ProjectAssignmentBreakdownFactory {

	public ProjectAssignmentBreakdown create(final Map<SkillStatus, Integer> statusCount) {
		return new ProjectAssignmentBreakdown(statusCount);
	}

	public ProjectAssignmentBreakdown create(final Collection<ProjectRoleEmployee> projectRoleEmployees,
											 final Collection<AccountSkillProfile> accountSkillProfiles,
											 final Collection<Employee> employees) {
		Map<Employee, Set<AccountSkill>> employeeSkills = getEmployeeSkill(projectRoleEmployees);
		Table<Employee, AccountSkill, AccountSkillProfile> table = buildTable(employees, accountSkillProfiles);

		Map<SkillStatus, Integer> results = new HashMap<>();
		for (Map.Entry<Employee, Set<AccountSkill>> employeeSkill : employeeSkills.entrySet()) {
			appendResults(results, employeeSkill.getKey(), employeeSkill.getValue(), table);
		}

		return new ProjectAssignmentBreakdown(results);
	}

	private void appendResults(Map<SkillStatus, Integer> results, Employee employee, Set<AccountSkill> accountSkills,
							   Table<Employee, AccountSkill, AccountSkillProfile> table) {
		SkillStatus skillStatus = getWorstStatus(employee, accountSkills, table);
		if (!results.containsKey(skillStatus)) {
			results.put(skillStatus, 0);
		}

		results.put(skillStatus, results.get(skillStatus) + 1);

	}

	private SkillStatus getWorstStatus(final Employee employee,
									   final Set<AccountSkill> accountSkills,
									   final Table<Employee, AccountSkill, AccountSkillProfile> table) {
		SkillStatus worst = SkillStatus.Completed;
		for (AccountSkill accountSkill : accountSkills) {
			AccountSkillProfile accountSkillProfile = table.get(employee, accountSkill);
			SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);

			if (skillStatus.compareTo(worst) < 0) {
				worst = skillStatus;
			}
		}

		return worst;
	}

	private Table<Employee, AccountSkill, AccountSkillProfile> buildTable(final Collection<Employee> employees,
																		  final Collection<AccountSkillProfile> accountSkillProfiles) {
		Table<Employee, AccountSkill, AccountSkillProfile> table = TreeBasedTable.create();
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			for (Employee employee : employees) {
				if (accountSkillProfile.getProfile().getEmployees().contains(employee)) {
					table.put(employee, accountSkillProfile.getSkill(), accountSkillProfile);
				}
			}
		}

		return table;
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkill(Collection<ProjectRoleEmployee> projectRoleEmployees) {
		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
			Employee employee = projectRoleEmployee.getEmployee();
			if (!employeeSkills.containsKey(employeeSkills)) {
				employeeSkills.put(employee, new HashSet<AccountSkill>());
			}

			employeeSkills.get(employee).addAll(getAccountSkillsFromProjectRoleEmployee(projectRoleEmployee));
		}

		return employeeSkills;
	}

	private Set<AccountSkill> getAccountSkillsFromProjectRoleEmployee(ProjectRoleEmployee projectRoleEmployee) {
		Set<AccountSkill> accountSkills = new HashSet<>();
		for (AccountSkillRole accountSkillRole : projectRoleEmployee.getProjectRole().getRole().getSkills()) {
			accountSkills.add(accountSkillRole.getSkill());
		}

		return accountSkills;
	}
}
