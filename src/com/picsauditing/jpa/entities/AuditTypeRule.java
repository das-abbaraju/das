package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type_rule")
public class AuditTypeRule extends AuditRule {

	private AuditType dependentAuditType;
	private AuditStatus dependentAuditStatus;

	public AuditType getDependentAuditType() {
		return dependentAuditType;
	}

	public void setDependentAuditType(AuditType dependentAuditType) {
		this.dependentAuditType = dependentAuditType;
	}

	public AuditStatus getDependentAuditStatus() {
		return dependentAuditStatus;
	}

	public void setDependentAuditStatus(AuditStatus dependentAuditStatus) {
		this.dependentAuditStatus = dependentAuditStatus;
	}

}
