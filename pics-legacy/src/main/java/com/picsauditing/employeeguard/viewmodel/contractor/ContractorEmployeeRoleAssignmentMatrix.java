package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContractorEmployeeRoleAssignmentMatrix {

    private final int totalNumberOfEmployeesAssignedToSite;
    private final List<String> skillNames;
    private final Map<RoleInfo, Integer> roleEmployee;
    private final List<ContractorEmployeeRoleAssignment> assignments;

    public ContractorEmployeeRoleAssignmentMatrix(Builder builder) {
        this.totalNumberOfEmployeesAssignedToSite = builder.totalNumberOfEmployeesAssignedToSite;
        this.skillNames = CollectionUtils.isEmpty(builder.skillNames)
                ? Collections.<String>emptyList() : Collections.unmodifiableList(builder.skillNames);
        this.roleEmployee = MapUtils.isEmpty(builder.roleEmployee)
                ? Collections.<RoleInfo, Integer>emptyMap() : Collections.unmodifiableMap(builder.roleEmployee);
        this.assignments = CollectionUtils.isEmpty(builder.assignments)
                ? Collections.<ContractorEmployeeRoleAssignment>emptyList() : Collections.unmodifiableList(builder.assignments);
    }

    public int getTotalNumberOfEmployeesAssignedToSite() {
        return totalNumberOfEmployeesAssignedToSite;
    }

    public List<String> getSkillNames() {
        return skillNames;
    }

    public Map<RoleInfo, Integer> getRoleEmployee() {
        return roleEmployee;
    }

    public List<ContractorEmployeeRoleAssignment> getAssignments() {
        return assignments;
    }

    public static class Builder {
        private int totalNumberOfEmployeesAssignedToSite;
        private List<String> skillNames;
        private Map<RoleInfo, Integer> roleEmployee;
        private List<ContractorEmployeeRoleAssignment> assignments;

        public Builder totalNumberOfEmployeesAssignedToSite(int totalNumberOfEmployeesAssignedToSite) {
            this.totalNumberOfEmployeesAssignedToSite = totalNumberOfEmployeesAssignedToSite;
            return this;
        }

        public Builder skillNames(List<String> skillNames) {
            this.skillNames = skillNames;
            return this;
        }

        public Builder roleEmployees(Map<RoleInfo, Integer> roleEmployees) {
            this.roleEmployee = roleEmployees;
            return this;
        }

        public Builder assignments(List<ContractorEmployeeRoleAssignment> assignments) {
            this.assignments = assignments;
            return this;
        }

        public ContractorEmployeeRoleAssignmentMatrix build() {
            return new ContractorEmployeeRoleAssignmentMatrix(this);
        }
    }
}
