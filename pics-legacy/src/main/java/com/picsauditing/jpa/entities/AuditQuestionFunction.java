package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.jpa.entities.QuestionFunction.FunctionInput;
import com.picsauditing.util.AnswerMap;

/**
 * Entity used to determine either the Answer (Calculation), Visibility and whether or not a {@link AuditQuestion} is
 * Required.
 * 
 * @author kpartridge
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question_function")
public final class AuditQuestionFunction extends BaseTable {

	private AuditQuestion question;
	private QuestionFunctionType type;
	private QuestionFunction function;
	private String expression;
	private boolean overwrite;

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

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	@OneToMany(mappedBy = "function")
	public List<AuditQuestionFunctionWatcher> getWatchers() {
		return watchers;
	}

	public void setWatchers(List<AuditQuestionFunctionWatcher> functions) {
		this.watchers = functions;
	}

    @Transient
    public Object calculate(AnswerMap answerMap) {
        return calculate(answerMap, null);
    }

	@Transient
	public Object calculate(AnswerMap answerMap, String currentAnswer) {
            Object result;
		try {
            FunctionInput input = new FunctionInput.Builder().answerMap(answerMap).watchers(watchers).build();
            input.setCurrentAnswer(currentAnswer);
            input.setExpression(expression);
			result = function.calculate(input);
		}
		catch (NumberFormatException e) {
			result = "Audit.missingParameter";
		}
		return result;
	}
}
