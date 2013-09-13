package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * There are the values that are used by the {@link AuditQuestionFunction} for calculating the answers to
 * {@link AuditQuestion}s, as well as their Visiblity and Required status.
 *
 * @author kpartridge
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question_function_watcher")
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
