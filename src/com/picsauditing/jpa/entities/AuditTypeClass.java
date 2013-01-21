package com.picsauditing.jpa.entities;

import javax.persistence.Transient;

public enum AuditTypeClass implements Translatable {
	PQF, Audit, Policy, IM, Employee, Review;

	public boolean isPolicy() {
		return Policy == this;
	}

	public boolean isPqf() {
		return PQF == this;
	}

	public boolean isAudit() {
		return Audit == this;
	}

	public boolean isEmployee() {
		return Employee == this;
	}

	public boolean isReview() {
		return Review == this;
	}
	
	public boolean isIm() {
		return IM == this;
	}
	
	public boolean isImEmployee() {
		return IM == this || Employee == this;
	}
	
	@Transient
	@Override
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
