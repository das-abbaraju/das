package com.picsauditing.employeeguard.models;

public class ContractorSummary implements StatusSummarizable {

	private int completed;
	private int pending;
	private int expiring;
	private int expired;
	private int requested;

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

	public int getRequested() {
		return requested;
	}

	public void setRequested(int requested) {
		this.requested = requested;
	}
}