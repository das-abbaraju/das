package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectRoleAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmployeeProjectRoleAssignmentFactory {

	public List<EmployeeProjectRoleAssignment> create(final Map<AccountModel, Set<Employee>> contractorEmployeeMap,
													  final List<AccountSkill> orderedSkills) {
		List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments = new ArrayList<>();
		for (AccountModel accountModel : contractorEmployeeMap.keySet()) {
			for (Employee employee : contractorEmployeeMap.get(accountModel)) {
				employeeProjectRoleAssignments.add(buildEmployeeProjectRoleAssignment(accountModel, employee,
						orderedSkills));
			}
		}

		return employeeProjectRoleAssignments;
	}

	public EmployeeProjectRoleAssignment create(final AccountModel accountModel,
												final Employee employee,
												final List<AccountSkill> orderedSkills) {
		return buildEmployeeProjectRoleAssignment(accountModel, employee, orderedSkills);
	}

	private EmployeeProjectRoleAssignment buildEmployeeProjectRoleAssignment(final AccountModel accountModel,
																			 final Employee employee,
																			 final List<AccountSkill> orderedSkills) {
		Map<AccountSkill, AccountSkillEmployee> employeeSkillMap = buildEmployeeAccountSkillMap(employee.getSkills());

		return new EmployeeProjectRoleAssignment.Builder()
				.contractorId(accountModel.getId())
				.contractorName(accountModel.getName())
				.employeeId(employee.getId())
				.employeeName(employee.getName())
				.skillStatuses(getRoleSkillStatuses(employeeSkillMap, orderedSkills))
				.build();
	}

	private Map<AccountSkill, AccountSkillEmployee> buildEmployeeAccountSkillMap(List<AccountSkillEmployee> employeeSkills) {
		return PicsCollectionUtil.convertToMap(employeeSkills,
				new PicsCollectionUtil.MapConvertable<AccountSkill, AccountSkillEmployee>() {

					@Override
					public AccountSkill getKey(AccountSkillEmployee accountSkillEmployee) {
						return accountSkillEmployee.getSkill();
					}
				});
	}

	private List<SkillStatus> getRoleSkillStatuses(final Map<AccountSkill, AccountSkillEmployee> employeeSkillMap,
												   final List<AccountSkill> orderedSkills) {
		List<SkillStatus> skillStatuses = new ArrayList<>();
		for (AccountSkill skill : orderedSkills) {
			AccountSkillEmployee accountSkillEmployee = employeeSkillMap.get(skill);
			if (accountSkillEmployee == null) {
				skillStatuses.add(SkillStatus.Expired);
			} else {
				skillStatuses.add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
			}
		}

		return skillStatuses;
	}
}
