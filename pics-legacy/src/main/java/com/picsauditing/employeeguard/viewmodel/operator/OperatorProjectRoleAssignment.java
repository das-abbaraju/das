package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.viewmodel.model.Role;
import com.picsauditing.employeeguard.viewmodel.model.Skill;

import java.util.List;

public class OperatorProjectRoleAssignment {

    private final List<Role> roles;
    private final List<Skill> skills;
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

        private List<Role> roles;
        private List<Skill> skills;
        private List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments;

        public Builder roles(final List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder skills(final List<Skill> skills) {
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
