package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_criteria_contractor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaContractor extends BaseTable {

	private ContractorAccount contractor;
	private FlagCriteria criteria;
	private String answer;
	private boolean verified;

	public FlagCriteriaContractor() {
	}

	public FlagCriteriaContractor(ContractorAccount ca, FlagCriteria fc, String answer) {
		contractor = ca;
		criteria = fc;
		this.answer = answer;
		setAuditColumns(new User(User.SYSTEM));
	}

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractorAccount) {
		this.contractor = contractorAccount;
	}

	@ManyToOne
	@JoinColumn(name = "criteriaID", nullable = false)
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

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public boolean equals(FlagCriteriaContractor other) {
		if (other.getId() > 0 && id > 0)
			return super.equals(other);
		if (!contractor.equals(other.getContractor()))
			return false;
		if (!criteria.equals(other.getCriteria()))
			return false;
		return true;
	}

	public void update(FlagCriteriaContractor change) {
		if (!answer.equals(change.getAnswer()))
			answer = change.getAnswer();
		if (verified != change.isVerified())
			verified = change.isVerified();
	}
}
