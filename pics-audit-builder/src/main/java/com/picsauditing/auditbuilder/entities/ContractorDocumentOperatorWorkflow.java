package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorAuditOperatorWorkflow")
@Table(name = "contractor_audit_operator_workflow")
public class ContractorDocumentOperatorWorkflow extends BaseTable {

	private ContractorDocumentOperator cao;
	private DocumentStatus status;
	private DocumentStatus previousStatus;
	private String notes;

	@ManyToOne
	@JoinColumn(name = "caoID", nullable = false)
	public ContractorDocumentOperator getCao() {
		return cao;
	}

	public void setCao(ContractorDocumentOperator cao) {
		this.cao = cao;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public DocumentStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentStatus status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	public DocumentStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(DocumentStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	@Column(length = 1000)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}