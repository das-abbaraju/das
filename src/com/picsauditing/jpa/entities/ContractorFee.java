package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_fee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorFee extends BaseTable {
	private ContractorAccount contractor;
	private FeeClass feeClass;
	private InvoiceFee currentLevel;
	private InvoiceFee newLevel;

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public FeeClass getFeeClass() {
		return feeClass;
	}

	public void setFeeClass(FeeClass feeClass) {
		this.feeClass = feeClass;
	}

	public InvoiceFee getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(InvoiceFee currentLevel) {
		this.currentLevel = currentLevel;
	}

	public InvoiceFee getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(InvoiceFee newLevel) {
		this.newLevel = newLevel;
	}

}
