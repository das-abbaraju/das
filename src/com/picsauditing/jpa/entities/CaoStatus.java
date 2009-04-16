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

	public boolean isPending() {
		return this.equals(CaoStatus.Pending);
	}

	public boolean isAwaiting() {
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

	public String getIcon() {
		boolean approved = true;
		
		return "<img src=\"images/icon_" + (approved ? "check" : "x") +
				".gif\" width=\"32\" height=\"32\" border=\"0\" title=\"" + this.toString() + "\" />";
	}

	static public String getIcon(String status) {
		boolean approved = true;
		
		return "<img src=\"images/icon_" + (approved ? "check" : "x") +
				".gif\" width=\"32\" height=\"32\" border=\"0\" title=\"" + status + "\" />";
	}

}
