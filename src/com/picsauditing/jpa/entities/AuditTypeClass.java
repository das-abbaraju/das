package com.picsauditing.jpa.entities;

public enum AuditTypeClass {
	PQF,
	Audit,
	Policy,
	IM,
	AnnualUpdate;

	public boolean isPolicy() {
		return Policy == this;
	}

	public boolean isPqf() {
		return PQF == this;
	}

	public boolean isAudit() {
		return Audit == this;
	}

}
