package com.picsauditing.jpa.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("D")
public class AuditCategoryMatrixDesktop extends AuditCategoryMatrix {
	private AuditQuestion auditQuestion;

	@ManyToOne
	@JoinColumn(name = "foreignKeyID")
	public AuditQuestion getAuditQuestion() {
		return auditQuestion;
	}

	public void setAuditQuestion(AuditQuestion auditQuestion) {
		this.auditQuestion = auditQuestion;
	}
}
