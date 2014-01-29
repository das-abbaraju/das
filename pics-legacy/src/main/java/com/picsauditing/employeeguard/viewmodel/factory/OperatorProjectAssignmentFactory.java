package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.model.Role;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectAssignment;

import java.util.List;

public class OperatorProjectAssignmentFactory {

    public OperatorProjectAssignment create(final List<Role> roles,
                                            final List<EmployeeProjectAssignment> employeeProjectAssignments) {
        return new OperatorProjectAssignment.Builder()
                .roles(roles)
                .employeeSiteAssignments(employeeProjectAssignments)
                .build();
    }
}
