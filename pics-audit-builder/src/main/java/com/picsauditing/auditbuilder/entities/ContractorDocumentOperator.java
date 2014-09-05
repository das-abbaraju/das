package com.picsauditing.auditbuilder.entities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorAuditOperator")
@Table(name = "contractor_audit_operator")
public class ContractorDocumentOperator extends BaseTable {

	private ContractorDocument audit;
	private OperatorAccount operator;
	private DocumentStatus status = DocumentStatus.Pending;
	private Date statusChangedDate;
	private int percentComplete;
	private int percentVerified;
	private boolean visible = true;
	private List<ContractorDocumentOperatorPermission> caoPermissions = new ArrayList<>();
	private List<ContractorDocumentOperatorWorkflow> caoWorkflow = new ArrayList<>();
	private DocumentSubStatus documentSubStatus;

	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorDocument getAudit() {
		return audit;
	}

	public void setAudit(ContractorDocument audit) {
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

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	public DocumentStatus getStatus() {
		return status;
	}

	public void setStatus(DocumentStatus status) {
		this.status = status;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Date getStatusChangedDate() {
		return statusChangedDate;
	}

	public void setStatusChangedDate(Date statusChangedDate) {
		this.statusChangedDate = statusChangedDate;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}

    public int getPercentVerified() {
		return percentVerified;
	}

	public void setPercentVerified(int percentVerified) {
		this.percentVerified = percentVerified;
	}

	@OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
	public List<ContractorDocumentOperatorPermission> getCaoPermissions() {
		return caoPermissions;
	}

	public void setCaoPermissions(List<ContractorDocumentOperatorPermission> caoPermissions) {
		this.caoPermissions = caoPermissions;
	}

	@OneToMany(mappedBy = "cao", cascade = { CascadeType.ALL })
	public List<ContractorDocumentOperatorWorkflow> getCaoWorkflow() {
		return caoWorkflow;
	}

	public void setCaoWorkflow(List<ContractorDocumentOperatorWorkflow> caoWorkflow) {
		this.caoWorkflow = caoWorkflow;
	}

    @Column(name = "auditSubStatus", nullable = true)
	@Enumerated(EnumType.STRING)
	public DocumentSubStatus getDocumentSubStatus() {
		return documentSubStatus;
	}

	public void setDocumentSubStatus(DocumentSubStatus documentSubStatus) {
		this.documentSubStatus = documentSubStatus;
	}
}