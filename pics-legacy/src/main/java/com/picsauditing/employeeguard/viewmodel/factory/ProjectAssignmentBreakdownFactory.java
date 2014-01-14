package com.picsauditing.employeeguard.viewmodel.factory;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectAssignmentBreakdown;

import java.util.*;

public class ProjectAssignmentBreakdownFactory {

    public ProjectAssignmentBreakdown create(List<ProjectRoleEmployee> projectRoleEmployees,
                                             List<AccountSkillEmployee> accountSkillEmployees) {
        Map<Employee, Set<AccountSkill>> employeeSkills = getEmployeeSkill(projectRoleEmployees);
        Table<Employee, AccountSkill, AccountSkillEmployee> table = buildTable(accountSkillEmployees);

        Map<SkillStatus, Integer> results = new HashMap<>();
        for (Map.Entry<Employee, Set<AccountSkill>> employeeSkill : employeeSkills.entrySet()) {
            appendResults(results, employeeSkill.getKey(), employeeSkill.getValue(), table);
        }

        return new ProjectAssignmentBreakdown(results);
    }

    private void appendResults(Map<SkillStatus, Integer> results, Employee employee, Set<AccountSkill> accountSkills,
                               Table<Employee, AccountSkill, AccountSkillEmployee> table) {
        SkillStatus skillStatus = getWorstStatus(employee, accountSkills, table);
        if (!results.containsKey(skillStatus)) {
            results.put(skillStatus, 0);
        }

        results.put(skillStatus, results.get(skillStatus) + 1);

    }

    private SkillStatus getWorstStatus(Employee employee, Set<AccountSkill> accountSkills, Table<Employee, AccountSkill, AccountSkillEmployee> table) {
        SkillStatus worst = SkillStatus.Complete;
        for (AccountSkill accountSkill : accountSkills) {
            AccountSkillEmployee accountSkillEmployee = table.get(employee, accountSkill);
            SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);

            if (skillStatus.compareTo(worst) < 0) {
                worst = skillStatus;
            }
        }

        return worst;
    }

    private Table<Employee, AccountSkill, AccountSkillEmployee> buildTable(List<AccountSkillEmployee> accountSkillEmployees) {
        Table<Employee, AccountSkill, AccountSkillEmployee> table = TreeBasedTable.create();
        for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
            table.put(accountSkillEmployee.getEmployee(), accountSkillEmployee.getSkill(), accountSkillEmployee);
        }

        return table;
    }

    private Map<Employee, Set<AccountSkill>> getEmployeeSkill(List<ProjectRoleEmployee> projectRoleEmployees) {
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
