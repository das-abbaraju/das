package com.picsauditing.jpa.entities;

public enum AuditTypeClass {
	PQF, Audit, Policy, IM;
	
	public boolean isPolicy() {
		return Policy == this;
	}
	public boolean isPqf() {
		return PQF == this;
	}
}
