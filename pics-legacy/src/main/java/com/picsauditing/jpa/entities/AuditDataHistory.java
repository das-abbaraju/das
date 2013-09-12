package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "pqfdata_hist")
public class AuditDataHistory extends BaseTable implements java.io.Serializable {

	private AuditData currentAuditData;
	private ContractorAudit audit;
	private AuditQuestion question;
	private String answer;
	private String comment;
	private YesNo wasChanged;
	private User auditor;
	private Date dateVerified;
	private User historyCreatedBy;
	private User historyUpdatedBy;
	private Date historyCreationDate;
	private Date historyUpdateDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "histID", nullable = false, updatable = false)
	public AuditData getCurrentAuditData() {
		return currentAuditData;
	}

	public void setCurrentAuditData(AuditData currentAuditData) {
		this.currentAuditData = currentAuditData;
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
	
	public String getAnswer() {
		return answer;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Enumerated(EnumType.STRING)
	public YesNo getWasChanged() {
		return wasChanged;
	}
	
	public void setWasChanged(YesNo wasChanged) {
		this.wasChanged = wasChanged;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}
	
	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getDateVerified() {
		return dateVerified;
	}
	
	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "histCreatedBy", nullable = true)
	public User getHistoryCreatedBy() {
		return historyCreatedBy;
	}
	
	public void setHistoryCreatedBy(User historyCreatedBy) {
		this.historyCreatedBy = historyCreatedBy;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "histUpdatedBy", nullable = true)
	public User getHistoryUpdatedBy() {
		return historyUpdatedBy;
	}
	
	public void setHistoryUpdatedBy(User historyUpdatedBy) {
		this.historyUpdatedBy = historyUpdatedBy;
	}
	
	@Column(name="histCreationDate")
	public Date getHistoryCreationDate() {
		return historyCreationDate;
	}
	
	public void setHistoryCreationDate(Date historyCreationDate) {
		this.historyCreationDate = historyCreationDate;
	}
	
	@Column(name="histUpdateDate")
	public Date getHistoryUpdateDate() {
		return historyUpdateDate;
	}
	
	public void setHistoryUpdateDate(Date historyUpdateDate) {
		this.historyUpdateDate = historyUpdateDate;
	}
	
}
