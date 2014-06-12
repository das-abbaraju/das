package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class MEmployeeRollupStatus {

	@Expose
	int completed;
	@Expose
	int expiring;
	@Expose
	int expired;

	private Set<MEmployeesManager.MEmployee> completedSet;
	private Set<MEmployeesManager.MEmployee> expiringSet;
	private Set<MEmployeesManager.MEmployee> expiredSet;


	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
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

	public Set<MEmployeesManager.MEmployee> getCompletedSet() {
		return completedSet;
	}

	public void setCompletedSet(Set<MEmployeesManager.MEmployee> completedSet) {
		this.completedSet = completedSet;
	}

	public Set<MEmployeesManager.MEmployee> getExpiringSet() {
		return expiringSet;
	}

	public void setExpiringSet(Set<MEmployeesManager.MEmployee> expiringSet) {
		this.expiringSet = expiringSet;
	}

	public Set<MEmployeesManager.MEmployee> getExpiredSet() {
		return expiredSet;
	}

	public void setExpiredSet(Set<MEmployeesManager.MEmployee> expiredSet) {
		this.expiredSet = expiredSet;
	}
}
