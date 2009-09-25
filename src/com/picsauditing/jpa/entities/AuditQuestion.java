package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfquestions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class AuditQuestion extends BaseTable implements java.io.Serializable, Comparable<AuditQuestion> {
	static public final int EMR = 2034;
	static public final int MANUAL_PQF = 1331;
	static public final int OQ_EMPLOYEES = 894;

	static public final String[] TYPE_ARRAY = { "Additional Insured", "AMBest", "Check Box", "Country", "Date",
			"Decimal Number", "File", "FileCertificate", "Industry", "License", "Main Work", "Money",
			"Office Location", "Radio", "Service", "State", "Text", "Text Area", "Yes/No", "Yes/No/NA" };

	private AuditSubCategory subCategory;
	private int number;
	private String question;
	private String columnHeader;
	private String uniqueCode;
	private Date effectiveDate = new Date();
	private Date expirationDate;
	private YesNo hasRequirement = YesNo.No;
	private String okAnswer;
	private String requirement;
	private YesNo isRedFlagQuestion = YesNo.No;
	private String isRequired;
	private AuditQuestion dependsOnQuestion = null;
	private String dependsOnAnswer;
	private String questionType;
	private String title;
	private YesNo isVisible = YesNo.Yes;
	private YesNo isGroupedWithPrevious = YesNo.No;
	private LowMedHigh riskLevel = null;
	private String linkUrl1;
	private String linkText1;
	private String linkUrl2;
	private String linkText2;
	private String linkUrl3;
	private String linkText3;
	private String linkUrl4;
	private String linkText4;
	private String linkUrl5;
	private String linkText5;
	private String linkUrl6;
	private String linkText6;
	private boolean showComment = false;

	protected List<AuditQuestionOperatorAccount> operator;
	protected List<AuditQuestionOption> options;
	private String criteria;
	private String criteriaAnswer;
	private String helpPage;

	@Transient
	public AuditType getAuditType() {
		return subCategory.getCategory().getAuditType();
	}

	@Transient
	public String getExpandedNumber() {
		return subCategory.getCategory().getNumber() + "." + subCategory.getNumber() + "." + number;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subCategoryID", nullable = false)
	public AuditSubCategory getSubCategory() {
		return this.subCategory;
	}

	public void setSubCategory(AuditSubCategory subCategory) {
		this.subCategory = subCategory;
	}

	@Column(nullable = false)
	public int getNumber() {
		return this.number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Column(nullable = false, length = 1000)
	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getHasRequirement() {
		return this.hasRequirement;
	}

	@Transient
	public boolean isHasRequirementB() {
		if (Strings.isEmpty(requirement))
			return false;
		return YesNo.Yes.equals(hasRequirement);
	}

	public void setHasRequirement(YesNo hasRequirement) {
		this.hasRequirement = hasRequirement;
	}

	public String getOkAnswer() {
		return this.okAnswer;
	}

	public void setOkAnswer(String okAnswer) {
		this.okAnswer = okAnswer;
	}

	public String getRequirement() {
		return this.requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	/**
	 * Yes, No, Depends
	 * 
	 * @return
	 */
	public String getIsRequired() {
		return this.isRequired;
	}

	public void setIsRequired(String isRequired) {
		this.isRequired = isRequired;
	}

	@ManyToOne
	@JoinColumn(name = "dependsOnQID")
	public AuditQuestion getDependsOnQuestion() {
		return dependsOnQuestion;
	}

	public void setDependsOnQuestion(AuditQuestion dependsOnQuestion) {
		this.dependsOnQuestion = dependsOnQuestion;
	}

	@Column(name = "dependsOnAnswer")
	public String getDependsOnAnswer() {
		return this.dependsOnAnswer;
	}

	public void setDependsOnAnswer(String dependsOnAnswer) {
		this.dependsOnAnswer = dependsOnAnswer;
	}

	@Column(nullable = false)
	public String getQuestionType() {
		return this.questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getIsVisible() {
		return this.isVisible;
	}

	@Transient
	public boolean isVisible() {
		return YesNo.Yes.equals(isVisible);
	}

	public void setIsVisible(YesNo isVisible) {
		this.isVisible = isVisible;
	}

	@Column(name = "title", length = 250)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getIsGroupedWithPrevious() {
		return this.isGroupedWithPrevious;
	}

	public void setIsGroupedWithPrevious(YesNo isGroupedWithPrevious) {
		this.isGroupedWithPrevious = isGroupedWithPrevious;
	}

	public String getLinkUrl1() {
		return this.linkUrl1;
	}

	public void setLinkUrl1(String linkUrl1) {
		this.linkUrl1 = linkUrl1;
	}

	public String getLinkText1() {
		return this.linkText1;
	}

	public void setLinkText1(String linkText1) {
		this.linkText1 = linkText1;
	}

	public String getLinkUrl2() {
		return this.linkUrl2;
	}

	public void setLinkUrl2(String linkUrl2) {
		this.linkUrl2 = linkUrl2;
	}

	public String getLinkText2() {
		return this.linkText2;
	}

	public void setLinkText2(String linkText2) {
		this.linkText2 = linkText2;
	}

	public String getLinkUrl3() {
		return this.linkUrl3;
	}

	public void setLinkUrl3(String linkUrl3) {
		this.linkUrl3 = linkUrl3;
	}

	public String getLinkText3() {
		return this.linkText3;
	}

	public void setLinkText3(String linkText3) {
		this.linkText3 = linkText3;
	}

	public String getLinkUrl4() {
		return this.linkUrl4;
	}

	public void setLinkUrl4(String linkUrl4) {
		this.linkUrl4 = linkUrl4;
	}

	public String getLinkText4() {
		return this.linkText4;
	}

	public void setLinkText4(String linkText4) {
		this.linkText4 = linkText4;
	}

	public String getLinkUrl5() {
		return this.linkUrl5;
	}

	public void setLinkUrl5(String linkUrl5) {
		this.linkUrl5 = linkUrl5;
	}

	public String getLinkText5() {
		return this.linkText5;
	}

	public void setLinkText5(String linkText5) {
		this.linkText5 = linkText5;
	}

	public String getLinkUrl6() {
		return this.linkUrl6;
	}

	public void setLinkUrl6(String linkUrl6) {
		this.linkUrl6 = linkUrl6;
	}

	public String getLinkText6() {
		return this.linkText6;
	}

	public void setLinkText6(String linkText6) {
		this.linkText6 = linkText6;
	}

	@Temporal(TemporalType.DATE)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getIsRedFlagQuestion() {
		return this.isRedFlagQuestion;
	}

	public void setIsRedFlagQuestion(YesNo isRedFlagQuestion) {
		this.isRedFlagQuestion = isRedFlagQuestion;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "auditQuestion")
	public List<AuditQuestionOperatorAccount> getOperator() {
		return operator;
	}

	public void setOperator(List<AuditQuestionOperatorAccount> operator) {
		this.operator = operator;
	}

	@Column(length = 50)
	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
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

	@Enumerated(EnumType.ORDINAL)
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

	@Column(length = 30)
	public String getColumnHeader() {
		return columnHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
	}
	
	@Column(length = 100)
	public String getHelpPage() {
		return helpPage;
	}

	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}

	@Transient
	public String getColumnHeaderOrQuestion() {
		if (columnHeader != null && columnHeader.length() > 0)
			return columnHeader;
		if (question == null)
			return "";
		return question;
	}

	@Transient
	public String getShortQuestion() {
		String columnText = getColumnHeaderOrQuestion();

		if (columnText.length() > 100)
			columnText = columnText.substring(0, 100);
		columnText = getSubCategory().getCategory().getNumber() + "." + getSubCategory().getNumber() + "."
				+ getNumber() + " " + columnText;
		columnText = getSubCategory().getCategory().getAuditType().getAuditName() + ": " + columnText;

		return columnText;
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

	@Transient
	public boolean isValid() {
		if (getSubCategory().getCategory().getValidDate().after(getEffectiveDate())
				&& getSubCategory().getCategory().getValidDate().before(getExpirationDate()))
			return true;
		return false;
	}

	@Override
	public int compareTo(AuditQuestion other) {
		if (other == null) {
			return 1;
		}

		int cmp = getSubCategory().compareTo(other.getSubCategory());

		if (cmp != 0)
			return cmp;

		return new Integer(getNumber()).compareTo(new Integer(other.getNumber()));
	}
}
