package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.List;

public class ContractorEmployeeRoleAssignmentMatrix {

    private List<String> skillNames;
    private List<RoleInfo> roles;
    private List<ContractorEmployeeRoleAssignment> assignments;

    public List<String> getSkillNames() {
        return skillNames;
    }

    public void setSkillNames(List<String> skillNames) {
        this.skillNames = skillNames;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    public List<ContractorEmployeeRoleAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<ContractorEmployeeRoleAssignment> assignments) {
        this.assignments = assignments;
    }
}
