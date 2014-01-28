package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectAssignment;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class EmployeeProjectAssignmentFactory {

    public EmployeeProjectAssignment create(final AccountModel accountModel,
                                            final Employee employee,
                                            final List<AccountSkill> roleSkills,
                                            final List<AccountSkill> projectSkills,
                                            final List<AccountSkill> siteRequiredSkills,
                                            final List<AccountSkill> corporateRequiredSkills) {
        return new EmployeeProjectAssignment.Builder()
                .contractorId(accountModel.getId())
                .contractorName(accountModel.getName())
                .employeeId(employee.getId())
                .employeeName(employee.getName())
                .skillStatusRollUp(getStatusRollUp(employee.getSkills(), roleSkills, projectSkills, siteRequiredSkills,
                        corporateRequiredSkills))
                .build();
    }

    public List<EmployeeProjectAssignment> create(final Map<AccountModel, Set<Employee>> contractorEmployees,
                                                  final Map<Employee, Set<AccountGroup>> employeeRoleAssignments,
                                                  final Map<AccountGroup, Set<AccountSkill>> projectRoleSkills,
                                                  final List<AccountSkill> projectSkills,
                                                  final List<AccountSkill> siteRequiredSkills,
                                                  final List<AccountSkill> corporateRequiredSkills) {
        List<EmployeeProjectAssignment> employeeProjectAssignments = new ArrayList<>();
        for (AccountModel accountModel : contractorEmployees.keySet()) {
            for (Employee employee : contractorEmployees.get(accountModel)) {
                if (employeeRoleAssignments.containsKey(employee)) {
                    employeeProjectAssignments.add(create(accountModel,
                            employee,
                            getAllEmployeeProjectRoleSkills(employeeRoleAssignments.get(employee), projectRoleSkills),
                        projectSkills,
                            siteRequiredSkills,
                            corporateRequiredSkills));
                }
            }
        }

        return employeeProjectAssignments;
    }

    private List<AccountSkill> getAllEmployeeProjectRoleSkills(final Set<AccountGroup> employeeRoleAssignments,
                                                               final Map<AccountGroup, Set<AccountSkill>> projectRoleSkills) {
        if (CollectionUtils.isEmpty(employeeRoleAssignments)) {
            return Collections.emptyList();
        }

        List<AccountSkill> skills = new ArrayList<>();
        for (AccountGroup role : employeeRoleAssignments) {
            skills.addAll(projectRoleSkills.get(role));
        }

        return skills;
    }

    private SkillStatus getStatusRollUp(final List<AccountSkillEmployee> employeeSkills,
                                        final List<AccountSkill> roleSkills,
                                        final List<AccountSkill> projectSkills,
                                        final List<AccountSkill> siteRequiredSkills,
                                        final List<AccountSkill> corporateRequiredSkills) {
        Map<AccountSkill, AccountSkillEmployee> employeeSkillsMap = Utilities.convertToMap(employeeSkills,
                new Utilities.MapConvertable<AccountSkill, AccountSkillEmployee>() {

                    @Override
                    public AccountSkill getKey(AccountSkillEmployee accountSkillEmployee) {
                        return accountSkillEmployee.getSkill();
                    }
                });

        SkillStatus lowestRoleSkillStatus = getLowestSkillStatus(employeeSkillsMap, roleSkills);
        if (lowestRoleSkillStatus == SkillStatus.Expired) {
            return lowestRoleSkillStatus;
        }

        SkillStatus lowestProjectSkillStatus = getLowestSkillStatus(employeeSkillsMap, projectSkills);
        if (lowestRoleSkillStatus == SkillStatus.Expired) {
            return lowestRoleSkillStatus;
        }

        SkillStatus lowestSiteSkillStatus = getLowestSkillStatus(employeeSkillsMap, siteRequiredSkills);
        if (lowestSiteSkillStatus == SkillStatus.Expired) {
            return lowestSiteSkillStatus;
        }

        SkillStatus lowestCorporateSkillStatus = getLowestSkillStatus(employeeSkillsMap, corporateRequiredSkills);

        return worstOf(Arrays.asList(lowestRoleSkillStatus, lowestProjectSkillStatus, lowestSiteSkillStatus,
                lowestCorporateSkillStatus));
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
