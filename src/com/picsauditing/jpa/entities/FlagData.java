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
@Table(name = "flag_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagData extends BaseTable {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor flag;

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

	public boolean equals(FlagData other) {
		if (id > 0 && other.getId() > 0)
			return super.equals(other);

		if (!contractor.equals(other.getContractor()))
			return false;
		if (!operator.equals(other.getOperator()))
			return false;
		if (!criteria.equals(other.getCriteria()))
			return false;
		return true;
	}

	public void update(FlagData change) {
		if (!equals(change))
			// Don't update flag data for the wrong contractor/operator/criteria
			return;

		if (!flag.equals(change.getFlag())) {
			this.setFlag(change.getFlag());
			this.setAuditColumns(new User(User.SYSTEM));
		}
	}
}
