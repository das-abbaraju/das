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
public final class DocumentQuestionFunctionWatcher extends BaseTable {

	private DocumentQuestionFunction function;
	private DocumentQuestion question;
	private String uniqueCode;

	@ManyToOne
	@JoinColumn(name = "functionID", nullable = false)
	public DocumentQuestionFunction getFunction() {
		return function;
	}

	public void setFunction(DocumentQuestionFunction function) {
		this.function = function;
	}

	@ManyToOne
	@JoinColumn(name = "questionID", nullable = false)
	public DocumentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(DocumentQuestion question) {
		this.question = question;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
}