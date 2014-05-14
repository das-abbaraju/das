package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.ContractorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ContractorEmployeeProjectAssignmentFactory {

	// TODO: Find out who is using this
	public List<ContractorEmployeeProjectAssignment> buildList(final List<Employee> accountEmployees,
															   final List<AccountSkillProfile> accountSkillProfiles,
															   final List<AccountSkill> accountSkills,
															   final List<Role> jobRoles) {
		Map<Employee, List<AccountSkillProfile>> employeeMap = buildEmployeeSkillsMap(accountEmployees, accountSkillProfiles);

		List<ContractorEmployeeProjectAssignment> employeeAssignmentInformation = new ArrayList<>();
		Map<Employee, List<Role>> employeeJobRolesMap = buildEmployeeJobRoles(accountEmployees, jobRoles);
		for (Map.Entry<Employee, List<AccountSkillProfile>> employeeMapEntry : employeeMap.entrySet()) {
			List<Group> employeeJobRoles = employeeJobRolesMap.get(employeeMapEntry.getKey());
			employeeAssignmentInformation.add(build(employeeMapEntry.getKey(), employeeMapEntry.getValue(),
					accountSkills, employeeJobRoles));
		}

		return employeeAssignmentInformation;
	}

	private Map<Employee, List<Role>> buildEmployeeJobRoles(List<Employee> accountEmployees, List<Role> jobRoles) {
		Map<Employee, List<Role>> employeeJobRoles = new TreeMap<>();

		for (Employee employee : accountEmployees) {
			for (GroupEmployee groupEmployee : employee.getGroups()) {
				if (jobRoles.contains(groupEmployee.getGroup())) {
					if (!employeeJobRoles.containsKey(employee)) {
						employeeJobRoles.put(employee, new ArrayList<Role>());
					}

					employeeJobRoles.get(employee).add(groupEmployee.getGroup());
				}
			}
		}

		return employeeJobRoles;
	}

	private Map<Employee, List<AccountSkillProfile>> buildEmployeeSkillsMap(final List<Employee> accountEmployees,
																			final List<AccountSkillProfile> accountSkillProfiles) {
		Map<Employee, List<AccountSkillProfile>> employeeSkillMap = new TreeMap<>();

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

	public ContractorEmployeeProjectAssignment build(final Employee employee,
													 final List<AccountSkillProfile> accountSkillProfiles,
													 final List<AccountSkill> accountSkills,
													 final List<Role> jobRoles) {
		ContractorEmployeeProjectAssignment employeeAssignmentInformation = new ContractorEmployeeProjectAssignment();
		employeeAssignmentInformation = addEmployeeInfo(employeeAssignmentInformation, employee);
		employeeAssignmentInformation.setSkillStatuses(buildOrderedSkillStatus(accountSkills, accountSkillProfiles));
		employeeAssignmentInformation.setAssignedGroupIds(EntityHelper.getIdsForEntities(jobRoles));
		return employeeAssignmentInformation;
	}

	private ContractorEmployeeProjectAssignment addEmployeeInfo(ContractorEmployeeProjectAssignment employeeAssignmentInformation, Employee employee) {
		employeeAssignmentInformation.setEmployeeId(employee.getId());
		employeeAssignmentInformation.setName(employee.getName());
		employeeAssignmentInformation.setTitle(employee.getPositionName());
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

	private void insertIntoMap(TreeMap<String, SkillStatus> statusMap, String name, SkillStatus skillStatus) {
		if (statusMap.containsKey(name)) {
			statusMap.put(name, skillStatus);
		}
	}

	public List<ContractorEmployeeProjectAssignment> buildListForRole(final List<Employee> employees,
																	  final List<AccountSkill> requiredSkills,
																	  final List<AccountSkillProfile> accountSkillProfiles,
																	  final List<ProjectRoleEmployee> projectRoleEmployees) {
		List<ContractorEmployeeProjectAssignment> assignments = new ArrayList<>();
		Map<Employee, List<SkillStatus>> employeeSkillStatuses = getEmployeeSkillStatuses(employees, requiredSkills, accountSkillProfiles);

		for (Employee employee : employees) {
			ContractorEmployeeProjectAssignment assignment = new ContractorEmployeeProjectAssignment();
			assignment.setAssigned(employeeIsAssignedToRole(projectRoleEmployees, employee));
			assignment.setEmployeeId(employee.getId());
			assignment.setName(employee.getName());
			assignment.setTitle(employee.getPositionName());
			assignment.setSkillStatuses(employeeSkillStatuses.get(employee));

			assignments.add(assignment);
		}

		return assignments;
	}

	private Map<Employee, List<SkillStatus>> getEmployeeSkillStatuses(final List<Employee> employees,
																	  final List<AccountSkill> requiredSkills,
																	  final List<AccountSkillProfile> accountSkillProfiles) {
		Map<Employee, List<SkillStatus>> employeeSkillStatuses = new TreeMap<>();

		for (Employee employee : employees) {
			employeeSkillStatuses.put(employee, new ArrayList<SkillStatus>());

			for (AccountSkill requiredSkill : requiredSkills) {
				AccountSkillProfile accountSkillProfile = getAccountSkillEmployeeWithDefault(employee, requiredSkill, accountSkillProfiles);
				employeeSkillStatuses.get(employee).add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile));
			}
		}

		return employeeSkillStatuses;
	}

	private AccountSkillProfile getAccountSkillEmployeeWithDefault(final Employee employee,
																   final AccountSkill requiredSkill,
																   final List<AccountSkillProfile> accountSkillProfiles) {
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			if (accountSkillProfile.getSkill().equals(requiredSkill)
					&& accountSkillProfile.getProfile().getEmployees().contains(employee)) {
				return accountSkillProfile;
			}
		}

		return new AccountSkillProfile(requiredSkill, employee.getProfile());
	}

	private boolean employeeIsAssignedToRole(final List<ProjectRoleEmployee> projectRoleEmployees, final Employee employee) {
		for (ProjectRoleEmployee projectRoleEmployee : projectRoleEmployees) {
			if (projectRoleEmployee.getEmployee().equals(employee)) {
				return true;
			}
		}

		return false;
	}
}
