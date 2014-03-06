package com.picsauditing.employeeguard.models;

public class StatusSummary {

	private int completed;
	private int pending;
	private int expiring;
	private int expired;

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
}
