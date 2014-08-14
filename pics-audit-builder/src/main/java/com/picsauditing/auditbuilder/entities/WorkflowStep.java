package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@Entity(name = "com.picsauditing.auditbuilder.entities.WorkflowStep")
@Table(name = "workflow_step")
@SuppressWarnings("serial")
public class WorkflowStep extends BaseTable {
    private Workflow workflow;

	private AuditStatus oldStatus;
	private AuditStatus newStatus;

    @ManyToOne
    @JoinColumn(name = "workflowID", nullable = false)
    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    @Enumerated(EnumType.STRING)
	public AuditStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(AuditStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AuditStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(AuditStatus newStatus) {
		this.newStatus = newStatus;
	}
}