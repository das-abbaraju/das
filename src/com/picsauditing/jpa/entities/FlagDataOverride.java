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

@Entity
@Table(name = "flag_data_override")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagDataOverride extends BaseTable {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private FlagCriteria criteria;
	private FlagColor forceflag;
	private Date forceEnd;

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
	public FlagColor getForceflag() {
		return forceflag;
	}

	public void setForceflag(FlagColor forceflag) {
		this.forceflag = forceflag;
	}

	public Date getForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(Date forceEnd) {
		this.forceEnd = forceEnd;
	}
	
	@Transient
	public boolean isInForce() {
		if (forceEnd == null)
			return false;
		return forceEnd.after(new Date());
	}
}
