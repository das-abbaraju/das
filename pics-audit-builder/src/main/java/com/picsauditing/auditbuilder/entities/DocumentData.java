package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditData")
@Table(name = "pqfdata")
public class DocumentData extends BaseTable implements java.io.Serializable {

    private ContractorDocument audit;
	private DocumentQuestion question;
	private String answer;
	private Date dateVerified;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorDocument getAudit() {
		return audit;
	}

	public void setAudit(ContractorDocument audit) {
		this.audit = audit;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "questionID", nullable = false, updatable = false)
	public DocumentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(DocumentQuestion question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

	public void setVerified(boolean inValue) {
		this.setDateVerified(inValue ? new Date() : null);
	}
}