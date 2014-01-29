package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;

import java.util.List;
import java.util.Map;

public class ContractorEmployeeRoleAssignmentMatrixFactory {

	public ContractorEmployeeRoleAssignmentMatrix create(final int totalNumberOfEmployeesAssignedToSite,
	                                                     final Map<RoleInfo, Integer> roleCounts,
	                                                     final List<ContractorEmployeeRoleAssignment> assignments) {
		return new ContractorEmployeeRoleAssignmentMatrix.Builder()
				.totalNumberOfEmployeesAssignedToSite(totalNumberOfEmployeesAssignedToSite)
				.roleEmployees(roleCounts)
				.assignments(assignments)
				.build();
	}
}
