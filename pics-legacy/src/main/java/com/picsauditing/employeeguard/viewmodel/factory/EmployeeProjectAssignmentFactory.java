package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectAssignment;

import java.util.*;

public class EmployeeProjectAssignmentFactory {

    public EmployeeProjectAssignment create(final AccountModel accountModel,
                                            final Employee employee,
                                            final List<AccountSkill> roleSkills,
                                            final List<AccountSkill> siteRequiredSkills,
                                            final List<AccountSkill> corporateRequiredSkills) {
        return new EmployeeProjectAssignment.Builder()
                .contractorId(accountModel.getId())
                .contractorName(accountModel.getName())
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .skillStatusRollUp(getStatusRollUp(employee.getSkills(), roleSkills, siteRequiredSkills,
                        corporateRequiredSkills))
                .build();
    }

    public List<EmployeeProjectAssignment> create(final Map<AccountModel, List<Employee>> contractorEmployees,
                                                  final List<AccountSkill> roleSkills,
                                                  final List<AccountSkill> siteRequiredSkills,
                                                  final List<AccountSkill> corporateRequiredSkills) {
        List<EmployeeProjectAssignment> employeeProjectAssignments = new ArrayList<>();
        for (AccountModel accountModel : contractorEmployees.keySet()) {
            for (Employee employee : contractorEmployees.get(accountModel)) {
                employeeProjectAssignments.add(create(accountModel, employee, roleSkills, siteRequiredSkills,
                        corporateRequiredSkills));
            }
        }

        return employeeProjectAssignments;
    }

    private SkillStatus getStatusRollUp(final List<AccountSkillEmployee> employeeSkills,
                                        final List<AccountSkill> roleSkills,
                                        final List<AccountSkill> siteRequiredSkills,
                                        final List<AccountSkill> corporateRequiredSkills) {
        Map<AccountSkill, AccountSkillEmployee> employeeSkillsMap = Utilities.convertToMap(employeeSkills,
                new Utilities.MapConvertable<AccountSkill, AccountSkillEmployee>() {

                    @Override
                    public AccountSkill getKey(AccountSkillEmployee accountSkillEmployee) {
                        return accountSkillEmployee.getSkill();
                    }
                });

        SkillStatus lowestSkillStatus = getLowestSkillStatus(employeeSkillsMap, roleSkills);
        if (lowestSkillStatus == SkillStatus.Expired) {
            return lowestSkillStatus;
        }

        SkillStatus lowestSiteSkillStatus = getLowestSkillStatus(employeeSkillsMap, siteRequiredSkills);
        if (lowestSiteSkillStatus == SkillStatus.Expired) {
            return lowestSiteSkillStatus;
        }

        SkillStatus lowestCorporateSkillStatus = getLowestSkillStatus(employeeSkillsMap, corporateRequiredSkills);

        return worstOf(Arrays.asList(lowestSkillStatus, lowestSiteSkillStatus, lowestCorporateSkillStatus));
    }

    private SkillStatus getLowestSkillStatus(final Map<AccountSkill, AccountSkillEmployee> employeeSkillsMap,
                                             final List<AccountSkill> roleSkills) {
        SkillStatus lowestStatus = SkillStatus.Complete;
        for (AccountSkill skill : roleSkills) {
            if (employeeSkillsMap.containsKey(skill)) {
                SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(employeeSkillsMap.get(skill));
                if (skillStatus == SkillStatus.Expired) {
                    return skillStatus;
                }

                if (skillStatus.compareTo(lowestStatus) < 0) {
                    lowestStatus = skillStatus;
                }
            }
        }

        return lowestStatus;
    }

    private SkillStatus worstOf(final List<SkillStatus> skillStatuses) {
        Collections.sort(skillStatuses);

        return skillStatuses.get(0);
    }
}
