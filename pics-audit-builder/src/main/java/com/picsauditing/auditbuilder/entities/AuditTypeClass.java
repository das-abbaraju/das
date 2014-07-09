package com.picsauditing.auditbuilder.entities;

public enum AuditTypeClass {
	PQF, Audit, Policy, IM, Employee, Review;

	public boolean isPolicy() {
		return Policy == this;
	}
}