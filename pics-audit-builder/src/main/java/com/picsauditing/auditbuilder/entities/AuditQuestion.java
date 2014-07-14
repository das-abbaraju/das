package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question")
public class AuditQuestion extends BaseHistory {

	static public final int MANUAL_PQF = 1331;
	static public final int COR = 2954;
	static public final int IEC = 10330;

	private int scoreWeight;
	private boolean hasRequirement;
	private boolean required;
	private String requiredAnswer;
	private String visibleAnswer;
	private String questionType;
	private AuditOptionGroup option;
	private String okAnswer;
	private AuditQuestion requiredQuestion;
	private AuditQuestion visibleQuestion;
	private AuditCategory category;

	private List<AuditQuestionFunction> functions = new ArrayList<>();
	private List<AuditQuestionFunctionWatcher> functionWatchers = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory auditCategory) {
		this.category = auditCategory;
	}

	@Column(nullable = false)
	public String getQuestionType() {
		return this.questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	@ManyToOne
	@JoinColumn(name = "optionID")
	public AuditOptionGroup getOption() {
		return option;
	}

	public void setOption(AuditOptionGroup option) {
		this.option = option;
	}

	@Column(nullable = false)
	public boolean isHasRequirement() {
		return hasRequirement;
	}

	public void setHasRequirement(boolean hasRequirement) {
		this.hasRequirement = hasRequirement;
	}

	public String getOkAnswer() {
		return this.okAnswer;
	}

	public void setOkAnswer(String okAnswer) {
		this.okAnswer = okAnswer;
	}

	@Column(nullable = false)
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@ManyToOne
	@JoinColumn(name = "requiredQuestion")
	public AuditQuestion getRequiredQuestion() {
		return requiredQuestion;
	}

	public void setRequiredQuestion(AuditQuestion requiredQuestion) {
		this.requiredQuestion = requiredQuestion;
	}

	public String getRequiredAnswer() {
		return requiredAnswer;
	}

	public void setRequiredAnswer(String requiredAnswer) {
		this.requiredAnswer = requiredAnswer;
	}

	@ManyToOne
	@JoinColumn(name = "visibleQuestion")
	public AuditQuestion getVisibleQuestion() {
		return visibleQuestion;
	}

	public void setVisibleQuestion(AuditQuestion visibleQuestion) {
		this.visibleQuestion = visibleQuestion;
	}

	public String getVisibleAnswer() {
		return visibleAnswer;
	}

	public void setVisibleAnswer(String visibleAnswer) {
		this.visibleAnswer = visibleAnswer;
	}

	@OneToMany(mappedBy = "question")
	public List<AuditQuestionFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<AuditQuestionFunction> functions) {
		this.functions = functions;
	}

	@OneToMany(mappedBy = "question")
	public List<AuditQuestionFunctionWatcher> getFunctionWatchers() {
		return functionWatchers;
	}

	public void setFunctionWatchers(List<AuditQuestionFunctionWatcher> functionWatchers) {
		this.functionWatchers = functionWatchers;
	}

	public int getScoreWeight() {
		return scoreWeight;
	}

	public void setScoreWeight(int scoreWeight) {
		this.scoreWeight = scoreWeight;
	}
}