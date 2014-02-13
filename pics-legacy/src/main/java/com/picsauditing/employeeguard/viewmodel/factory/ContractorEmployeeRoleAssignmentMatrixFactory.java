package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContractorEmployeeRoleAssignmentMatrixFactory {

	public ContractorEmployeeRoleAssignmentMatrix create(final int totalNumberOfEmployeesAssignedToSite,
	                                                     final List<AccountSkill> roleSkills,
	                                                     final Map<RoleInfo, Integer> roleCounts,
	                                                     final List<ContractorEmployeeRoleAssignment> assignments) {
		List<String> skillNames = new ArrayList<>();
		for (AccountSkill skill : roleSkills) {
			skillNames.add(skill.getName());
		}

		return new ContractorEmployeeRoleAssignmentMatrix.Builder()
				.totalNumberOfEmployeesAssignedToSite(totalNumberOfEmployeesAssignedToSite)
				.skillNames(skillNames)
				.roleEmployees(roleCounts)
				.assignments(assignments)
				.build();
	}


}
