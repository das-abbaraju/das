package com.picsauditing.jpa.entities;

public enum AuditTypeClass {
	Audit, Policy, IM;
	
	public boolean isAudit() {
		return Audit == this;
	}
}
