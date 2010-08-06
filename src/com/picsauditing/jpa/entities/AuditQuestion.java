package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestion extends BaseHistory {
	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	static public final int EMR = 2034;
	static public final int MANUAL_PQF = 1331;
	static public final int OQ_EMPLOYEES = 894;
	static public final int COR = 2954;

	static public final String[] TYPE_ARRAY = { "Additional Insured", "AMBest",
			"Check Box", "Country", "Date", "Decimal Number", "File",
			"FileCertificate", "Industry", "License", "Main Work", "Money",
			"Number", "Office Location", "Radio", "Service", "State", "Text",
			"Text Area", "Yes/No", "Yes/No/NA" };

	private AuditCategory auditCategory;
	private int number;
	private String name;
	private String questionType;
	private boolean hasRequirement;
	private String okAnswer;
	private boolean required;
	private AuditQuestion requiredQuestion;
	private String requiredAnswer;
	private AuditQuestion visibleQuestion;
	private String visibleAnswer;
	private String columnHeader;
	private String uniqueCode;
	private String title;
	private boolean groupedWithPrevious;
	private boolean flaggable;
	private boolean showComment;
	private LowMedHigh riskLevel = null;
	private String helpPage;
	private String requirement;
	
	private String criteria;
	private String criteriaAnswer;

	private List<AuditQuestion> requiredQuestions;
	private List<AuditQuestion> visibleQuestions;
	protected List<AuditQuestionOption> options;

	public AuditQuestion() {

	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getAuditCategory() {
		return this.auditCategory;
	}

	public void setAuditCategory(AuditCategory auditCategory) {
		this.auditCategory = auditCategory;
	}

	@Column(nullable = false)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Column(nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(nullable = false)
	public String getQuestionType() {
		return this.questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public void setHasRequirement(boolean hasRequirement) {
		this.hasRequirement = hasRequirement;
	}

	@Column(nullable = false)
	public boolean isHasRequirement() {
		return hasRequirement;
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

	@Column(length = 30)
	public String getColumnHeader() {
		return columnHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}

	@Column(length = 50)
	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@Column(name = "title", length = 250)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(nullable = false)
	public boolean isGroupedWithPrevious() {
		return groupedWithPrevious;
	}

	public void setGroupedWithPrevious(boolean groupedWithPrevious) {
		this.groupedWithPrevious = groupedWithPrevious;
	}

	@Column(nullable = false)
	public boolean isFlaggable() {
		return flaggable;
	}

	public void setFlaggable(boolean flaggable) {
		this.flaggable = flaggable;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	public boolean isShowComment() {
		return showComment;
	}

	public void setShowComment(boolean showComment) {
		this.showComment = showComment;
	}

	@Enumerated(EnumType.ORDINAL)
	public LowMedHigh getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(LowMedHigh risk) {
		this.riskLevel = risk;
	}

	@Column(length = 100)
	public String getHelpPage() {
		return helpPage;
	}

	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	public String getRequirement() {
		return requirement;
	}

	@Transient
	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	@Transient
	public String getCriteriaAnswer() {
		return criteriaAnswer;
	}

	public void setCriteriaAnswer(String criteriaAnswer) {
		this.criteriaAnswer = criteriaAnswer;
	}

	@OneToMany(mappedBy = "requiredQuestion")
	public List<AuditQuestion> getRequiredQuestions() {
		return requiredQuestions;
	}

	public void setRequiredQuestions(List<AuditQuestion> requiredQuestions) {
		this.requiredQuestions = requiredQuestions;
	}

	@OneToMany(mappedBy = "visibleQuestion")
	public List<AuditQuestion> getVisibleQuestions() {
		return visibleQuestions;
	}

	public void setVisibleQuestions(List<AuditQuestion> visibleQuestions) {
		this.visibleQuestions = visibleQuestions;
	}

	@OneToMany(mappedBy = "auditQuestion")
	@OrderBy("number")
	public List<AuditQuestionOption> getOptions() {
		return options;
	}

	@Transient
	public List<AuditQuestionOption> getOptionsVisible() {
		List<AuditQuestionOption> options = new ArrayList<AuditQuestionOption>();
		for (AuditQuestionOption o : getOptions())
			if (o.isVisibleB())
				options.add(o);
		return options;
	}

	public void setOptions(List<AuditQuestionOption> options) {
		this.options = options;
	}

	@Transient
	public AuditType getAuditType() {
		return auditCategory.getAuditType();
	}

	@Transient
	public String getExpandedNumber() {
		return "" + number;
	}
}
