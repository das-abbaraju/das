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

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_criteria_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor flag = FlagColor.Red;
	private String hurdle;
	private int affected = 0;
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

	public int getAffected() {
		return affected;
	}

	public void setAffected(int affected) {
		this.affected = affected;
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
	
	@Transient 
	public String getReplaceHurdle() {
		return criteria.getDescription().replaceAll("\\{HURDLE\\}", criteriaValue());
	}

	@Transient 
	public String criteriaValue() {
		if (criteria.isAllowCustomValue() && hurdle != null) {
			if (criteria.getDataType().equals(FlagCriteria.NUMBER))
				return Strings.formatDecimalComma(hurdle);
			
			return hurdle;
		}

		return criteria.getDefaultValue();
	}

	@Override
	public String toString() {
		return getReplaceHurdle() + " for " + operator.toString();
	}
}
