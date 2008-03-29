package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "pqfdata")
public class PqfData implements java.io.Serializable {

	private int dataID;
	@Deprecated
	private Account contractorAccount;
	private ContractorAudit audit;
	private PqfQuestion pqfquestion;
	private short num;
	private String answer;
	private Account auditor;
	private String comment;
	private Date dateVerified;
	private String verifiedAnswer;
	private YesNo isCorrect;
	private YesNo wasChanged;

	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID", nullable = false, insertable = false, updatable = false)
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
	public PqfQuestion getPqfquestion() {
		return pqfquestion;
	}

	public void setPqfquestion(PqfQuestion pqfquestion) {
		this.pqfquestion = pqfquestion;
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
