package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;

public class ContractorEmployeeRoleAssignmentMatrixFactory {
    public ContractorEmployeeRoleAssignmentMatrix create() {
        // Map<Employee, Set<Role>> ?
        // List<Employee> all contractor employees
        // Skills for role
        // Map<Role, Integer> for role counts???

        return new ContractorEmployeeRoleAssignmentMatrix.Builder().build();
    }
}
