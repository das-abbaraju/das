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

@Entity
@Table(name = "pqfdata")
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

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "dataID", nullable = false, insertable = false, updatable = false)
	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}

	@ManyToOne()
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
	public boolean getIsCorrectBoolean() {
		return YesNo.Yes.equals(isCorrect);
	}

	public void setIsCorrectBoolean(boolean isCorrect) {
		this.isCorrect = isCorrect ? YesNo.Yes : YesNo.No;
	}
	
	@Enumerated(EnumType.STRING)
	public YesNo getWasChanged() {
		return wasChanged;
	}

	public void setWasChanged(YesNo wasChanged) {
		this.wasChanged = wasChanged;
	}

}
