package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flag_criteria_contractor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaContractor extends BaseTable {
	
	private ContractorAccount contractorAccount;
	private FlagCriteria criteria;
	private String answer;
	
	@ManyToOne
	@JoinColumn(name="conID", nullable=false)
	public ContractorAccount getContractorAccount() {
		return contractorAccount;
	}
	public void setContractorAccount(ContractorAccount contractorAccount) {
		this.contractorAccount = contractorAccount;
	}
	
	@ManyToOne
	@JoinColumn(name="criteriaID", nullable=false)
	public FlagCriteria getCriteria() {
		return criteria;
	}
	public void setCriteria(FlagCriteria criteria) {
		this.criteria = criteria;
	}
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
