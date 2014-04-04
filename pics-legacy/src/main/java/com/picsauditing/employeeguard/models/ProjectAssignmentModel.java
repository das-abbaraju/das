package com.picsauditing.employeeguard.models;

public class ProjectAssignmentModel extends ProjectModel implements StatusSummarizable {

	private int employees;
	private int completed;
	private int pending;
	private int expiring;
	private int expired;

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
}
