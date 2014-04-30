package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@Entity(name = "com.picsauditing.flagcalculator.entities.WorkflowStep")
@Table(name = "workflow_step")
@SuppressWarnings("serial")
public class WorkflowStep extends BaseTable {
<<<<<<< HEAD
	private Workflow workflow;
//	private AuditStatus oldStatus;
	private AuditStatus newStatus;
//	private EmailTemplate emailTemplate;
//	private boolean noteRequired = false;
//	private String name;
//	private String helpText;

	@ManyToOne
	@JoinColumn(name = "workflowID", nullable = false)
	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

//	@Enumerated(EnumType.STRING)
//	public AuditStatus getOldStatus() {
//		return oldStatus;
//	}
//
//	public void setOldStatus(AuditStatus oldStatus) {
//		this.oldStatus = oldStatus;
//	}
//
=======
	private AuditStatus newStatus;

>>>>>>> 7ae760b... US831 Deprecated old FDC
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AuditStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(AuditStatus newStatus) {
		this.newStatus = newStatus;
	}
}