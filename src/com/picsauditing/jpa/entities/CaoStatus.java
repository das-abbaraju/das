package com.picsauditing.jpa.entities;

public enum CaoStatus {
	NotApplicable, Pending, Awaiting, Verified, Approved, Rejected;
	
	/**
	 * 
	 * @return TRUE if NotApplicable or Awaiting
	 */
	public boolean isTemporary() {
		if (this.equals(NotApplicable))
			return true;
		if (this.equals(Awaiting))
			return true;
		return false;
	}
	
	public boolean isPending() {
		return this.equals(CaoStatus.Pending);
	}
	
	public boolean isAwaiting() {
		return this.equals(CaoStatus.Awaiting);
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
}
