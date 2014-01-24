package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.List;

public class OperatorProjectRoleAssignment {

    private final List<RoleInfo> roles;
    private final List<EmployeeSiteRoleAssignment> employeeSiteRoleAssignments;

    public OperatorProjectRoleAssignment(Builder builder) {
        this.roles = builder.roles;
        this.employeeSiteRoleAssignments = builder.employeeSiteRoleAssignments;
    }

    public List<EmployeeSiteRoleAssignment> getEmployeeSiteRoleAssignments() {
        return employeeSiteRoleAssignments;
    }

    public static class Builder {
        private List<RoleInfo> roles;
        private List<EmployeeSiteRoleAssignment> employeeSiteRoleAssignments;

        public Builder roles(List<RoleInfo> roles) {
            this.roles = roles;
            return this;
        }

        public Builder employeeSiteRoleAssignments(List<EmployeeSiteRoleAssignment> employeeSiteRoleAssignments) {
            this.employeeSiteRoleAssignments = employeeSiteRoleAssignments;
            return this;
        }

        public OperatorProjectRoleAssignment build() {
            return new OperatorProjectRoleAssignment(this);
        }
    }
}
