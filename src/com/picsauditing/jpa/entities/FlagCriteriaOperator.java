package com.picsauditing.jpa.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flag_criteria_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor flag;
	private String hurdle;
	
	private List<FlagData> data;

	@ManyToOne
	@JoinColumn(name="opID", nullable=false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@ManyToOne
	@JoinColumn(name="criteriaID", nullable=false)
	public FlagCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}

	@Enumerated(EnumType.STRING)
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

	@OneToMany
	@JoinColumns( {
			@JoinColumn(name = "opID", referencedColumnName = "opID", insertable = false, updatable = false),
			@JoinColumn(name = "criteriaID", referencedColumnName = "criteriaID", insertable = false, updatable = false) })
	public List<FlagData> getData() {
		return data;
	}
	
	/**
	 * Determine if a contractor's answer to this criteria should be flagged and
	 * if so, what color. If the contractor criteria is not the same as the
	 * operator criteria, then throw an exception.
	 * 
	 * @param contractorCriteria
	 * @return
	 */
	public FlagColor evaluate(FlagCriteriaContractor contractorCriteria) {

		return null;
	}

}
