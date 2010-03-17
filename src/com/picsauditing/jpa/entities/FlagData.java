package com.picsauditing.jpa.entities;

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
@Table(name = "flag_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagData extends BaseTable implements Comparable<FlagData> {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor flag;
	private FlagCriteriaContractor criteriaContractor;

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

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
	public FlagColor getFlag() {
		return flag;
	}

	public void setFlag(FlagColor flag) {
		this.flag = flag;
	}

	@Transient
	public FlagCriteriaContractor getCriteriaContractor() {
		return criteriaContractor;
	}

	public void setCriteriaContractor(FlagCriteriaContractor criteriaContractor) {
		this.criteriaContractor = criteriaContractor;
	}

	@Override
	public boolean equals(Object other) {
		FlagData fd = (FlagData) other;

		if (id > 0 && fd.getId() > 0)
			return super.equals(other);

		if (!contractor.equals(fd.getContractor()))
			return false;
		if (!operator.equals(fd.getOperator()))
			return false;
		if (!criteria.equals(fd.getCriteria()))
			return false;
		return true;
	}

	@Override
	public void update(BaseTable change) {
		FlagData fd = (FlagData) change;
		if (!equals(change))
			// Don't update flag data for the wrong contractor/operator/criteria
			return;

		if (!flag.equals(fd.getFlag())) {
			this.setFlag(fd.getFlag());
			this.setAuditColumns(new User(User.SYSTEM));
		}
	}

	@Override
	public int compareTo(FlagData o) {
		return criteria.compareTo(o.criteria);
	}
}
