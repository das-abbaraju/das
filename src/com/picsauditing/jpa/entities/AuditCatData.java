package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "pqfcatdata")
public class AuditCatData implements java.io.Serializable {
	private int id;
	private AuditCategory category;
	private ContractorAudit audit;
	private YesNo applies = YesNo.No;
	private int percentCompleted;
	private int percentVerified;
	private int percentClosed;
	private int requiredCompleted;
	private int numRequired;
	private int numAnswered;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "catDataID", nullable = false, insertable=false, updatable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "catID", nullable = false)
	public AuditCategory getCategory() {
		return category;
	}

	public void setCategory(AuditCategory category) {
		this.category = category;
	}

	@ManyToOne
	@JoinColumn(name = "auditID", nullable = false)
	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	@Column(nullable = false)
	public YesNo getApplies() {
		return applies;
	}

	public void setApplies(YesNo applies) {
		this.applies = applies;
	}

	public int getPercentCompleted() {
		return percentCompleted;
	}

	public void setPercentCompleted(int percentCompleted) {
		this.percentCompleted = percentCompleted;
	}

	public int getPercentVerified() {
		return percentVerified;
	}

	public void setPercentVerified(int percentVerified) {
		this.percentVerified = percentVerified;
	}

	public int getPercentClosed() {
		return percentClosed;
	}

	public void setPercentClosed(int percentClosed) {
		this.percentClosed = percentClosed;
	}

	public int getRequiredCompleted() {
		return requiredCompleted;
	}

	public void setRequiredCompleted(int requiredCompleted) {
		this.requiredCompleted = requiredCompleted;
	}

	public int getNumRequired() {
		return numRequired;
	}

	public void setNumRequired(int numRequired) {
		this.numRequired = numRequired;
	}

	public int getNumAnswered() {
		return numAnswered;
	}

	public void setNumAnswered(int numAnswered) {
		this.numAnswered = numAnswered;
	}

}
