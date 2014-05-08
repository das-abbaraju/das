package com.picsauditing.employeeguard.viewmodel.factory;

import com.google.common.collect.Table;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;

import java.util.*;

public class EmployeeSiteAssignmentModelFactory {

	public List<EmployeeSiteAssignmentModel> create(final Map<Employee, SkillStatus> employeeStatuses,
	                                                final Map<Employee, Set<Role>> roleAssignments,
	                                                final Map<Integer, AccountModel> accounts) {
		List<EmployeeSiteAssignmentModel> models = new ArrayList<>();
		for (Map.Entry<Employee, SkillStatus> entry : employeeStatuses.entrySet()) {
			Employee employee = entry.getKey();
			int assignments = roleAssignments.containsKey(employee) ? roleAssignments.get(employee).size() : 0;

			EmployeeSiteAssignmentModel model = new EmployeeSiteAssignmentModel.Builder()
					.accountId(employee.getAccountId())
					.accountName(accounts.get(employee.getAccountId()).getName())
					.assignments(assignments)
					.employeeId(employee.getId())
					.employeeName(employee.getName())
					.employeeTitle(employee.getPositionName())
					.numberOfRolesAssigned(assignments)
					.status(entry.getValue())
					.build();

			models.add(model);
		}

		Collections.sort(models);

		return Collections.unmodifiableList(models);
	}

	public List<EmployeeSiteAssignmentModel> create(final Collection<Employee> employees,
	                                                final List<AccountSkill> skills,
	                                                final Table<Employee, AccountSkill, AccountSkillEmployee> employeeSkills,
	                                                final Map<Integer, AccountModel> accounts) {
		List<EmployeeSiteAssignmentModel> models = new ArrayList<>();
		for (Employee employee : employees) {
			int accountId = employee.getAccountId();

			List<SkillStatus> skillStatuses = new ArrayList<>();
			for (AccountSkill skill : skills) {
				SkillStatus status = SkillStatusCalculator.calculateStatusFromSkill(employeeSkills.get(employee, skill));
				skillStatuses.add(status);
			}

			EmployeeSiteAssignmentModel model = new EmployeeSiteAssignmentModel.Builder()
					.accountId(accountId)
					.accountName(accounts.get(accountId).getName())
					.employeeId(employee.getId())
					.employeeName(employee.getName())
					.employeeTitle(employee.getPositionName())
					.skillStatuses(skillStatuses)
					.build();

			models.add(model);
		}

		Collections.sort(models);

		return Collections.unmodifiableList(models);
	}
}
