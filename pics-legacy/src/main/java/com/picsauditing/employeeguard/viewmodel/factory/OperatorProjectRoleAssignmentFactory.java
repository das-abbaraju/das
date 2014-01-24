package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeSiteRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectRoleAssignment;

import java.util.List;

public class OperatorProjectRoleAssignmentFactory {

    public OperatorProjectRoleAssignment create(List<RoleInfo> roles, List<EmployeeSiteRoleAssignment> employeeSiteRoleAssignments) {
        return new OperatorProjectRoleAssignment.Builder()
                .roles(roles)
                .employeeSiteRoleAssignments(employeeSiteRoleAssignments)
                .build();
    }
}
