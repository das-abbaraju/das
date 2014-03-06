package com.picsauditing.employeeguard.models;

import java.util.List;

public class OperatorSiteAssignmentStatus implements StatusSummarizable {

	private int id;
	private String name;
	private int employees;
	private StatusSummary status;
	private List<ProjectAssignmentModel> projects;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEmployees() {
		return employees;
	}

	public void setEmployees(int employees) {
		this.employees = employees;
	}

	@Override
	public StatusSummary getStatus() {
		return status;
	}

	@Override
	public void setStatus(StatusSummary status) {
		this.status = status;
	}

	public List<ProjectAssignmentModel> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectAssignmentModel> projects) {
		this.projects = projects;
	}
}
