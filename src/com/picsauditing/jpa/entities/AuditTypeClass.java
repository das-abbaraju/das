package com.picsauditing.jpa.entities;

public enum AuditTypeClass {
	Audit, Policy;
	
	public boolean isAudit() {
		return Audit == this;
	}
}
