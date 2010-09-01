package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum AuditStatus {
	NotApplicable,
	Pending,
	Submitted,
	Resubmitted,
	Incomplete,
	Complete,
	Approved;

	public static String DEFAULT = "- Audit Status -";

	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		values.add(AuditStatus.DEFAULT);
		for (AuditStatus value : AuditStatus.values())
			values.add(value.name());
		return values;
	}

	public boolean isApproved() {
		return this.equals(Approved);
	}

	@Deprecated
	public boolean isActive() {
		return isApproved();
	}

	public boolean isNotApplicable() {
		return this.equals(NotApplicable);
	}

	@Deprecated
	public boolean isExempt() {
		return isNotApplicable();
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

	public boolean isIncomplete() {
		return this.equals(Incomplete);
	}

	/**
	 * Is the status Active or Exempt
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isActiveResubmittedExempt() {
		if (this.equals(Resubmitted))
			return true;
		return isActiveExempt();
	}

	/**
	 * Is the status Active or Exempt
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isActiveExempt() {
		if (isActive())
			return true;
		if (isExempt())
			return true;
		return false;
	}

	/**
	 * Is the status Active or Exempt or Submitted
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isActiveExemptSubmitted() {
		if (this.equals(Submitted))
			return true;
		return isActiveExempt();
	}

	/**
	 * Is the status Active or Submitted
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isActiveSubmitted() {
		if (this.equals(Submitted))
			return true;
		return isActive();
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

	/**
	 * Is the status Pending or Expired
	 * 
	 * @return
	 */
	@Deprecated
	public boolean isPendingExpired() {
		if (this.equals(Pending))
			return true;
		return false;
	}

	/**
	 * if minimumStatus is Active, then return true if Active, Exempt<br>
	 * if minimumStatus is Submitted, then return true if Submitted, Resubmitted
	 * too
	 * 
	 * @param minimumStatus
	 * @return
	 */
	@Deprecated
	public boolean isComplete(AuditStatus minimumStatus) {
		// if (this.equals(Expired))
		// return true;
		if (isActiveExempt())
			return true;
		if (Submitted.equals(minimumStatus)) {
			if (this.equals(Submitted))
				return true;
			if (this.equals(Resubmitted))
				return true;
		}
		return false;
	}
}
