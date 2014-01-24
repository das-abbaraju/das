package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.List;

public class OperatorProjectAssignment {

    private final List<RoleInfo> roles;
    private final List<EmployeeProjectAssignment> employeeProjectAssignments;

    public OperatorProjectAssignment(Builder builder) {
        this.roles = builder.roles;
        this.employeeProjectAssignments = builder.employeeProjectAssignments;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public List<EmployeeProjectAssignment> getEmployeeProjectAssignments() {
        return employeeProjectAssignments;
    }

    public static class Builder {
        private List<RoleInfo> roles;
        private List<EmployeeProjectAssignment> employeeProjectAssignments;

        public Builder roles(List<RoleInfo> roles) {
            this.roles = roles;
            return this;
        }

        public Builder employeeSiteAssignments(List<EmployeeProjectAssignment> employeeProjectAssignments) {
            this.employeeProjectAssignments = employeeProjectAssignments;
            return this;
        }

        public OperatorProjectAssignment build() {
            return new OperatorProjectAssignment(this);
        }
    }
}
