package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ContractorEmployeeRoleAssignmentFactory {

	public List<ContractorEmployeeRoleAssignment> build(final List<Employee> contractorEmployees,
														final Set<Employee> assignedEmployees,
														final Map<Employee, List<SkillStatus>> employeeSkills) {
		if (CollectionUtils.isEmpty(contractorEmployees)) {
			return Collections.emptyList();
		}

		List<ContractorEmployeeRoleAssignment> assignments = new ArrayList<>();
		for (Employee employee : contractorEmployees) {
			boolean assigned = CollectionUtils.isNotEmpty(assignedEmployees) && assignedEmployees.contains(employee);

			assignments.add(new ContractorEmployeeRoleAssignment.Builder()
					.employeeId(employee.getId())
					.name(employee.getName())
					.title(employee.getPositionName())
					.assigned(assigned)
					.skillStatuses(employeeSkills.get(employee))
					.build());
		}

		return assignments;
	}
}
