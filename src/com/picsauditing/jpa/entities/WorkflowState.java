package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "workflow_state")
@SuppressWarnings("serial")
public class WorkflowState extends BaseTable {
	private Workflow workflow;
	private AuditStatus status;
	private boolean hasRequirements;
	private TranslatableString name;
	private boolean contractorCanEdit;
	private boolean operatorCanEdit;

	@ManyToOne
	@JoinColumn(name = "workflowID", nullable = false)
	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@Transient
	public TranslatableString getName() {
		return this.name;
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}


	public boolean isHasRequirements() {
		return hasRequirements;
	}

	public void setHasRequirements(boolean hasRequirements) {
		this.hasRequirements = hasRequirements;
	}

	public boolean isContractorCanEdit() {
		return contractorCanEdit;
	}

	public void setContractorCanEdit(boolean contractorCanEdit) {
		this.contractorCanEdit = contractorCanEdit;
	}

	public boolean isOperatorCanEdit() {
		return operatorCanEdit;
	}

	public void setOperatorCanEdit(boolean operatorCanEdit) {
		this.operatorCanEdit = operatorCanEdit;
	}
}
