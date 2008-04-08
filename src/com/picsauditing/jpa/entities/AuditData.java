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
@Table(name = "pqfdata")
public class AuditData implements java.io.Serializable {

	private int dataID;
	@Deprecated
	private Account contractorAccount;
	private ContractorAudit audit;
	private AuditQuestion question;
	private short num;
	private String answer;
	private Account auditor;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID", nullable = false, insertable = false,updatable = false)
	public Account getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(Account contractorAccount) {
		this.contractorAccount = contractorAccount;
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

	public short getNum() {
		return num;
	}

	public void setNum(short num) {
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
	public Account getAuditor() {
		return auditor;
	}

	public void setAuditor(Account auditor) {
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

	public YesNo getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(YesNo isCorrect) {
		this.isCorrect = isCorrect;
	}

	public YesNo getWasChanged() {
		return wasChanged;
	}

	public void setWasChanged(YesNo wasChanged) {
		this.wasChanged = wasChanged;
	}

}
