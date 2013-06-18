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
@Table(name = "workflow_step")
@SuppressWarnings("serial")
public class WorkflowStep extends BaseTable {
	private Workflow workflow;
	private AuditStatus oldStatus;
	private AuditStatus newStatus;
	private EmailTemplate emailTemplate;
	private boolean noteRequired = false;
	private TranslatableString name;
	private TranslatableString helpText;

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

	@ManyToOne
	@JoinColumn(name = "emailTemplateID")
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	public boolean isNoteRequired() {
		return noteRequired;
	}
	
	public void setNoteRequired(boolean noteRequired) {
		this.noteRequired = noteRequired;
	}

	@Transient
	public String getButtonName() {
		return name.toString();
	}

	@Transient
	public String getButtonHelpText() {
		String text = helpText.toString();
		if (text == null || text.startsWith(getClass().getSimpleName()))
			text = "";
		return text;
	}

	@Transient
	public TranslatableString getName() {
		return name;
	}

	public void setName(TranslatableString name) {
		this.name = name;
	}

	@Transient
	public TranslatableString getHelpText() {
		return helpText;
	}

	public void setHelpText(TranslatableString helpText) {
		this.helpText = helpText;
	}
}
