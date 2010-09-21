package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum AuditStatus {
	Pending("Pending"),
	Incomplete("Reject"),
	Submitted("Submit"),
	Resubmitted("Resubmit"),
	Complete("Complete"),
	Approved("Approve"),
	NotApplicable("NotApplicable"),
	Expired("Expire");

	private String button;
	public static String DEFAULT = "- Audit Status -";
	
	private AuditStatus(String button) {
		this.button = button;
	}
	
	public String getButton() {
		return button;
	}
	
	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		values.add(AuditStatus.DEFAULT);
		for (AuditStatus value : AuditStatus.values())
			values.add(value.name());
		return values;
	}

	/**
	 * Pending, Incomplete, Submitted, Resubmitted, Complete, Approved,
	 * NotApplicable
	 * 
	 * @param o
	 * @return
	 */
	public boolean before(AuditStatus o) {
		return this.ordinal() < o.ordinal();
	}

	/**
	 * Pending, Incomplete, Submitted, Resubmitted, Complete, Approved,
	 * NotApplicable
	 * 
	 * @param o
	 * @return
	 */
	public boolean after(AuditStatus o) {
		return this.ordinal() > o.ordinal();
	}

	public boolean isApproved() {
		return this.equals(Approved);
	}

	public boolean isNotApplicable() {
		return this.equals(NotApplicable);
	}

	public boolean isPending() {
		return this.equals(Pending);
	}

	public boolean isSubmitted() {
		return this.equals(Submitted);
	}

	public boolean isResubmitted() {
		return this.equals(Resubmitted);
	}

	public boolean isSubmittedResubmitted() {
		return isSubmitted() || isResubmitted();
	}

	public boolean isIncomplete() {
		return this.equals(Incomplete);
	}

	public boolean isComplete() {
		return this.equals(Complete);
	}

	public boolean isExpired() {
		return this.equals(Expired);
	}

	/**
	 * Is the status Pending or Submitted
	 * 
	 * @return
	 */
	public boolean isPendingSubmittedResubmitted() {
		if (this.equals(Submitted))
			return true;
		if (this.equals(Resubmitted))
			return true;
		if (this.equals(Pending))
			return true;
		return false;
	}

	/**
	 * Is the status Pending or Submitted
	 * 
	 * @return
	 */
	public boolean isPendingSubmitted() {
		if (this.equals(Submitted))
			return true;
		if (this.equals(Pending))
			return true;
		return false;
	}
	
	static public AuditStatus[] valuesWithoutPendingExpired() {
		AuditStatus[] statuses = new AuditStatus[AuditStatus.values().length - 3];
		int i = 0;
		for (AuditStatus status : AuditStatus.values()) {
			if (status.after(AuditStatus.Pending) && status.before(AuditStatus.NotApplicable))
				statuses[i++] = status;
		}
		return statuses;
	}

}
