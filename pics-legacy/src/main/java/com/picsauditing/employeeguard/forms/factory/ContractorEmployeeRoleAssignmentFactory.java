package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class ContractorEmployeeRoleAssignmentFactory {

	public List<ContractorEmployeeRoleAssignment> build(final List<Employee> contractorEmployees,
	                                                    final Role role,
	                                                    final Set<Employee> assignedEmployees,
	                                                    final Map<Employee, Set<AccountSkillEmployee>> employeeSkills) {
		if (CollectionUtils.isEmpty(contractorEmployees)
				|| role == null
				|| CollectionUtils.isEmpty(assignedEmployees)
				|| MapUtils.isEmpty(employeeSkills)) {
			return Collections.emptyList();
		}

		List<ContractorEmployeeRoleAssignment> assignments = new ArrayList<>();
		List<AccountSkillRole> roleSkills = role.getSkills();

		for (Employee employee : contractorEmployees) {
			boolean assigned = CollectionUtils.isNotEmpty(assignedEmployees) && assignedEmployees.contains(employee);
			List<SkillStatus> statuses = initializeSkillStatuses(roleSkills);

			if (employeeSkills.containsKey(employee)) {
				statuses = calculateSkillStatuses(roleSkills, employeeSkills.get(employee));
			}

			assignments.add(new ContractorEmployeeRoleAssignment.Builder()
					.employeeId(employee.getId())
					.name(employee.getName())
					.title(employee.getPositionName())
					.assigned(assigned)
					.skillStatuses(statuses)
					.build());
		}

		return assignments;
	}

	private List<SkillStatus> initializeSkillStatuses(List<AccountSkillRole> roleSkills) {
		if (CollectionUtils.isEmpty(roleSkills)) {
			return Collections.emptyList();
		}

		List<SkillStatus> skillStatuses = new ArrayList<>();
		for (int index = 0; index < roleSkills.size(); index++) {
			skillStatuses.add(SkillStatus.Expired);
		}

		return skillStatuses;
	}

	private List<SkillStatus> calculateSkillStatuses(List<AccountSkillRole> roleSkills,
	                                                 Set<AccountSkillEmployee> accountSkillEmployees) {
		List<SkillStatus> statuses = new ArrayList<>();
		for (AccountSkillRole accountSkillRole : roleSkills) {
			AccountSkillEmployee accountSkillEmployee = findEmployeeSkill(accountSkillRole.getSkill(), accountSkillEmployees);
			statuses.add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
		}
		return statuses;
	}

	private AccountSkillEmployee findEmployeeSkill(AccountSkill skill, Set<AccountSkillEmployee> accountSkillEmployees) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			if (accountSkillEmployee.getSkill().equals(skill)) {
				accountSkillEmployees.remove(accountSkillEmployee);
				return accountSkillEmployee;
			}
		}

		return null;
	}
}
