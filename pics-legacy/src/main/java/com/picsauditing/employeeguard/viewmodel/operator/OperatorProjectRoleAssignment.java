package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;

import java.util.List;

public class OperatorProjectRoleAssignment {

    private final List<RoleInfo> roles;
    private final List<SkillInfo> skills;
    private final List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments;

    public OperatorProjectRoleAssignment(final Builder builder) {
        this.roles = builder.roles;
        this.skills = builder.skills;
        this.employeeProjectRoleAssignments = builder.employeeProjectRoleAssignments;
    }

    public List<EmployeeProjectRoleAssignment> getEmployeeProjectRoleAssignments() {
        return employeeProjectRoleAssignments;
    }

    public static class Builder {

        private List<RoleInfo> roles;
        private List<SkillInfo> skills;
        private List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments;

        public Builder roles(final List<RoleInfo> roles) {
            this.roles = roles;
            return this;
        }

        public Builder skills(final List<SkillInfo> skills) {
            this.skills = skills;
            return this;
        }

        public Builder employeeSiteRoleAssignments(final List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments) {
            this.employeeProjectRoleAssignments = employeeProjectRoleAssignments;
            return this;
        }

        public OperatorProjectRoleAssignment build() {
            return new OperatorProjectRoleAssignment(this);
        }
    }
}
