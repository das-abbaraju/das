package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.List;

public class OperatorProjectAssignment {

    private final List<RoleInfo> roles;
    private final List<EmployeeSiteAssignment> employeeSiteAssignments;

    public OperatorProjectAssignment(Builder builder) {
        this.roles = builder.roles;
        this.employeeSiteAssignments = builder.employeeSiteAssignments;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public List<EmployeeSiteAssignment> getEmployeeSiteAssignments() {
        return employeeSiteAssignments;
    }

    public static class Builder {
        private List<RoleInfo> roles;
        private List<EmployeeSiteAssignment> employeeSiteAssignments;

        public Builder roles(List<RoleInfo> roles) {
            this.roles = roles;
            return this;
        }

        public Builder employeeSiteAssignments(List<EmployeeSiteAssignment> employeeSiteAssignments) {
            this.employeeSiteAssignments = employeeSiteAssignments;
            return this;
        }

        public OperatorProjectAssignment build() {
            return new OperatorProjectAssignment(this);
        }
    }
}
