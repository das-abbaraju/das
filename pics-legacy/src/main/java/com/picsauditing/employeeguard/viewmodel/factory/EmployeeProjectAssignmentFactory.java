package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
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
		List<AccountSkillProfile> accountSkillProfiles = new ArrayList<>();
		if (employee.getProfile() != null && CollectionUtils.isNotEmpty(employee.getProfile().getSkills())) {
			accountSkillProfiles = employee.getProfile().getSkills();
		}

		return new EmployeeProjectAssignment.Builder()
				.contractorId(accountModel.getId())
				.contractorName(accountModel.getName())
				.employeeId(employee.getId())
				.employeeName(employee.getName())
				.skillStatusRollUp(getStatusRollUp(accountSkillProfiles, roleSkills, projectSkills, siteRequiredSkills,
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
		for (Role role : employeeRoleAssignments) {
			if (projectRoleSkills.containsKey(role)) {
				skills.addAll(projectRoleSkills.get(role));
			}
		}

		return skills;
	}

	private SkillStatus getStatusRollUp(final List<AccountSkillProfile> profileSkills,
										final List<AccountSkill> roleSkills,
										final List<AccountSkill> projectSkills,
										final List<AccountSkill> siteRequiredSkills,
										final List<AccountSkill> corporateRequiredSkills) {
		Map<AccountSkill, AccountSkillProfile> employeeSkillsMap = PicsCollectionUtil.convertToMap(profileSkills,
				new PicsCollectionUtil.MapConvertable<AccountSkill, AccountSkillProfile>() {

					@Override
					public AccountSkill getKey(AccountSkillProfile accountSkillProfile) {
						return accountSkillProfile.getSkill();
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

	private SkillStatus getLowestSkillStatus(final Map<AccountSkill, AccountSkillProfile> profileSkillsMap,
											 final List<AccountSkill> roleSkills) {
		SkillStatus lowestStatus = SkillStatus.Completed;
		for (AccountSkill skill : roleSkills) {
			if (profileSkillsMap.containsKey(skill)) {
				SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(profileSkillsMap.get(skill));
				if (skillStatus == SkillStatus.Expired) {
					return skillStatus;
				}

				if (skillStatus.compareTo(lowestStatus) < 0) {
					lowestStatus = skillStatus;
				}
			} else {
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
