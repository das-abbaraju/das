package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_criteria_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor flag = FlagColor.Red;
	private String hurdle;
	private float percentAffected = 0;
	private Date lastCalculated;
	private LowMedHigh minRiskLevel = LowMedHigh.None;

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne
	@JoinColumn(name = "criteriaID", nullable = false)
	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	@Enumerated(EnumType.STRING)
	@JoinColumn(nullable = false)
	public FlagColor getFlag() {
		return flag;
	}

	public void setFlag(FlagColor flag) {
		this.flag = flag;
	}

	public String getHurdle() {
		return hurdle;
	}

	public void setHurdle(String hurdle) {
		this.hurdle = hurdle;
	}

	public float getPercentAffected() {
		return percentAffected;
	}

	public void setPercentAffected(float percentAffected) {
		this.percentAffected = percentAffected;
	}

	public Date getLastCalculated() {
		return lastCalculated;
	}

	public void setLastCalculated(Date lastCalculated) {
		this.lastCalculated = lastCalculated;
	}

	@Enumerated(EnumType.ORDINAL)
	public LowMedHigh getMinRiskLevel() {
		return minRiskLevel;
	}

	public void setMinRiskLevel(LowMedHigh minRiskLevel) {
		this.minRiskLevel = minRiskLevel;
	}

	@Transient
	public boolean isNeedsRecalc() {
		if (lastCalculated != null) {
			Date now = new Date();
			Long diff = now.getTime() - lastCalculated.getTime();

			// Difference is a day?
			if (diff > (long) 60 * 60 * 24 * 1000)
				return true;
			else
				return false;
		}

		return true;
	}

	public String replaceHurdle() {
		String value = criteria.getDefaultValue();

		if (criteria.isAllowCustomValue() && hurdle != null) {
			value = hurdle;
		}

		return criteria.getDescription().replaceAll("\\{HURDLE\\}", value);
	}

	public String criteriaValue() {
		if (criteria.isAllowCustomValue() && hurdle != null)
			return hurdle;

		return criteria.getDefaultValue();
	}

	@Override
	public String toString() {
		return replaceHurdle() + " for " + operator.toString();
	}
}
