package com.picsauditing.jpa.entities;

public enum AuditTypeClass implements Translatable {
	PQF, Audit, Policy, IM;

	public boolean isPolicy() {
		return Policy == this;
	}

	public boolean isPqf() {
		return PQF == this;
	}

	public boolean isAudit() {
		return Audit == this;
	}

	public boolean isIm() {
		return IM == this;
	}

	@Override
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
