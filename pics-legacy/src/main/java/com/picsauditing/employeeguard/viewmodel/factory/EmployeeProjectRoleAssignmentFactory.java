package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
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
		Map<AccountSkill, AccountSkillProfile> employeeSkillMap = buildEmployeeAccountSkillMap(employee.getProfile().getSkills());

		return new EmployeeProjectRoleAssignment.Builder()
				.contractorId(accountModel.getId())
				.contractorName(accountModel.getName())
				.employeeId(employee.getId())
				.employeeName(employee.getName())
				.skillStatuses(getRoleSkillStatuses(employeeSkillMap, orderedSkills))
				.build();
	}

	private Map<AccountSkill, AccountSkillProfile> buildEmployeeAccountSkillMap(List<AccountSkillProfile> profileSkills) {
		return PicsCollectionUtil.convertToMap(profileSkills,
				new PicsCollectionUtil.MapConvertable<AccountSkill, AccountSkillProfile>() {

					@Override
					public AccountSkill getKey(AccountSkillProfile accountSkillProfile) {
						return accountSkillProfile.getSkill();
					}
				});
	}

	private List<SkillStatus> getRoleSkillStatuses(final Map<AccountSkill, AccountSkillProfile> profileSkillMap,
												   final List<AccountSkill> orderedSkills) {
		List<SkillStatus> skillStatuses = new ArrayList<>();
		for (AccountSkill skill : orderedSkills) {
			AccountSkillProfile accountSkillProfile = profileSkillMap.get(skill);
			if (accountSkillProfile == null) {
				skillStatuses.add(SkillStatus.Expired);
			} else {
				skillStatuses.add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile));
			}
		}

		return skillStatuses;
	}
}
