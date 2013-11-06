package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.List;
import java.util.Map;

public class ContractorProjectAssignmentMatrix {

    private List<RoleInfo> roles;
	private List<String> skillNames;
    private List<ContractorEmployeeProjectAssignment> assignments;
	private Map<Integer, List<Integer>> employeeRoles;

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

	public List<String> getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(List<String> skillNames) {
		this.skillNames = skillNames;
	}

	public List<ContractorEmployeeProjectAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<ContractorEmployeeProjectAssignment> assignments) {
        this.assignments = assignments;
    }

	public Map<Integer, List<Integer>> getEmployeeRoles() {
		return employeeRoles;
	}

	public void setEmployeeRoles(Map<Integer, List<Integer>> employeeRoles) {
		this.employeeRoles = employeeRoles;
	}
}
