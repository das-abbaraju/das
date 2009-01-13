package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable {
	private ContractorAudit audit;
	private OperatorAccount operator;
	private CaoStatus status = CaoStatus.Missing;
	private CaoStatus recommendedStatus = CaoStatus.Missing;
	private String notes;

	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false, updatable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public CaoStatus getStatus() {
		return status;
	}

	public void setStatus(CaoStatus status) {
		this.status = status;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public CaoStatus getRecommendedStatus() {
		return recommendedStatus;
	}

	public void setRecommendedStatus(CaoStatus recommendedStatus) {
		this.recommendedStatus = recommendedStatus;
	}

	@Column(length = 255)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
