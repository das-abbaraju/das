package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SiteAssignmentModelFactory {

	public SiteAssignmentModel create(final List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels,
									  final Map<RoleInfo, Integer> roleCounts) {
		return new SiteAssignmentModel.Builder()
				.employeeSiteAssignmentModels(employeeSiteAssignmentModels)
				.totalEmployeesAssignedToSite(employeeSiteAssignmentModels.size())
				.roleEmployeeCount(roleCounts)
				.build();
	}

	public SiteAssignmentModel create(final AccountModel site,
									  final List<AccountModel> employeeAccounts,
									  final Set<Employee> employees,
									  final Map<Employee, SkillStatus> employeeSkillStatusMap,
									  final Map<RoleInfo, Integer> roleCounts) {
		Map<Employee, Set<Role>> employeeRoles = getEmployeeRoles(employees, site.getId());

		List<EmployeeSiteAssignmentModel> employeeSiteAssignments =
				buildEmployeeSiteAssignmentsWithEmployeeStatusMap(employeeAccounts, employeeSkillStatusMap, employeeRoles);

		return create(employeeSiteAssignments, roleCounts);
	}

	private Map<Employee, Set<Role>> getEmployeeRoles(Set<Employee> employees, int siteId) {
		Map<Employee, Set<Role>> employeeRoles = new HashMap<>();

		for (Employee employee : employees) {
			for (SiteAssignment siteAssignment : employee.getSiteAssignments()) {
				Role role = siteAssignment.getRole();

				if (siteAssignment.getSiteId() == siteId) {
					PicsCollectionUtil.addToMapOfKeyToSet(employeeRoles, employee, role);
				}
			}

			for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
				Project project = projectRoleEmployee.getProjectRole().getProject();

				if (project.getAccountId() == siteId) {
					PicsCollectionUtil.addToMapOfKeyToSet(employeeRoles, employee, projectRoleEmployee.getProjectRole().getRole());
				}
			}
		}

		return employeeRoles;
	}

	private List<EmployeeSiteAssignmentModel> buildEmployeeSiteAssignmentsWithEmployeeStatusMap(List<AccountModel> employeeAccounts,
																								Map<Employee, SkillStatus> employeeStatus,
																								Map<Employee, Set<Role>> employeeRoles) {
		Map<Integer, AccountModel> accounts = getIdToAccountModel(employeeAccounts);

		return ViewModelFactory.getEmployeeSiteAssignmentModelFactory().create(employeeStatus, employeeRoles, accounts);
	}

	private List<EmployeeSiteAssignmentModel> buildEmployeeSiteAssignments(List<AccountModel> employeeAccounts,
																		   Map<Employee, Set<AccountSkill>> employeeSkills,
																		   Map<Employee, Set<Role>> employeeRoles) {
		Map<Integer, AccountModel> accounts = getIdToAccountModel(employeeAccounts);
		Map<Employee, SkillStatus> statusRollUp = calculateEmployeeSkillStatus(employeeSkills);

		return ViewModelFactory.getEmployeeSiteAssignmentModelFactory().create(statusRollUp, employeeRoles, accounts);
	}

	private Map<Integer, AccountModel> getIdToAccountModel(List<AccountModel> employeeAccounts) {
		return PicsCollectionUtil.convertToMap(employeeAccounts, new PicsCollectionUtil.MapConvertable<Integer, AccountModel>() {
			@Override
			public Integer getKey(AccountModel entity) {
				return entity.getId();
			}
		});
	}

	private Map<Employee, SkillStatus> calculateEmployeeSkillStatus(Map<Employee, Set<AccountSkill>> employeeRequiredSkills) {
		Map<Employee, SkillStatus> employeeStatus = new HashMap<>();

		for (Map.Entry<Employee, Set<AccountSkill>> entry : employeeRequiredSkills.entrySet()) {
			Employee employee = entry.getKey();

			//List<AccountSkillProfile> employeeSkills = filterRequiredEmployeeSkills(employee, entry.getValue());
			employeeStatus.put(employee, calculateWorstStatusOf(employee, entry.getValue()));
		}

		return employeeStatus;
	}

	private List<AccountSkillProfile> filterRequiredEmployeeSkills(final Employee employee, final Set<AccountSkill> requiredSkills) {
		if (employee.getProfile() == null) {
			return Collections.emptyList();
		}

		List<AccountSkillProfile> employeeSkills = new ArrayList<>(employee.getProfile().getSkills());

		CollectionUtils.filter(employeeSkills, new GenericPredicate<AccountSkillProfile>() {
			@Override
			public boolean evaluateEntity(AccountSkillProfile accountSkillProfile) {
				return requiredSkills.contains(accountSkillProfile.getSkill());
			}
		});

		return employeeSkills;
	}

	private Map<Integer, AccountSkillProfile> prepareEmployeeDocumentationsLookup(Employee employee) {
		if (employee.getProfile() == null) {
			return Collections.EMPTY_MAP;
		}

		List<AccountSkillProfile> employeeDocumentations = employee.getProfile().getSkills();
		Map<Integer, AccountSkillProfile> employeeDocumentationLookup = PicsCollectionUtil.convertToMap(employeeDocumentations, new PicsCollectionUtil.MapConvertable<Integer, AccountSkillProfile>() {
			@Override
			public Integer getKey(AccountSkillProfile entity) {
				return entity.getSkill().getId();
			}
		});

		return employeeDocumentationLookup;
	}

	private SkillStatus calculateWorstStatusOf(Employee employee, Set<AccountSkill> employeeRequiredSkills) {

		Map<Integer, AccountSkillProfile> employeeDocumentationLookup = prepareEmployeeDocumentationsLookup(employee);

		SkillStatus worst = SkillStatus.Completed;

		for (AccountSkill employeeReqdSkill : employeeRequiredSkills) {
			int skillId = employeeReqdSkill.getId();
			if (!employeeDocumentationLookup.containsKey(skillId)) {
				return SkillStatus.Expired;
			}

			AccountSkillProfile accountSkillProfile = employeeDocumentationLookup.get(skillId);
			SkillStatus current = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);
			if (current == SkillStatus.Expired) {
				return current;
			}

			if (current.ordinal() < worst.ordinal()) {
				worst = current;
			}

		}

		return worst;
	}


}
