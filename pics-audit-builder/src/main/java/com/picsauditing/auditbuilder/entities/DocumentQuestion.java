package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditQuestion")
@Table(name = "audit_question")
public class DocumentQuestion extends BaseHistory {

	static public final int MANUAL_PQF = 1331;
	static public final int COR = 2954;
	static public final int IEC = 10330;

    private int number;
	private int scoreWeight;
	private boolean hasRequirement;
	private boolean required;
	private String requiredAnswer;
	private String visibleAnswer;
	private String questionType;
	private DocumentOptionGroup option;
	private String okAnswer;
	private DocumentQuestion requiredQuestion;
	private DocumentQuestion visibleQuestion;
	private DocumentCategory category;

	private List<DocumentQuestionFunction> functions = new ArrayList<>();
	private List<DocumentQuestionFunctionWatcher> functionWatchers = new ArrayList<>();

    @Column(nullable = false)
    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public DocumentCategory getCategory() {
		return category;
	}

	public void setCategory(DocumentCategory documentCategory) {
		this.category = documentCategory;
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
	public DocumentOptionGroup getOption() {
		return option;
	}

	public void setOption(DocumentOptionGroup option) {
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
	public DocumentQuestion getRequiredQuestion() {
		return requiredQuestion;
	}

	public void setRequiredQuestion(DocumentQuestion requiredQuestion) {
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
	public DocumentQuestion getVisibleQuestion() {
		return visibleQuestion;
	}

	public void setVisibleQuestion(DocumentQuestion visibleQuestion) {
		this.visibleQuestion = visibleQuestion;
	}

	public String getVisibleAnswer() {
		return visibleAnswer;
	}

	public void setVisibleAnswer(String visibleAnswer) {
		this.visibleAnswer = visibleAnswer;
	}

	@OneToMany(mappedBy = "question")
	public List<DocumentQuestionFunction> getFunctions() {
		return functions;
	}

	public void setFunctions(List<DocumentQuestionFunction> functions) {
		this.functions = functions;
	}

	@OneToMany(mappedBy = "question")
	public List<DocumentQuestionFunctionWatcher> getFunctionWatchers() {
		return functionWatchers;
	}

	public void setFunctionWatchers(List<DocumentQuestionFunctionWatcher> functionWatchers) {
		this.functionWatchers = functionWatchers;
	}

	public int getScoreWeight() {
		return scoreWeight;
	}

	public void setScoreWeight(int scoreWeight) {
		this.scoreWeight = scoreWeight;
	}
}