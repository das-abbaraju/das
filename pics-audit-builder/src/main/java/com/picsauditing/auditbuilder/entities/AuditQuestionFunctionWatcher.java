package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditQuestionFunctionWatcher")
@Table(name = "audit_question_function_watcher")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "audit_cache")
public final class AuditQuestionFunctionWatcher extends BaseTable {

	private AuditQuestionFunction function;
	private AuditQuestion question;
	private String uniqueCode;

	@ManyToOne
	@JoinColumn(name = "functionID", nullable = false)
	public AuditQuestionFunction getFunction() {
		return function;
	}

	public void setFunction(AuditQuestionFunction function) {
		this.function = function;
	}

	@ManyToOne
	@JoinColumn(name = "questionID", nullable = false)
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
}