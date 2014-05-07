package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectAssignment;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class EmployeeProjectAssignmentFactory {

    private EmployeeProjectAssignment create(final AccountModel accountModel,
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
                                                  final Map<Employee, Set<Role>> employeeRoleAssignments,
                                                  final Map<Role, Set<AccountSkill>> projectRoleSkills,
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

    private List<AccountSkill> getAllEmployeeProjectRoleSkills(final Set<Role> employeeRoleAssignments,
                                                               final Map<Role, Set<AccountSkill>> projectRoleSkills) {
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
        Map<AccountSkill, AccountSkillEmployee> employeeSkillsMap = PicsCollectionUtil.convertToMap(employeeSkills,
				new PicsCollectionUtil.MapConvertable<AccountSkill, AccountSkillEmployee>() {

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
      SkillStatus lowestStatus = SkillStatus.Completed;
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
        else{
          //-- If there's no documentation for this skill, then we are at the highest status of "expired".
          return SkillStatus.Expired;
        }
      }

      return lowestStatus;
    }

    private SkillStatus worstOf(final List<SkillStatus> skillStatuses) {
        Collections.sort(skillStatuses);

        return skillStatuses.get(0);
    }
}
