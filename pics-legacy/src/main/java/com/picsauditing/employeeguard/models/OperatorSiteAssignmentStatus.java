package com.picsauditing.employeeguard.models;

import java.util.List;

public class OperatorSiteAssignmentStatus {

	private int id;
	private String name;
	private int employees;
	private int completed;
	private int pending;
	private int expiring;
	private int expired;
	private List<ProjectStatusModel> projects;

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

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getExpiring() {
		return expiring;
	}

	public void setExpiring(int expiring) {
		this.expiring = expiring;
	}

	public int getExpired() {
		return expired;
	}

	public void setExpired(int expired) {
		this.expired = expired;
	}

	public List<ProjectStatusModel> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectStatusModel> projects) {
		this.projects = projects;
	}
}
