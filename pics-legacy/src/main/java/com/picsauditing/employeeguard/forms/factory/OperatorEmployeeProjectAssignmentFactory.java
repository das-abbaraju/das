package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.operator.OperatorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;

import java.util.*;

public class OperatorEmployeeProjectAssignmentFactory {
	public List<OperatorEmployeeProjectAssignment> buildList(final List<Employee> accountEmployees,
															 final List<AccountSkillProfile> accountSkillProfiles,
															 final List<AccountSkill> accountSkills,
															 final Map<Integer, AccountModel> accountModels,
															 final List<Role> jobRoles) {
		Map<Employee, List<AccountSkillProfile>> employeeMap =
				buildEmployeeSkillsMap(accountEmployees, accountSkillProfiles);

		List<OperatorEmployeeProjectAssignment> employeeAssignmentInformation = new ArrayList<>();
		Map<Employee, List<Role>> employeeJobRoles = buildEmployeeJobRoles(accountEmployees, jobRoles);
		for (Map.Entry<Employee, List<AccountSkillProfile>> employeeMapEntry : employeeMap.entrySet()) {
			OperatorEmployeeProjectAssignment operatorEmployeeProjectAssignment = build(employeeMapEntry.getKey(),
					employeeMapEntry.getValue(), accountSkills, accountModels);

			operatorEmployeeProjectAssignment
					.setAssignedRoleIds(EntityHelper.getIdsForEntities(employeeJobRoles.get(employeeMapEntry.getKey())));

			employeeAssignmentInformation.add(operatorEmployeeProjectAssignment);
		}

		return employeeAssignmentInformation;
	}

	private Map<Employee, List<Role>> buildEmployeeJobRoles(List<Employee> accountEmployees, List<Role> jobRoles) {
		Map<Employee, List<Role>> employeeJobRoles = new TreeMap<>();

		for (Employee employee : accountEmployees) {
			for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
				Role role = projectRoleEmployee.getProjectRole().getRole();
				if (jobRoles.contains(role)) {
					if (!employeeJobRoles.containsKey(employee)) {
						employeeJobRoles.put(employee, new ArrayList<Role>());
					}

					employeeJobRoles.get(employee).add(role);
				}
			}
		}

		return employeeJobRoles;
	}

	private Map<Employee, List<AccountSkillProfile>> buildEmployeeSkillsMap(final List<Employee> accountEmployees,
																			final List<AccountSkillProfile> accountSkillProfiles) {
		Map<Employee, List<AccountSkillProfile>> employeeSkillMap = new HashMap<>();

		for (Employee employee : accountEmployees) {
			employeeSkillMap.put(employee, new ArrayList<AccountSkillProfile>());

			for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
				if (accountSkillProfile.getProfile().getEmployees().contains(employee)) {
					if (employeeSkillMap.containsKey(employee)) {
						employeeSkillMap.get(employee).add(accountSkillProfile);
					}
				}
			}
		}

		return employeeSkillMap;
	}

	public OperatorEmployeeProjectAssignment build(final Employee employee,
												   final List<AccountSkillProfile> accountSkillProfiles,
												   final List<AccountSkill> accountSkills,
												   final Map<Integer, AccountModel> accountModels) {
		OperatorEmployeeProjectAssignment employeeAssignmentInformation = new OperatorEmployeeProjectAssignment();
		employeeAssignmentInformation = addEmployeeInfo(employeeAssignmentInformation, employee, accountModels);
		employeeAssignmentInformation.setSkillStatuses(buildOrderedSkillStatus(accountSkills, accountSkillProfiles));
		return employeeAssignmentInformation;
	}

	private OperatorEmployeeProjectAssignment addEmployeeInfo(OperatorEmployeeProjectAssignment employeeAssignmentInformation, Employee employee, Map<Integer, AccountModel> accountModels) {
		employeeAssignmentInformation.setEmployeeId(employee.getId());
		employeeAssignmentInformation.setEmployeeName(employee.getName());
		employeeAssignmentInformation.setTitle(employee.getPositionName());
		employeeAssignmentInformation.setCompanyId(employee.getAccountId());
		employeeAssignmentInformation.setCompanyName(accountModels.get(employee.getAccountId()).getName());

		return employeeAssignmentInformation;
	}

	private List<SkillStatus> buildOrderedSkillStatus(final List<AccountSkill> accountSkills,
													  final List<AccountSkillProfile> accountSkillProfiles) {
		TreeMap<String, SkillStatus> statusMap = fillMapWithKeys(accountSkills);
		statusMap = buildSkillStatusMap(statusMap, accountSkillProfiles);
		return new ArrayList<>(statusMap.values());
	}

	private TreeMap<String, SkillStatus> fillMapWithKeys(List<AccountSkill> accountSkills) {
		TreeMap<String, SkillStatus> statusMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (AccountSkill accountSkill : accountSkills) {
			statusMap.put(accountSkill.getName(), SkillStatus.Expired);
		}

		return statusMap;
	}

	private TreeMap<String, SkillStatus> buildSkillStatusMap(final TreeMap<String, SkillStatus> statusMap,
															 final List<AccountSkillProfile> accountSkillProfiles) {
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);
			insertIntoMap(statusMap, accountSkillProfile.getSkill().getName(), skillStatus);
		}

		return statusMap;
	}

	private void insertIntoMap(final TreeMap<String, SkillStatus> statusMap,
							   final String name,
							   final SkillStatus skillStatus) {
		if (statusMap.containsKey(name)) {
			statusMap.put(name, skillStatus);
		}
	}
}
