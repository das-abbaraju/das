package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.viewmodel.model.Role;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectRoleAssignment;

import java.util.List;

public class OperatorProjectRoleAssignmentFactory {

    public OperatorProjectRoleAssignment create(final List<Role> roles,
                                                final List<Skill> skills,
                                                final List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments) {


        return new OperatorProjectRoleAssignment.Builder()
                .roles(roles)
                .skills(skills)
                .employeeSiteRoleAssignments(employeeProjectRoleAssignments)
                .build();
    }
}
