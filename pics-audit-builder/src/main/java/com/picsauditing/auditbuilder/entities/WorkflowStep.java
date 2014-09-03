package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@Entity(name = "com.picsauditing.auditbuilder.entities.WorkflowStep")
@Table(name = "workflow_step")
@SuppressWarnings("serial")
public class WorkflowStep extends BaseTable {
    private Workflow workflow;

	private DocumentStatus oldStatus;
	private DocumentStatus newStatus;

    @ManyToOne
    @JoinColumn(name = "workflowID", nullable = false)
    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Enumerated(EnumType.STRING)
	public DocumentStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(DocumentStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public DocumentStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(DocumentStatus newStatus) {
		this.newStatus = newStatus;
	}
}