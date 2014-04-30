package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AuditData")
@Table(name = "pqfdata")
public class AuditData extends BaseTable implements java.io.Serializable {

    private ContractorAudit audit;
	private AuditQuestion question;
	private String answer;
	private String comment;
	private Date dateVerified;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "questionID", nullable = false, updatable = false)
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}
}
