package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeSiteAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectAssignment;

import java.util.List;

public class OperatorProjectAssignmentFactory {

    public OperatorProjectAssignment create(List<RoleInfo> roles, List<EmployeeSiteAssignment> employeeSiteAssignments) {
        return new OperatorProjectAssignment.Builder()
                .roles(roles)
                .employeeSiteAssignments(employeeSiteAssignments)
                .build();
    }
}
