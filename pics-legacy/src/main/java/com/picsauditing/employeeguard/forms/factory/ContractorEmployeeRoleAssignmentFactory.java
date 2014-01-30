package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ContractorEmployeeRoleAssignmentFactory {

    public ContractorEmployeeRoleAssignment build(Employee employee, AccountSkillEmployee accountSkillEmployee) {
        return new ContractorEmployeeRoleAssignment.Builder()
                .employeeId(employee.getId())
                .name(employee.getName())
                .title(employee.getPositionName())
                .skillStatuses(Arrays.asList(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee)))
                .build();
    }

	public ContractorEmployeeRoleAssignment build(Map<Employee, Set<AccountSkillEmployee>> employeesAndSkills) {

	}
}
