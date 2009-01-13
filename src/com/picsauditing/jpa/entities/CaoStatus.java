package com.picsauditing.jpa.entities;

public enum CaoStatus {
	NotApplicable, Missing, Approved, Rejected;
	
	public boolean isTemporary() {
		if (this.equals(NotApplicable))
			return true;
		if (this.equals(Missing))
			return true;
		return false;
	}
}
