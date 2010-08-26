package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_type_rule")
public class AuditTypeRule extends AuditRule {

	private AuditType dependentAuditType;
	private AuditStatus dependentAuditStatus;

	@ManyToOne
	@JoinColumn(name = "dependentAuditTypeID")
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

	public void update(AuditTypeRule source) {
		super.update(source);
		dependentAuditType = source.dependentAuditType;
		dependentAuditStatus = source.dependentAuditStatus;
	}

}
