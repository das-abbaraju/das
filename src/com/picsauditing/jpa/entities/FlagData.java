package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flag_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagData extends BaseTable {
	
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor flag;
	
	private FlagCriteriaContractor contractorCriteria;
	private FlagCriteriaOperator operatorCriteria;

	@ManyToOne
	@JoinColumn(name="conID", nullable=false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

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

	@ManyToOne
	@JoinColumns( {
		@JoinColumn(name = "opID", referencedColumnName = "opID", insertable = false, updatable = false),
		@JoinColumn(name = "criteriaID", referencedColumnName = "criteriaID", insertable = false, updatable = false) })
	public FlagCriteriaOperator getOperatorCriteria() {
		return operatorCriteria;
	}
	
	public void setOperatorCriteria(FlagCriteriaOperator operatorCriteria) {
		this.operatorCriteria = operatorCriteria;
	}

	@ManyToOne
	@JoinColumns( {
		@JoinColumn(name = "conID", referencedColumnName = "conID", insertable = false, updatable = false),
		@JoinColumn(name = "criteriaID", referencedColumnName = "criteriaID", insertable = false, updatable = false) })
	public FlagCriteriaContractor getContractorCriteria() {
		return contractorCriteria;
	}
	
	public void setContractorCriteria(FlagCriteriaContractor contractorCriteria) {
		this.contractorCriteria = contractorCriteria;
	}
}
