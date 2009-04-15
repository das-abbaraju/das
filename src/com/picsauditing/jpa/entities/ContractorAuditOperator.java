package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@Entity
@Table(name = "contractor_audit_operator")
public class ContractorAuditOperator extends BaseTable {
	private ContractorAudit audit;
	private OperatorAccount operator;
	private CaoStatus status = CaoStatus.Pending;
	private CaoStatus recommendedStatus = CaoStatus.Pending;
	private String notes;
	private boolean inherit;

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
	
	public boolean isInherit() {
		return inherit;
	}

	public void setInherit(boolean inherit) {
		this.inherit = inherit;
	}

	@Transient
	public boolean isNotesLength() {
		if(Strings.isEmpty(notes))
			return false;
		return true;
	}
}
