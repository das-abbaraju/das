package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectRoleAssignment;

import java.util.List;

public class OperatorProjectRoleAssignmentFactory {

    public OperatorProjectRoleAssignment create(final List<RoleInfo> roles,
                                                final List<SkillInfo> skills,
                                                final List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments) {
        return new OperatorProjectRoleAssignment.Builder()
                .roles(roles)
                .employeeSiteRoleAssignments(employeeProjectRoleAssignments)
                .build();
    }
}
