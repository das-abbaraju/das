package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@Entity
@Table(name = "workflow_step")
@SuppressWarnings("serial")
public class WorkflowStep extends BaseTable {
//	private Workflow workflow;
//	private AuditStatus oldStatus;
	private AuditStatus newStatus;
//	private EmailTemplate emailTemplate;
//	private boolean noteRequired = false;
//	private String name;
//	private String helpText;
//
//	@ManyToOne
//	@JoinColumn(name = "workflowID", nullable = false)
//	public Workflow getWorkflow() {
//		return workflow;
//	}
//
//	public void setWorkflow(Workflow workflow) {
//		this.workflow = workflow;
//	}
//
//	@Enumerated(EnumType.STRING)
//	public AuditStatus getOldStatus() {
//		return oldStatus;
//	}
//
//	public void setOldStatus(AuditStatus oldStatus) {
//		this.oldStatus = oldStatus;
//	}
//
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AuditStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(AuditStatus newStatus) {
		this.newStatus = newStatus;
	}

//	@ManyToOne
//	@JoinColumn(name = "emailTemplateID")
//	public EmailTemplate getEmailTemplate() {
//		return emailTemplate;
//	}
//
//	public void setEmailTemplate(EmailTemplate emailTemplate) {
//		this.emailTemplate = emailTemplate;
//	}
//
//	public boolean isNoteRequired() {
//		return noteRequired;
//	}
//
//	public void setNoteRequired(boolean noteRequired) {
//		this.noteRequired = noteRequired;
//	}
//
//	@Transient
//	public String getButtonName() {
//		return getName();
//	}
//
//	@Transient
//	public String getButtonHelpText() {
//		String text = helpText.toString();
//		if (text == null || text.startsWith(getClass().getSimpleName())) {
//			text = "";
//		}
//		return text;
//	}
//
//	@Transient
//	public String getName() {
//		if (name != null) {
//			return name;
//		}
//
//		return new TranslatableString(getI18nKey("name")).toTranslatedString();
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	@Transient
//	public String getHelpText() {
//		return helpText;
//	}
//
//	public void setHelpText(String helpText) {
//		this.helpText = helpText;
//	}
//
//    public static WorkflowStepBuilder builder() {
//        return new WorkflowStepBuilder();
//    }
}