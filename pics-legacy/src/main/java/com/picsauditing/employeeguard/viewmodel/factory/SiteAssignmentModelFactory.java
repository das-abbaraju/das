package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.SkillUsage;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SiteAssignmentModelFactory {

	public SiteAssignmentModel create(List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels, Map<Role, List<Employee>> roleEmployees) {
		Map<String, Integer> roleEmployeeCount = new TreeMap<>();

		for (Map.Entry<Role, List<Employee>> entry : roleEmployees.entrySet()) {
			roleEmployeeCount.put(entry.getKey().getName(), entry.getValue().size());
		}

		return new SiteAssignmentModel.Builder()
				.employeeSiteAssignmentModels(employeeSiteAssignmentModels)
				.totalEmployeesAssignedToSite(employeeSiteAssignmentModels.size())
				.roleEmployeeCount(roleEmployeeCount)
				.build();
	}

	public SiteAssignmentModel create(final AccountModel site, final List<AccountModel> employeeAccounts, final List<SkillUsage> skillUsages) {
		List<Employee> employees = getEmployees(skillUsages);
		Map<Employee, Set<AccountSkill>> employeeSkills = getEmployeeSkills(skillUsages);
		Map<Employee, List<Role>> employeeRoles = getEmployeeRoles(employees, site.getId());

		List<EmployeeSiteAssignmentModel> employeeSiteAssignments = buildEmployeeSiteAssignments(employeeAccounts, employeeSkills, employeeRoles);

		Map<Role, List<Employee>> rolesToEmployees = Utilities.invertMap(employeeRoles);

		return create(employeeSiteAssignments, rolesToEmployees);
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkills(List<SkillUsage> skillUsages) {
		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		for (SkillUsage skillUsage : skillUsages) {
			Employee employee = skillUsage.getEmployee();

			Utilities.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getSiteAssignmentSkills().keySet());
			Utilities.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getProjectJobRoleSkills().keySet());
			Utilities.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getSiteRequiredSkills().keySet());
			Utilities.addAllToMapOfKeyToSet(employeeSkills, employee, skillUsage.getCorporateRequiredSkills().keySet());
		}
		return employeeSkills;
	}

	private List<Employee> getEmployees(List<SkillUsage> skillUsages) {
		return ExtractorUtil.extractList(skillUsages, new Extractor<SkillUsage, Employee>() {
			@Override
			public Employee extract(SkillUsage skillUsage) {
				return skillUsage.getEmployee();
			}
		});
	}

	private Map<Employee, List<Role>> getEmployeeRoles(List<Employee> employees, int siteId) {
		Map<Employee, List<Role>> employeeRoles = new HashMap<>();

		for (Employee employee : employees) {
			for (RoleEmployee roleEmployee : employee.getRoles()) {
				Role role = roleEmployee.getRole();

				if (role.getAccountId() == siteId) {
					Utilities.addToMapOfKeyToList(employeeRoles, employee, role);
				}
			}

			for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
				Project project = projectRoleEmployee.getProjectRole().getProject();

				if (project.getAccountId() == siteId) {
					Utilities.addToMapOfKeyToList(employeeRoles, employee, projectRoleEmployee.getProjectRole().getRole());
				}
			}
		}

		return employeeRoles;
	}

	private List<EmployeeSiteAssignmentModel> buildEmployeeSiteAssignments(List<AccountModel> employeeAccounts,
	                                                                       Map<Employee, Set<AccountSkill>> employeeSkills,
	                                                                       Map<Employee, List<Role>> employeeRoles) {
		Map<Integer, AccountModel> accounts = getIdToAccountModel(employeeAccounts);
		Map<Employee, SkillStatus> statusRollUp = calculateEmployeeSkillStatus(employeeSkills);

		return ViewModeFactory.getEmployeeSiteAssignmentModelFactory().create(statusRollUp, employeeRoles, accounts);
	}

	private Map<Integer, AccountModel> getIdToAccountModel(List<AccountModel> employeeAccounts) {
		return Utilities.convertToMap(employeeAccounts, new Utilities.MapConvertable<Integer, AccountModel>() {
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

			List<AccountSkillEmployee> employeeSkills = filterRequiredEmployeeSkills(employee, entry.getValue());
			employeeStatus.put(employee, calculateWorstStatusOf(employeeSkills));
		}

		return employeeStatus;
	}

	private List<AccountSkillEmployee> filterRequiredEmployeeSkills(final Employee employee, final Set<AccountSkill> requiredSkills) {
		List<AccountSkillEmployee> employeeSkills = new ArrayList<>(employee.getSkills());

		CollectionUtils.filter(employeeSkills, new GenericPredicate<AccountSkillEmployee>() {
			@Override
			public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
				return requiredSkills.contains(accountSkillEmployee.getSkill());
			}
		});

		return employeeSkills;
	}

	private SkillStatus calculateWorstStatusOf(List<AccountSkillEmployee> employeeSkills) {
		SkillStatus worst = SkillStatus.Complete;

		for (AccountSkillEmployee employeeSkill : employeeSkills) {
			SkillStatus current = SkillStatusCalculator.calculateStatusFromSkill(employeeSkill);

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
