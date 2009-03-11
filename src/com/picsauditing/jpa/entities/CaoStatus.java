package com.picsauditing.jpa.entities;

public enum CaoStatus {
	NotApplicable, Awaiting, Approved, Rejected;
	
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
}
