package com.picsauditing.jpa.entities;

import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfquestion_text")
public class AuditQuestionText extends BaseTable implements
		java.io.Serializable {
	private AuditQuestion auditQuestion;
	private Locale locale = AuditQuestion.DEFAULT_LOCALE;
	private String question;
	private String requirement;

	public AuditQuestionText() {
	}

	public AuditQuestionText(AuditQuestion auditQuestion, String question) {
		this.auditQuestion = auditQuestion;
		this.question = question;
	}

	public AuditQuestionText(AuditQuestion auditQuestion, String question,
			Locale locale) {
		this.auditQuestion = auditQuestion;
		this.question = question;
		this.locale = locale;
	}

	public AuditQuestionText(AuditQuestionText a, AuditQuestion aq) {
		this.auditQuestion = aq;
		this.locale = a.getLocale();
		this.question = a.getQuestion();
		this.requirement = a.getRequirement();
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "questionID", nullable = false, updatable = false)
	public AuditQuestion getAuditQuestion() {
		return auditQuestion;
	}

	public void setAuditQuestion(AuditQuestion auditQuestion) {
		this.auditQuestion = auditQuestion;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	@Override
	public String toString() {
		return locale + " - " + question;
	}
}
