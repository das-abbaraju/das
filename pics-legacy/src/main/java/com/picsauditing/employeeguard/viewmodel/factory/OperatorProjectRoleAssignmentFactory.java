package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.model.Role;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectRoleAssignment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
