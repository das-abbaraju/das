package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question_function")
public final class AuditQuestionFunction extends BaseTable {

	private AuditQuestion question;
	private QuestionFunctionType type;
	private QuestionFunction function;
	private String expression;

	private List<AuditQuestionFunctionWatcher> watchers = new ArrayList<AuditQuestionFunctionWatcher>();

	@ManyToOne
	@JoinColumn(name = "questionID", nullable = false)
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
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

	@OneToMany(mappedBy = "function")
	public List<AuditQuestionFunctionWatcher> getWatchers() {
		return watchers;
	}

	public void setWatchers(List<AuditQuestionFunctionWatcher> functions) {
		this.watchers = functions;
	}

	@Transient
	private Map<String, String> getParameterMap(AnswerMap answerMap) {
		Map<String, String> params = new HashMap<String, String>();
		for (AuditQuestionFunctionWatcher watcher : watchers) {
			AuditData auditData = answerMap.get(watcher.getQuestion().getId());
			String answer = "";
			if (auditData != null)
				answer = auditData.getAnswer();
			params.put(watcher.getUniqueCode(), answer);
		}

		return params;
	}

	@Transient
	public Object calculate(AnswerMap answerMap) {
		Object result = function.calculate(getParameterMap(answerMap));
		return result;
	}
}
