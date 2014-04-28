package com.picsauditing.employeeguard.models;

import java.util.List;

public class OperatorEmployeeModel {

	private RequiredSkills required;
	private List<ProjectStatusModel> projects;
	private List<RoleStatusModel> roles;

	public RequiredSkills getRequired() {
		return required;
	}

	public void setRequired(RequiredSkills required) {
		this.required = required;
	}

	public List<ProjectStatusModel> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectStatusModel> projects) {
		this.projects = projects;
	}

	public List<RoleStatusModel> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleStatusModel> roles) {
		this.roles = roles;
	}
}
