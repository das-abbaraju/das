package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum ApprovalStatus implements Translatable {
	/** No - "Not Approved" **/
	N,
	/** Pending - "Pending Approval" **/
	P,
	/** Contractor - "Contractor in General Contractor Relationship" **/
	C,
	/** Yes - "Approved" **/
	Y;

	public boolean isPending() {
		return this.equals(P);
	}

	public boolean isYes() {
		return this.equals(Y);
	}

	public boolean isNo() {
		return this.equals(N);
	}

	public boolean isContractor() {
		return this.equals(C);
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
