package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pqfdata")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class AuditData implements java.io.Serializable {

	private int dataID;
	private ContractorAudit audit;
	private AuditQuestion question;
	private int num;
	private String answer;
	private User auditor;
	private String comment;
	private Date dateVerified;
	private String verifiedAnswer;
	private YesNo isCorrect;
	private YesNo wasChanged;

	private FlagColor flagColor;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "dataID", nullable = false, insertable = false, updatable = false)
	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditID", nullable = false, updatable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "questionID", nullable = false, updatable = false)
	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

	public String getVerifiedAnswer() {
		return verifiedAnswer;
	}

	public void setVerifiedAnswer(String verifiedAnswer) {
		this.verifiedAnswer = verifiedAnswer;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(YesNo isCorrect) {
		this.isCorrect = isCorrect;
	}

	@Transient
	public boolean isVerified() {
		return YesNo.Yes.equals(isCorrect);
	}

	public void setVerified(boolean isCorrect) {
		this.isCorrect = isCorrect ? YesNo.Yes : YesNo.No;
	}

	@Transient
	public boolean isUnverified() {
		return YesNo.No.equals(isCorrect);
	}

	@Enumerated(EnumType.STRING)
	public YesNo getWasChanged() {
		return wasChanged;
	}

	public void setWasChanged(YesNo wasChanged) {
		this.wasChanged = wasChanged;
	}

	@Transient
	public String getVerifiedAnswerOrAnswer() {
		if (verifiedAnswer != null && verifiedAnswer.length() > 0)
			return verifiedAnswer;

		if (answer != null)
			return answer;

		return "";
	}

	@Transient
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	@Transient
	public boolean isHasRequirements() {
		// This may no be the best solution. We may want to make pqf not have
		// open requirements.
		if (audit.getAuditType().isPqf())
			return false;
		return (YesNo.Yes.equals(wasChanged) || isRequirementOpen());
	}

	@Transient
	public boolean isRequirementOpen() {
		return (question.getOkAnswer().indexOf(answer) == -1);
	}

	@Transient
	public boolean isCommentLength() {
		if (comment.length() > 0)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + dataID;
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
		final AuditData other = (AuditData) obj;
		if (dataID != other.dataID)
			return false;
		return true;
	}

}
