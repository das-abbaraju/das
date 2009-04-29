package com.picsauditing.jpa.entities;

public enum AuditTypeClass {
	PQF, Audit, Policy, IM;
	
	public boolean isPQF() {
		return PQF == this;
	}
	
	public boolean isAudit() {
		return Audit == this;
	}
	
	public boolean isPolicy() {
		return Policy == this;
	}
}
