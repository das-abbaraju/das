package com.picsauditing.jpa.entities;

public enum CaoStatus {
	NotApplicable, Pending, Submitted, Verified, Approved, Rejected;

	/**
	 * 
	 * @return TRUE if NotApplicable or Pending
	 */
	public boolean isTemporary() {
		if (this.equals(NotApplicable))
			return true;
		if (this.equals(Pending))
			return true;
		return false;
	}

	public boolean isNotApplicable() {
		return this.equals(CaoStatus.NotApplicable);
	}

	public boolean isPending() {
		return this.equals(CaoStatus.Pending);
	}

	public boolean isSubmitted() {
		return this.equals(CaoStatus.Submitted);
	}

	public boolean isVerified() {
		return this.equals(CaoStatus.Verified);
	}

	public boolean isApproved() {
		return this.equals(CaoStatus.Approved);
	}

	public boolean isRejected() {
		return this.equals(CaoStatus.Rejected);
	}

	public String getColor() {
		String color = "";
		if (isPending() || isSubmitted() || isVerified())
			color = "Amber";
		else if (isApproved())
			color = "Green";
		else if (isRejected())
			color = "Red";

		return color;
	}
}
