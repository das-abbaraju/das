package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_operator_workflow")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorAuditOperatorWorkflow extends BaseTable {

	private int caoID;
	private AuditStatus status;
	private AuditStatus previousStatus;
	private String notes;

	@ManyToOne
	@JoinColumn(name = "caoID", nullable = false)
	public int getCaoID() {
		return caoID;
	}

	public void setCaoID(int caoID) {
		this.caoID = caoID;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	public AuditStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(AuditStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	@Column(length = 1000)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
