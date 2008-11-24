package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

@Entity
@Table(name = "pqfquestions")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class AuditQuestion implements java.io.Serializable {
	static public final int EMR05 = 889;
	static public final int EMR06 = 1519;
	static public final int EMR07 = 1617;
	
	static public final int EMR = 2034;

	static public int getEmrYear(int questionID) {
		switch (questionID) {
		case EMR07:
			return 2007;
		case EMR06:
			return 2006;
		case EMR05:
			return 2005;
		}
		return 0;
	}

	static public final int EMR_AVG = 0;
	static public final int MANUAL_PQF = 1331;

	static public final String[] TYPE_ARRAY = { "Check Box", "Country", "Date", "Decimal Number", "Drop Down", "File",
			"Industry", "License", "Main Work", "Manual", "Money", "Office", "Office Location", "Radio", "Service",
			"State", "Text", "Text Area", "Yes/No", "Yes/No/NA" }; // must
															// match
																	// ENUM in
																	// db

	static public final String filesFolder = "files/pqf";
	
	private int questionID;
	private AuditSubCategory subCategory;
	private int number;
	private String question;
	private Date dateCreated = new Date();
	private Date lastModified = new Date();
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
	private String columnHeader;
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

	protected List<AuditQuestionOperatorAccount> operator;
	protected List<AuditQuestionOption> options;
	protected AuditData answer;
	private String criteria;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	public int getQuestionID() {
		return this.questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
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

	public void setIsVisible(YesNo isVisible) {
		this.isVisible = isVisible;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "lastModified", nullable = false, length = 10)
	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	@Column(name = "title", nullable = false, length = 250)
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
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
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

	@Transient
	public AuditData getAnswer() {
		return answer;
	}

	public void setAnswer(AuditData answer) {
		this.answer = answer;
	}

	@Transient
	public boolean isRequired() {

		if (isRequired.equals("Yes"))
			return true;
		if (isRequired.equals("Depends")) {
			if (dependsOnQuestion == null)
				return false;
			if (dependsOnAnswer == null)
				return false;
			AuditData contractorAnswer = dependsOnQuestion.getAnswer();
			if (contractorAnswer == null)
				// The contractor hasn't answered this question yet
				return false;
			// Such as "Yes" and "Yes with Office" answers.
			if (dependsOnAnswer.equals("Yes*"))
				return contractorAnswer.getAnswer().startsWith("Yes");

			if (dependsOnAnswer.equals(contractorAnswer.getAnswer()))
				return true;
		}
		return false;
	}

	@OneToMany(mappedBy = "auditQuestion")
	@OrderBy("number")
	public List<AuditQuestionOption> getOptions() {
		return options;
	}

	public void setOptions(List<AuditQuestionOption> options) {
		this.options = options;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + questionID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AuditQuestion other = (AuditQuestion) obj;
		if (questionID != other.questionID)
			return false;
		return true;
	}

	@Column(length = 30)
	public String getColumnHeader() {
		return columnHeader;
	}

	public void setColumnHeader(String columnHeader) {
		this.columnHeader = columnHeader;
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
}
