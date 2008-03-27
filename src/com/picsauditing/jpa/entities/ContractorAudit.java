package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contractor_audit")
public class ContractorAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auditID", nullable = false)
	private int id = 0;

	private int auditTypeID;
	private int conID;
	private Date createdDate;
	private String auditStatus;
	private Date expiresDate;
	@Enumerated(EnumType.ORDINAL)
	private int auditorID;
	private Date assignedDate;
	private Date scheduledDate;
	private Date completedDate;
	private Date closedDate;
	private int requestedByOpID;
	private String auditLocation;
	private String percentComplete;
	private String percentVerified;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	public int getAuditorID() {
		return auditorID;
	}

	public void setAuditorID(int auditorID) {
		this.auditorID = auditorID;
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

	public int getRequestedByOpID() {
		return requestedByOpID;
	}

	public void setRequestedByOpID(int requestedByOpID) {
		this.requestedByOpID = requestedByOpID;
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
