package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.operator.OperatorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.*;

public class OperatorEmployeeProjectAssignmentFactory {
//	public List<OperatorEmployeeProjectAssignment> buildList(List<Employee> accountEmployees, List<AccountSkillEmployee> accountSkillEmployees, List<AccountSkill> accountSkills, Map<Integer, AccountModel> accountModels, List<Group> jobRoles) {
//		Map<Employee, List<AccountSkillEmployee>> employeeMap = buildEmployeeSkillsMap(accountEmployees, accountSkillEmployees);
//
//		List<OperatorEmployeeProjectAssignment> employeeAssignmentInformation = new ArrayList<>();
//		Map<Employee, List<Group>> employeeJobRoles = buildEmployeeJobRoles(accountEmployees, jobRoles);
//		for (Map.Entry<Employee, List<AccountSkillEmployee>> employeeMapEntry : employeeMap.entrySet()) {
//			OperatorEmployeeProjectAssignment operatorEmployeeProjectAssignment = build(employeeMapEntry.getKey(), employeeMapEntry.getValue(), accountSkills, accountModels);
//			operatorEmployeeProjectAssignment.setAssignedRoleIds(EntityHelper.getIdsForEntities(employeeJobRoles.get(employeeMapEntry.getKey())));
//			employeeAssignmentInformation.add(operatorEmployeeProjectAssignment);
//		}
//
//		return employeeAssignmentInformation;
//	}

    public List<OperatorEmployeeProjectAssignment> buildList(List<Employee> accountEmployees, List<AccountSkillEmployee> accountSkillEmployees, List<AccountSkill> accountSkills, Map<Integer, AccountModel> accountModels, List<Role> jobRoles) {
        Map<Employee, List<AccountSkillEmployee>> employeeMap = buildEmployeeSkillsMap(accountEmployees, accountSkillEmployees);

        List<OperatorEmployeeProjectAssignment> employeeAssignmentInformation = new ArrayList<>();
        Map<Employee, List<Role>> employeeJobRoles = buildEmployeeJobRoles(accountEmployees, jobRoles);
        for (Map.Entry<Employee, List<AccountSkillEmployee>> employeeMapEntry : employeeMap.entrySet()) {
            OperatorEmployeeProjectAssignment operatorEmployeeProjectAssignment = build(employeeMapEntry.getKey(), employeeMapEntry.getValue(), accountSkills, accountModels);
            operatorEmployeeProjectAssignment.setAssignedRoleIds(EntityHelper.getIdsForEntities(employeeJobRoles.get(employeeMapEntry.getKey())));
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

	private Map<Employee, List<AccountSkillEmployee>> buildEmployeeSkillsMap(List<Employee> accountEmployees, List<AccountSkillEmployee> accountSkillEmployees) {
		Map<Employee, List<AccountSkillEmployee>> employeeSkillMap = new HashMap<>();

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

	public OperatorEmployeeProjectAssignment build(Employee employee, List<AccountSkillEmployee> accountSkillEmployees, List<AccountSkill> accountSkills, Map<Integer, AccountModel> accountModels) {
		OperatorEmployeeProjectAssignment employeeAssignmentInformation = new OperatorEmployeeProjectAssignment();
		employeeAssignmentInformation = addEmployeeInfo(employeeAssignmentInformation, employee, accountModels);
		employeeAssignmentInformation.setSkillStatuses(buildOrderedSkillStatus(accountSkills, accountSkillEmployees));
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
}
