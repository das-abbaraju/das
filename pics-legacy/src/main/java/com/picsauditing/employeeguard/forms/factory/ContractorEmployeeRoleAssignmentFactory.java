package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ContractorEmployeeRoleAssignmentFactory {

	public List<ContractorEmployeeRoleAssignment> build(final List<Employee> contractorEmployees,
														final Role role,
														final Set<Employee> assignedEmployees,
														final Map<Employee, List<SkillStatus>> employeeSkills) {
		if (CollectionUtils.isEmpty(contractorEmployees) || role == null) {
			return Collections.emptyList();
		}

		List<ContractorEmployeeRoleAssignment> assignments = new ArrayList<>();
		List<AccountSkill> roleSkills = ExtractorUtil.extractList(role.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);
		Collections.sort(roleSkills);

		for (Employee employee : contractorEmployees) {
			boolean assigned = CollectionUtils.isNotEmpty(assignedEmployees) && assignedEmployees.contains(employee);
			List<SkillStatus> statuses = initializeSkillStatuses(roleSkills.size());

			if (employeeSkills.containsKey(employee)) {
				statuses = employeeSkills.get(employee);
			}

			assignments.add(new ContractorEmployeeRoleAssignment.Builder()
					.employeeId(employee.getId())
					.name(employee.getName())
					.title(employee.getPositionName())
					.assigned(assigned)
					.skillStatuses(statuses)
					.build());
		}

		return assignments;
	}

	private List<SkillStatus> initializeSkillStatuses(int size) {
		List<SkillStatus> skillStatuses = new ArrayList<>();

		for (int index = 0; index < size; index++) {
			skillStatuses.add(SkillStatus.Expired);
		}

		return skillStatuses;
	}
}
