package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.forms.contractor.ContractorEmployeeRoleAssignment;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;

import java.util.Arrays;

public class ContractorEmployeeRoleAssignmentFactory {

    public ContractorEmployeeRoleAssignment build(Employee employee, AccountSkillEmployee accountSkillEmployee) {
        ContractorEmployeeRoleAssignment contractorEmployeeRoleAssignment = new ContractorEmployeeRoleAssignment();
        contractorEmployeeRoleAssignment.setEmployeeId(employee.getId());
        contractorEmployeeRoleAssignment.setName(employee.getName());
        contractorEmployeeRoleAssignment.setTitle(employee.getPositionName());
        contractorEmployeeRoleAssignment.setSkillStatuses(Arrays.asList(SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee)));
        return contractorEmployeeRoleAssignment;
    }
}
