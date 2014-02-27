package com.picsauditing.jpa.entities;

import com.picsauditing.access.PermissionAware;
import com.picsauditing.access.Permissions;

import javax.persistence.Transient;

public enum ApprovalStatus implements Translatable, PermissionAware {

	/** Pending - "Pending Approval" **/
	P,
	/** No - "Rejected" **/
	N,
	/** Yes - "Approved" **/
	Y,
	/** Yes and Forced - "Default to Approved" **/
	YF,
	/** No and Forced - "Default to Not Approved" **/
	NF,
	/** Contractor - "Contractor in General Contractor Relationship" **/
	C,
	/** Contractor Denied - "Contractor denied General Contractor Relationship" **/
	D;
    public static ApprovalStatus Pending = P;
    public static ApprovalStatus Rejected = N;
    public static ApprovalStatus Approved = Y;

	public boolean isPending() {
		return this.equals(P);
	}

	public boolean isYes() {
		return this.equals(Y) || this.equals(YF);
	}

	public boolean isNo() {
		return this.equals(N) || this.equals(NF);
	}

	public boolean isYesForced() {
		return this.equals(NF);
	}

	public boolean isNoForced() {
		return this.equals(NF);
	}

	public boolean isForced() {
		return this.equals(NF) || this.equals(YF);
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

    public boolean isVisibleTo(Permissions permissions) {
        return (this != YF && this != NF);
    }
}