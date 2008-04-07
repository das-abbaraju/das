package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "pqfquestions")
public class AuditQuestion implements java.io.Serializable {
	static public int EMR03 = 127;
	static public int EMR04 = 126;
	static public int EMR05 = 889;
	static public int EMR06 = 1519;
	static public int EMR07 = 1617;

	private int questionID;
	private AuditSubCategory subCategory;
	private short number;
	private String question;
	private String hasRequirement;
	private String okAnswer;
	private String requirement;
	private String isRequired;
	private short dependsOnQid;
	private String dependsOnAnswer;
	private String questionType;
	private String isVisible;
	private Date lastModified;
	private String title;
	private String isGroupedWithPrevious;
	private String link;
	private String linkText;
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
	private Date dateCreated;
	private String isRedFlagQuestion;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	public int getQuestionID() {
		return this.questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subCategoryID", nullable = false, insertable = false, updatable = false)
	public AuditSubCategory getSubCategory() {
		return this.subCategory;
	}

	public void setSubCategory(AuditSubCategory subCategory) {
		this.subCategory = subCategory;
	}

	@Column(name = "number", nullable = false)
	public short getNumber() {
		return this.number;
	}

	public void setNumber(short number) {
		this.number = number;
	}

	@Column(name = "question", nullable = false, length = 16277215)
	public String getQuestion() {
		return this.question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Column(name = "hasRequirement", nullable = false, length = 3)
	public String getHasRequirement() {
		return this.hasRequirement;
	}

	public void setHasRequirement(String hasRequirement) {
		this.hasRequirement = hasRequirement;
	}

	@Column(name = "okAnswer", nullable = false, length = 10)
	public String getOkAnswer() {
		return this.okAnswer;
	}

	public void setOkAnswer(String okAnswer) {
		this.okAnswer = okAnswer;
	}

	@Column(name = "requirement", nullable = false, length = 16277215)
	public String getRequirement() {
		return this.requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	@Column(name = "isRequired", nullable = false, length = 7)
	public String getIsRequired() {
		return this.isRequired;
	}

	public void setIsRequired(String isRequired) {
		this.isRequired = isRequired;
	}

	@Column(name = "dependsOnQID", nullable = false)
	public short getDependsOnQid() {
		return this.dependsOnQid;
	}

	public void setDependsOnQid(short dependsOnQid) {
		this.dependsOnQid = dependsOnQid;
	}

	@Column(name = "dependsOnAnswer", nullable = false, length = 100)
	public String getDependsOnAnswer() {
		return this.dependsOnAnswer;
	}

	public void setDependsOnAnswer(String dependsOnAnswer) {
		this.dependsOnAnswer = dependsOnAnswer;
	}

	@Column(name = "questionType", nullable = false, length = 14)
	public String getQuestionType() {
		return this.questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	@Column(name = "isVisible", nullable = false, length = 3)
	public String getIsVisible() {
		return this.isVisible;
	}

	public void setIsVisible(String isVisible) {
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

	@Column(name = "isGroupedWithPrevious", nullable = false, length = 3)
	public String getIsGroupedWithPrevious() {
		return this.isGroupedWithPrevious;
	}

	public void setIsGroupedWithPrevious(String isGroupedWithPrevious) {
		this.isGroupedWithPrevious = isGroupedWithPrevious;
	}

	@Column(name = "link", nullable = false, length = 250)
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Column(name = "linkText", nullable = false, length = 250)
	public String getLinkText() {
		return this.linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	@Column(name = "linkURL1", nullable = false)
	public String getLinkUrl1() {
		return this.linkUrl1;
	}

	public void setLinkUrl1(String linkUrl1) {
		this.linkUrl1 = linkUrl1;
	}

	@Column(name = "linkText1", nullable = false)
	public String getLinkText1() {
		return this.linkText1;
	}

	public void setLinkText1(String linkText1) {
		this.linkText1 = linkText1;
	}

	@Column(name = "linkURL2", nullable = false)
	public String getLinkUrl2() {
		return this.linkUrl2;
	}

	public void setLinkUrl2(String linkUrl2) {
		this.linkUrl2 = linkUrl2;
	}

	@Column(name = "linkText2", nullable = false)
	public String getLinkText2() {
		return this.linkText2;
	}

	public void setLinkText2(String linkText2) {
		this.linkText2 = linkText2;
	}

	@Column(name = "linkURL3", nullable = false)
	public String getLinkUrl3() {
		return this.linkUrl3;
	}

	public void setLinkUrl3(String linkUrl3) {
		this.linkUrl3 = linkUrl3;
	}

	@Column(name = "linkText3", nullable = false)
	public String getLinkText3() {
		return this.linkText3;
	}

	public void setLinkText3(String linkText3) {
		this.linkText3 = linkText3;
	}

	@Column(name = "linkURL4", nullable = false)
	public String getLinkUrl4() {
		return this.linkUrl4;
	}

	public void setLinkUrl4(String linkUrl4) {
		this.linkUrl4 = linkUrl4;
	}

	@Column(name = "linkText4", nullable = false)
	public String getLinkText4() {
		return this.linkText4;
	}

	public void setLinkText4(String linkText4) {
		this.linkText4 = linkText4;
	}

	@Column(name = "linkURL5", nullable = false)
	public String getLinkUrl5() {
		return this.linkUrl5;
	}

	public void setLinkUrl5(String linkUrl5) {
		this.linkUrl5 = linkUrl5;
	}

	@Column(name = "linkText5", nullable = false)
	public String getLinkText5() {
		return this.linkText5;
	}

	public void setLinkText5(String linkText5) {
		this.linkText5 = linkText5;
	}

	@Column(name = "linkURL6", nullable = false)
	public String getLinkUrl6() {
		return this.linkUrl6;
	}

	public void setLinkUrl6(String linkUrl6) {
		this.linkUrl6 = linkUrl6;
	}

	@Column(name = "linkText6", nullable = false)
	public String getLinkText6() {
		return this.linkText6;
	}

	public void setLinkText6(String linkText6) {
		this.linkText6 = linkText6;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateCreated", nullable = false, length = 10)
	public Date getDateCreated() {
		return this.dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "isRedFlagQuestion", nullable = false, length = 3)
	public String getIsRedFlagQuestion() {
		return this.isRedFlagQuestion;
	}

	public void setIsRedFlagQuestion(String isRedFlagQuestion) {
		this.isRedFlagQuestion = isRedFlagQuestion;
	}

}
