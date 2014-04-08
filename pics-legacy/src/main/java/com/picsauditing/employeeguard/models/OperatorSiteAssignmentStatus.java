package com.picsauditing.employeeguard.models;

import java.util.List;

public class OperatorSiteAssignmentStatus implements IdNameComposite, StatusSummarizable {

	private int id;
	private String name;
	private int employees;

	private int completed;
	private int pending;
	private int expiring;
	private int expired;

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

	@Override
	public int getEmployees() {
		return employees;
	}

	@Override
	public void setEmployees(int employees) {
		this.employees = employees;
	}

	@Override
	public int getCompleted() {
		return completed;
	}

	@Override
	public void setCompleted(int completed) {
		this.completed = completed;
	}

	@Override
	public int getPending() {
		return pending;
	}

	@Override
	public void setPending(int pending) {
		this.pending = pending;
	}

	@Override
	public int getExpiring() {
		return expiring;
	}

	@Override
	public void setExpiring(int expiring) {
		this.expiring = expiring;
	}

	@Override
	public int getExpired() {
		return expired;
	}

	@Override
	public void setExpired(int expired) {
		this.expired = expired;
	}

	public List<ProjectAssignmentModel> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectAssignmentModel> projects) {
		this.projects = projects;
	}
}
