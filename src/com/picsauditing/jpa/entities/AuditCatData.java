package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_cat_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class AuditCatData extends BaseTable implements java.io.Serializable {

	private ContractorAudit audit;
	private AuditCategory category;
	private int requiredCompleted = 0;
	private int numRequired = 0;
	private int numAnswered = 0;
	private boolean override = false;
	private boolean applies = true;
	private int percentCompleted = 0;
	private int percentVerified = 0;
	private int percentClosed = 0;
	private float score = 0;
	private int scoreCount = 0;

	@ManyToOne(fetch = FetchType.EAGER)
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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getScoreCount() {
		return scoreCount;
	}

	public void setScoreCount(int scoreCount) {
		this.scoreCount = scoreCount;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Transient
	public String getPrintableScore() {

		if (getScoreCount() > 0) {

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
