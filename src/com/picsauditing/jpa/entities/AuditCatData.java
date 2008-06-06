package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "pqfcatdata")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="temp")
public class AuditCatData implements java.io.Serializable {
	private int id;
	private AuditCategory category;
	private ContractorAudit audit;
	private YesNo applies = YesNo.Yes;
	private int percentCompleted = 0;
	private int percentVerified = 0;
	private int percentClosed = 0;
	private int requiredCompleted = 0;
	private int numRequired = 0;
	private int numAnswered = 0;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "catDataID", nullable = false, insertable=false, updatable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public YesNo getApplies() {
		return applies;
	}

	@Transient
	public boolean isAppliesB() {
		if (audit.getAuditType().getAuditTypeID() > 2)
			return true;
		return applies.equals(YesNo.Yes);
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
