package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@Entity
@Table(name = "workflow_step")
@SuppressWarnings("serial")
public class WorkflowStep extends BaseTable {

	private AuditStatus oldStatus;
	private AuditStatus newStatus;

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