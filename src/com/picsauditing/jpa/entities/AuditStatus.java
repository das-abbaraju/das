package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum AuditStatus {
	Pending,
	Incomplete,
	Submitted,
	Resubmitted,
	Complete,
	Approved,
	NotApplicable,
	Expired;

	public static String DEFAULT = "- Audit Status -";

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

}
