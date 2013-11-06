package com.picsauditing.employeeguard.forms.operator;

import java.util.List;
import java.util.Map;

public class OperatorProjectAssignmentMatrix {
	private List<RoleInfo> roles;
	private List<String> skillNames;
	private List<OperatorEmployeeProjectAssignment> assignments;

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

	public List<OperatorEmployeeProjectAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(List<OperatorEmployeeProjectAssignment> assignments) {
		this.assignments = assignments;
	}

}
