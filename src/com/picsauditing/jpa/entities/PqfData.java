package com.picsauditing.jpa.entities;

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
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

	private PqfDataKey id;
	private ContractorInfo contractorInfo;
	private Account auditor;
	private Pqfquestion pqfquestion;
	private short num;
	private String answer;
	private String comment;
	private Date dateVerified;
	private String verifiedAnswer;
	private String isCorrect;
	private String wasChanged;

	public PqfData() {
	}

	public PqfData(PqfDataKey id, ContractorInfo contractorInfo, Pqfquestion pqfquestion, short num, String answer,
			String comment, Date dateVerified, String verifiedAnswer, String isCorrect, String wasChanged) {
		this.id = id;
		this.contractorInfo = contractorInfo;
		this.pqfquestion = pqfquestion;
		this.num = num;
		this.answer = answer;
		this.comment = comment;
		this.dateVerified = dateVerified;
		this.verifiedAnswer = verifiedAnswer;
		this.isCorrect = isCorrect;
		this.wasChanged = wasChanged;
	}

	public PqfData(PqfDataKey id, ContractorInfo contractorInfo, Account auditor, Pqfquestion pqfquestion, short num,
			String answer, String comment, Date dateVerified, String verifiedAnswer, String isCorrect, String wasChanged) {
		this.id = id;
		this.contractorInfo = contractorInfo;
		this.auditor = auditor;
		this.pqfquestion = pqfquestion;
		this.num = num;
		this.answer = answer;
		this.comment = comment;
		this.dateVerified = dateVerified;
		this.verifiedAnswer = verifiedAnswer;
		this.isCorrect = isCorrect;
		this.wasChanged = wasChanged;
	}

	@EmbeddedId
	@AttributeOverrides( { @AttributeOverride(name = "conId", column = @Column(name = "conID", nullable = false)),
			@AttributeOverride(name = "questionId", column = @Column(name = "questionID", nullable = false)) })
	public PqfDataKey getId() {
		return this.id;
	}

	public void setId(PqfDataKey id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conID", nullable = false, insertable = false, updatable = false)
	public ContractorInfo getContractorInfo() {
		return this.contractorInfo;
	}

	public void setContractorInfo(ContractorInfo contractorInfo) {
		this.contractorInfo = contractorInfo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditorID")
	public Account getAuditor() {
		return this.auditor;
	}

	public void setAuditor(Account auditor) {
		this.auditor = auditor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "questionID", nullable = false, insertable = false, updatable = false)
	public Pqfquestion getPqfquestion() {
		return this.pqfquestion;
	}

	public void setPqfquestion(Pqfquestion pqfquestion) {
		this.pqfquestion = pqfquestion;
	}

	@Column(name = "num", nullable = false)
	public short getNum() {
		return this.num;
	}

	public void setNum(short num) {
		this.num = num;
	}

	@Column(name = "answer", nullable = false, length = 65535)
	public String getAnswer() {
		return this.answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Column(name = "comment", nullable = false)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "dateVerified", nullable = true, length = 10)
	public Date getDateVerified() {
		return this.dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}

	@Column(name = "verifiedAnswer", nullable = false, length = 65535)
	public String getVerifiedAnswer() {
		return this.verifiedAnswer;
	}

	public void setVerifiedAnswer(String verifiedAnswer) {
		this.verifiedAnswer = verifiedAnswer;
	}

	@Column(name = "isCorrect", nullable = false, length = 3)
	public String getIsCorrect() {
		return this.isCorrect;
	}

	public void setIsCorrect(String isCorrect) {
		this.isCorrect = isCorrect;
	}

	@Column(name = "wasChanged", nullable = false, length = 3)
	public String getWasChanged() {
		return this.wasChanged;
	}

	public void setWasChanged(String wasChanged) {
		this.wasChanged = wasChanged;
	}

}
