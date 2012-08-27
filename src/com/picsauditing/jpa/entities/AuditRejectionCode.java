package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "audit_rejection_codes")
public class AuditRejectionCode extends BaseTable {

	private OperatorAccount operator;
	private AuditSubStatus auditSubStatus;
	private String rejectionReason;
	
	private static final long serialVersionUID = -6751099062193684624L;
	
	@ManyToOne
	@JoinColumn(name = "operatorID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
	
	@Column(name = "rejectionCategory")
	@Enumerated(EnumType.STRING)
	public AuditSubStatus getAuditSubStatus() {
		return auditSubStatus;
	}
	
	public void setAuditSubStatus(AuditSubStatus auditSubStatus) {
		this.auditSubStatus = auditSubStatus;
	}
	
	public String getRejectionReason() {
		return rejectionReason;
	}
	
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

}