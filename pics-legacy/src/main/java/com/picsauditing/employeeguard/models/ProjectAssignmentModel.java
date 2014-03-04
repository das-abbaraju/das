package com.picsauditing.employeeguard.models;

import java.util.Date;

public class ProjectAssignmentModel extends ProjectModel implements StatusSummary {

	private String location;
	private Date startDate;
	private Date endDate;
	private int completed;
	private int pending;
	private int expiring;
	private int expired;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
