package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum ApprovalStatus implements Translatable {
	/** No - "Not Approved" **/
	N,
	/** Pending - "Pending Approval" **/
	P,
	/** Contractor - "Contractor in General Contractor Relationship" **/
	C,
	/** Contractor Denied - "Contractor denied General Contractor Relationship" **/
	D,
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

	public boolean isDenied() {
		return this.equals(D);
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
