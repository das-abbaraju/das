package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestion extends BaseHistory implements Comparable<AuditQuestion> {

	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	static public final int EMR = 2034;
	static public final int MANUAL_PQF = 1331;
	static public final int OQ_EMPLOYEES = 894;
	static public final int COR = 2954;

	static public final String[] TYPE_ARRAY = { "Additional Insured", "AMBest", "Check Box", "Country", "Date",
			"Decimal Number", "File", "FileCertificate", "License", "Main Work", "Money", "Number",
			"Office Location", "Radio", "Service", "State", "Text", "Text Area", "Yes/No", "Yes/No/NA" };

	private AuditCategory category;
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
	private String requirement;
	private String helpPage;
	private String helpText;

	private String criteria;
	private String criteriaAnswer;

	private List<AuditQuestion> dependentRequired;
	private List<AuditQuestion> dependentVisible;
	private Set<AuditQuestion> dependentQuestions;
	protected List<AuditQuestionOption> options;
	private List<AuditCategoryRule> auditCategoryRules;
	private List<AuditTypeRule> auditTypeRules;

	public AuditQuestion() {

	}

	public AuditQuestion(AuditQuestion a, AuditCategory ac) {
		this.number = a.number;
		this.name = a.name;
		this.questionType = a.questionType;
		this.hasRequirement = a.hasRequirement;
		this.okAnswer = a.okAnswer;
		this.required = a.required;
		this.requiredQuestion = a.requiredQuestion;
		this.requiredAnswer = a.requiredAnswer;
		this.visibleQuestion = a.visibleQuestion;
		this.visibleAnswer = a.visibleAnswer;
		this.columnHeader = a.columnHeader;
		this.uniqueCode = a.uniqueCode;
		this.title = a.title;
		this.groupedWithPrevious = a.groupedWithPrevious;
		this.flaggable = a.flaggable;
		this.showComment = a.showComment;
		this.riskLevel = a.riskLevel;
		this.helpPage = a.helpPage;
		this.helpText = a.helpText;
		this.requirement = a.requirement;
		this.category = ac;
		this.effectiveDate = a.effectiveDate;
		this.expirationDate = a.expirationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryID", nullable = false)
	public AuditCategory getCategory() {
		return this.category;
	}

	public void setCategory(AuditCategory auditCategory) {
		this.category = auditCategory;
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

	@Column(length = 1000)
	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
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
	public List<AuditQuestion> getDependentRequired() {
		return dependentRequired;
	}

	public void setDependentRequired(List<AuditQuestion> requiredQuestions) {
		this.dependentRequired = requiredQuestions;
	}

	@OneToMany(mappedBy = "visibleQuestion")
	public List<AuditQuestion> getDependentVisible() {
		return dependentVisible;
	}

	public void setDependentVisible(List<AuditQuestion> visibleQuestions) {
		this.dependentVisible = visibleQuestions;
	}

	@Transient
	public List<AuditQuestion> getDependentVisible(String answer) {
		List<AuditQuestion> dependentVisibleBasedOnAnswer = new ArrayList<AuditQuestion>();
		for (AuditQuestion visQ : dependentVisible) {
			if (visQ.visibleAnswer != null && visQ.visibleAnswer.equals(answer))
				dependentVisibleBasedOnAnswer.add(visQ);
		}

		return dependentVisibleBasedOnAnswer;
	}

	@Transient
	public List<AuditQuestion> getDependentVisibleHide(String answer) {
		List<AuditQuestion> dependentVisibleBasedOnAnswer = new ArrayList<AuditQuestion>();
		for (AuditQuestion visQ : dependentVisible) {
			if (visQ.visibleAnswer != null && !visQ.visibleAnswer.equals(answer))
				dependentVisibleBasedOnAnswer.add(visQ);
		}

		return dependentVisibleBasedOnAnswer;
	}

	@Transient
	public Set<AuditQuestion> getDependentQuestions() {
		if (dependentQuestions == null) {
			dependentQuestions = new HashSet<AuditQuestion>();
			dependentQuestions.addAll(dependentRequired);
			dependentQuestions.addAll(dependentQuestions);
		}

		return dependentQuestions;
	}

	@OneToMany(mappedBy = "question")
	public List<AuditCategoryRule> getAuditCategoryRules() {
		return auditCategoryRules;
	}

	public void setAuditCategoryRules(List<AuditCategoryRule> auditCategoryRules) {
		this.auditCategoryRules = auditCategoryRules;
	}

	@OneToMany(mappedBy = "question")
	public List<AuditTypeRule> getAuditTypeRules() {
		return auditTypeRules;
	}

	public void setAuditTypeRules(List<AuditTypeRule> auditTypeRules) {
		this.auditTypeRules = auditTypeRules;
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
		return category.getParentAuditType();
	}

	@Transient
	public String getExpandedNumber() {
		return category.getFullNumber() + "." + number;
	}

	@Transient
	public String getColumnHeaderOrQuestion() {
		if (columnHeader != null && columnHeader.length() > 0)
			return columnHeader;
		if (getName() == null)
			return "";
		return getName();
	}

	@Override
	public int compareTo(AuditQuestion other) {
		if (other == null) {
			return 1;
		}

		int cmp = getCategory().compareTo(other.getCategory());

		if (cmp != 0)
			return cmp;

		return new Integer(getNumber()).compareTo(new Integer(other.getNumber()));
	}
	
	/**
	 * 		Comparator for comparing AuditQuestions not based on
	 * 		natural ordering.  Use compareTo if you want to order based 
	 * 		on the AuditQuestion's number and direct Category (it's natural order)
	 * @return
	 * 		The comparator for full ordering of AuditQuestions
	 */
	public static Comparator<AuditQuestion> getComparator(){
		return new Comparator<AuditQuestion>() {
			@Override
			public int compare(AuditQuestion o1, AuditQuestion o2) {
				String[] o1a = o1.getExpandedNumber().split("\\.");
				String[] o2a = o2.getExpandedNumber().split("\\.");
				for(int i=0; i<o1a.length; i++){
					if(i>o2a.length)
						return -1;
					if(o1a[i].equals(o2a[i]))
						continue;
					else
						return Integer.valueOf(o1a[i]).compareTo(Integer.valueOf(o2a[i]));
				}				
				return 0;
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSON(boolean full) {
		JSONObject j = super.toJSON(full);
		j.put("category", category.toJSON());
		j.put("name", name);

		return j;
	}

	/**
	 * Return true if there are category rules that require immediate
	 * recalculation when the answer to this question changes
	 * 
	 * @return
	 */
	@Transient
	public boolean isRecalculateCategories() {
		if (getAuditCategoryRules().size() > 0)
			return true;
		if (getAuditTypeRules().size() > 0)
			return true;
		return false;
	}

	@Transient
	public String getShortQuestion() {
		String columnText = getName();

		if (columnText.length() > 45) {
			columnText = columnText.substring(0, 45);
			columnText += " [...]";
		}

		return getExpandedNumber() + ": " + columnText;
	}
}
