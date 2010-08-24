package com.picsauditing.jpa.entities;

import java.util.ArrayList;

public enum AuditStatus {
	Pending,
	Incomplete,
	Submitted,
	Resubmitted,
	Active,
	Exempt,
	Expired;

	public static String DEFAULT = "- Audit Status -";

	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		values.add(AuditStatus.DEFAULT);
		for (AuditStatus value : AuditStatus.values())
			values.add(value.name());
		return values;
	}

	public boolean isActive() {
		return this.equals(Active);
	}

	public boolean isExempt() {
		return this.equals(Exempt);
	}

	public boolean isExpired() {
		return this.equals(Expired);
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
	public boolean isActiveResubmittedExempt() {
		if (this.equals(Active))
			return true;
		if (this.equals(Exempt))
			return true;
		if (this.equals(Resubmitted))
			return true;
		return false;
	}

	/**
	 * Is the status Active or Exempt
	 * 
	 * @return
	 */
	public boolean isActiveExempt() {
		if (this.equals(Active))
			return true;
		if (this.equals(Exempt))
			return true;
		return false;
	}

	/**
	 * Is the status Active or Exempt or Submitted
	 * 
	 * @return
	 */
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
	public boolean isActiveSubmitted() {
		if (this.equals(Submitted))
			return true;
		if (this.equals(Active))
			return true;
		return false;
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
	public boolean isPendingExpired() {
		if (this.equals(Expired))
			return true;
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
	public boolean isComplete(AuditStatus minimumStatus) {
		if (this.equals(Expired))
			return true;
		if (this.equals(Active))
			return true;
		if (this.equals(Exempt))
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
