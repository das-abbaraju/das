package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "contractor_audit")
public class ContractorAudit {
	private int id = 0;
	private AuditType auditType;
	private Account contractorAccount;
	private Date createdDate;
	private AuditStatus auditStatus;
	private Date expiresDate;
	private User auditor;
	private Date assignedDate;
	private Date scheduledDate;
	private Date completedDate;
	private Date closedDate;
	private Account requestingOpAccount;
	private String auditLocation;
	private String percentComplete;
	private String percentVerified;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auditID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "auditTypeID")
	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	@ManyToOne
	@JoinColumn(name = "conID")
	public Account getContractorAccount() {
		return contractorAccount;
	}

	public void setContractorAccount(Account contractorAccount) {
		this.contractorAccount = contractorAccount;
	}

	@Temporal(TemporalType.DATE)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Enumerated(EnumType.STRING)
	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "auditorID")
	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	@ManyToOne
	@JoinColumn(name = "requestedByOpID")
	public Account getRequestingOpAccount() {
		return requestingOpAccount;
	}

	public void setRequestingOpAccount(Account requestingOpAccount) {
		this.requestingOpAccount = requestingOpAccount;
	}

	public String getAuditLocation() {
		return auditLocation;
	}

	public void setAuditLocation(String auditLocation) {
		this.auditLocation = auditLocation;
	}

	public String getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	public String getPercentVerified() {
		return percentVerified;
	}

	public void setPercentVerified(String percentVerified) {
		this.percentVerified = percentVerified;
	}
}
