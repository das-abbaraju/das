package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		return models;
	}
}
