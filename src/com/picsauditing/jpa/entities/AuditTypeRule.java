package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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

	@Transient
	public String getDependentAuditTypeLabel() {
		return dependentAuditType == null ? "*" : dependentAuditType.toString();
	}

	public void setDependentAuditType(AuditType dependentAuditType) {
		this.dependentAuditType = dependentAuditType;
	}

	public AuditStatus getDependentAuditStatus() {
		return dependentAuditStatus;
	}

	@Transient
	public String getDependentAuditStatusLabel() {
		return dependentAuditStatus == null ? "*" : dependentAuditStatus.toString();
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
