package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_data_override")
public class FlagDataOverride extends BaseTable {

	private ContractorAccount contractor;
	private OperatorAccount operator;
	private ContractorOperator contractorOperator;
	private FlagCriteria criteria;
	private FlagColor forceflag;
	private Date forceEnd;
	private String year;

	@ManyToOne
	@JoinColumns(
		{ @JoinColumn(name = "opID", referencedColumnName = "genID", insertable=false, updatable=false),
		  @JoinColumn(name = "conID", referencedColumnName = "subID", insertable=false, updatable=false) })
	public ContractorOperator getContractorOperator() {
		return contractorOperator;
	}
	
	public void setContractorOperator(ContractorOperator contractorOperator) {
		this.contractorOperator = contractorOperator;
	}
	
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
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Transient
	public boolean isInForce() {
		if (forceEnd == null)
			return false;
		return forceEnd.after(new Date());
	}
}
