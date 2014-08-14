package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.AuditCatData")
@Table(name = "audit_cat_data")
public class AuditCatData extends BaseTable implements java.io.Serializable {

	private ContractorAudit audit;
	private AuditCategory category;
	private int requiredCompleted = 0;
	private int numRequired = 0;
	private int numAnswered = 0;
	private int numVerified = 0;
	private boolean override = false;
	private boolean applies = true;
	private float score = 0f;
	private float scorePossible = 0f;

	@ManyToOne
	@JoinColumn(name = "categoryID", nullable = false)
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

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	@Column(nullable = false)
	public boolean isApplies() {
		return applies;
	}

	public void setApplies(boolean applies) {
		this.applies = applies;
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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getScorePossible() {
		return scorePossible;
	}

	public void setScorePossible(float scoreCount) {
		this.scorePossible = scoreCount;
	}

	public int getNumVerified() {
		return numVerified;
	}

	public void setNumVerified(int numVerified) {
		this.numVerified = numVerified;
	}
}