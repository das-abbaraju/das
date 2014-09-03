package com.picsauditing.auditbuilder.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditTypeRule")
@Table(name = "audit_type_rule")
public class DocumentTypeRule extends DocumentRule {

	private boolean manuallyAdded = false;

	public boolean isManuallyAdded() {
		return manuallyAdded;
	}

	public void setManuallyAdded(boolean manuallyAdded) {
		this.manuallyAdded = manuallyAdded;
	}
}