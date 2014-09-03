package com.picsauditing.auditbuilder.entities;

public enum DocumentStatus {
	Pending,
	Incomplete,
	Submitted,
	Resubmit,
	Resubmitted,
	Complete,
	PendingClientApproval,
	Approved,
	NotApplicable,
	Expired;

	public boolean before(DocumentStatus o) {
		return this.ordinal() < o.ordinal();
	}

	public boolean after(DocumentStatus o) {
		return this.ordinal() > o.ordinal();
	}

	public boolean isApproved() {
		return this.equals(Approved);
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

	public boolean isResubmit() {
		if(this.equals(Resubmit))
			return true;
		return false;
	}
}