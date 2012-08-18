package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
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
	private Integer ruleID;

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

	/**
	 * If true, then allow for manually setting the isApplies field
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	/**
	 * This is dynamically set by AuditBuilder.fillAuditCategories() when one or
	 * more CAOs require this category. If Override==true, then the value must
	 * be manually set
	 */
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

	public Integer getRuleID() {
		return ruleID;
	}

	public void setRuleID(Integer ruleID) {
		this.ruleID = ruleID;
	}

	@Transient
	public List<AuditQuestion> getEffectiveQuestions() {
		List<AuditQuestion> result = new ArrayList<AuditQuestion>();
		Date effectiveDate = audit.getEffectiveDate();
		if (effectiveDate == null)
			effectiveDate = new Date();
		for (AuditQuestion auditQuestion : category.getQuestions()) {
			if (auditQuestion.isCurrent(effectiveDate)) {
				result.add(auditQuestion);
			}
		}

		return result;
	}

	@Transient
	public String getPrintableScore() {
		if (getScorePossible() > 0) {

			int tempScore = Math.round(score);

			Map<Integer, String> map = new HashMap<Integer, String>() {

				{
					put(0, "Red");
					put(1, "Yellow");
					put(2, "Green");
				}
			};

			return map.get(tempScore);
		} else {
			return "-";
		}
	}
}
