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

	public List<ContractorEmployeeProjectAssignment> buildList(List<Employee> accountEmployees, List<AccountSkillEmployee> accountSkillEmployees, List<AccountSkill> accountSkills, List<AccountGroup> jobRoles) {
		Map<Employee, List<AccountSkillEmployee>> employeeMap = buildEmployeeSkillsMap(accountEmployees, accountSkillEmployees);

		List<ContractorEmployeeProjectAssignment> employeeAssignmentInformation = new ArrayList<>();
		Map<Employee, List<AccountGroup>> employeeJobRolesMap = buildEmployeeJobRoles(accountEmployees, jobRoles);
		for (Map.Entry<Employee, List<AccountSkillEmployee>> employeeMapEntry : employeeMap.entrySet()) {
			List<AccountGroup> employeeJobRoles = employeeJobRolesMap.get(employeeMapEntry.getKey());
			employeeAssignmentInformation.add(build(employeeMapEntry.getKey(), employeeMapEntry.getValue(), accountSkills, employeeJobRoles));
		}

		return employeeAssignmentInformation;
	}

	private Map<Employee, List<AccountGroup>> buildEmployeeJobRoles(List<Employee> accountEmployees, List<AccountGroup> jobRoles) {
		Map<Employee, List<AccountGroup>> employeeJobRoles = new TreeMap<>();

		for (Employee employee : accountEmployees) {
			for (AccountGroupEmployee accountGroupEmployee : employee.getGroups()) {
				if (jobRoles.contains(accountGroupEmployee.getGroup())) {
					if (!employeeJobRoles.containsKey(employee)) {
						employeeJobRoles.put(employee, new ArrayList<AccountGroup>());
					}

					employeeJobRoles.get(employee).add(accountGroupEmployee.getGroup());
				}
			}
		}

		return employeeJobRoles;
	}

	private Map<Employee, List<AccountSkillEmployee>> buildEmployeeSkillsMap(List<Employee> accountEmployees, List<AccountSkillEmployee> accountSkillEmployees) {
		Map<Employee, List<AccountSkillEmployee>> employeeSkillMap = new TreeMap<>();

		for (Employee employee : accountEmployees) {
			employeeSkillMap.put(employee, new ArrayList<AccountSkillEmployee>());

			for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
				if (employee.equals(accountSkillEmployee.getEmployee())) {
					if (employeeSkillMap.containsKey(employee)) {
						employeeSkillMap.get(employee).add(accountSkillEmployee);
					}
				}
			}
		}

		return employeeSkillMap;
	}

	public ContractorEmployeeProjectAssignment build(Employee employee, List<AccountSkillEmployee> accountSkillEmployees, List<AccountSkill> accountSkills, List<AccountGroup> jobRoles) {
		ContractorEmployeeProjectAssignment employeeAssignmentInformation = new ContractorEmployeeProjectAssignment();
		employeeAssignmentInformation = addEmployeeInfo(employeeAssignmentInformation, employee);
		employeeAssignmentInformation.setSkillStatuses(buildOrderedSkillStatus(accountSkills, accountSkillEmployees));
		employeeAssignmentInformation.setAssignedGroupIds(EntityHelper.getIdsForEntities(jobRoles));
		return employeeAssignmentInformation;
	}

	private ContractorEmployeeProjectAssignment addEmployeeInfo(ContractorEmployeeProjectAssignment employeeAssignmentInformation, Employee employee) {
		employeeAssignmentInformation.setEmployeeId(employee.getId());
		employeeAssignmentInformation.setName(employee.getName());
		employeeAssignmentInformation.setTitle(employee.getPositionName());
		return employeeAssignmentInformation;
	}

	private List<SkillStatus> buildOrderedSkillStatus(List<AccountSkill> accountSkills, List<AccountSkillEmployee> accountSkillEmployees) {
		TreeMap<String, SkillStatus> statusMap = fillMapWithKeys(accountSkills);
		statusMap = buildSkillStatusMap(statusMap, accountSkillEmployees);
		return new ArrayList<>(statusMap.values());
	}

	private TreeMap<String, SkillStatus> fillMapWithKeys(List<AccountSkill> accountSkills) {
		TreeMap<String, SkillStatus> statusMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (AccountSkill accountSkill : accountSkills) {
			statusMap.put(accountSkill.getName(), SkillStatus.Expired);
		}

		return statusMap;
	}

	private TreeMap<String, SkillStatus> buildSkillStatusMap(TreeMap<String, SkillStatus> statusMap, List<AccountSkillEmployee> accountSkillEmployees) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);
			insertIntoMap(statusMap, accountSkillEmployee.getSkill().getName(), skillStatus);
		}

		return statusMap;
	}

	private void insertIntoMap(TreeMap<String, SkillStatus> statusMap, String name, SkillStatus skillStatus) {
		if (statusMap.containsKey(name)) {
			statusMap.put(name, skillStatus);
		}
	}

	public List<ContractorEmployeeProjectAssignment> buildListForRole(final List<Employee> employees, final List<AccountSkill> requiredSkills, final List<AccountSkillEmployee> accountSkillEmployees, final List<ProjectRoleEmployee> projectRoleEmployees) {
		List<ContractorEmployeeProjectAssignment> assignments = new ArrayList<>();
		Map<Employee, List<SkillStatus>> employeeSkillStatuses = getEmployeeSkillStatuses(employees, requiredSkills, accountSkillEmployees);

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

	private Map<Employee, List<SkillStatus>> getEmployeeSkillStatuses(final List<Employee> employees, final List<AccountSkill> requiredSkills, final List<AccountSkillEmployee> accountSkillEmployees) {
		Map<Employee, List<SkillStatus>> employeeSkillStatuses = new TreeMap<>();

		for (Employee employee : employees) {
			employeeSkillStatuses.put(employee, new ArrayList<SkillStatus>());

			for (AccountSkill requiredSkill : requiredSkills) {
				AccountSkillEmployee accountSkillEmployee = getAccountSkillEmployeeWithDefault(employee, requiredSkill, accountSkillEmployees);
				employeeSkillStatuses.get(employee).add(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
			}
		}

		return employeeSkillStatuses;
	}

	private AccountSkillEmployee getAccountSkillEmployeeWithDefault(Employee employee, AccountSkill requiredSkill, List<AccountSkillEmployee> accountSkillEmployees) {
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			if (accountSkillEmployee.getSkill().equals(requiredSkill) && accountSkillEmployee.getEmployee().equals(employee)) {
				return accountSkillEmployee;
			}
		}

		return new AccountSkillEmployee(requiredSkill, employee);
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
