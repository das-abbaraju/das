package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditQuestionFunction")
@Table(name = "audit_question_function")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "audit_cache")
public final class DocumentQuestionFunction extends BaseTable {

	private DocumentQuestion question;
	private QuestionFunctionType type;
	private QuestionFunction function;
	private String expression;
	private boolean overwrite;

	private List<DocumentQuestionFunctionWatcher> watchers = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "questionID", nullable = false)
	public DocumentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(DocumentQuestion question) {
		this.question = question;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public QuestionFunctionType getType() {
		return type;
	}

	public void setType(QuestionFunctionType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public QuestionFunction getFunction() {
		return function;
	}

	public void setFunction(QuestionFunction function) {
		this.function = function;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	@OneToMany(mappedBy = "function")
	public List<DocumentQuestionFunctionWatcher> getWatchers() {
		return watchers;
	}

	public void setWatchers(List<DocumentQuestionFunctionWatcher> functions) {
		this.watchers = functions;
	}
}