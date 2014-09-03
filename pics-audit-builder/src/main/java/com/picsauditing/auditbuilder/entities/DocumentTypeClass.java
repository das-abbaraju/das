package com.picsauditing.auditbuilder.entities;

public enum DocumentTypeClass {
	PQF, Audit, Policy, IM, Employee, Review;

	public boolean isPolicy() {
		return Policy == this;
	}
}