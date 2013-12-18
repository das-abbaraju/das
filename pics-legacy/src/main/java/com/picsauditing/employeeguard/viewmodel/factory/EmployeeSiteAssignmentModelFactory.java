package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeSiteAssignmentModelFactory {

	public List<EmployeeSiteAssignmentModel> create(Map<Employee, SkillStatus> employeeStatuses, Map<Employee, List<Group>> roleAssignments, Map<Integer, AccountModel> accounts) {
		List<EmployeeSiteAssignmentModel> models = new ArrayList<>();

		for (Map.Entry<Employee, SkillStatus> entry : employeeStatuses.entrySet()) {
			Employee employee = entry.getKey();

			EmployeeSiteAssignmentModel model = new EmployeeSiteAssignmentModel.Builder()
					.accountId(employee.getAccountId())
					.accountName(accounts.get(employee.getAccountId()).getName())
					.assignments(roleAssignments.get(employee).size())
					.employeeId(employee.getId())
					.employeeName(employee.getName())
					.employeeTitle(employee.getPositionName())
					.status(entry.getValue())
					.build();

			models.add(model);
		}

		return models;
	}
}
