package com.picsauditing.jpa.entities;

import java.math.BigDecimal;

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
@Table(name = "contractor_fee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "temp")
public class ContractorFee extends BaseTable {
	private ContractorAccount contractor;
	private FeeClass feeClass;
	private InvoiceFee currentLevel;
	private BigDecimal currentAmount = BigDecimal.ZERO;
	private InvoiceFee newLevel;
	private BigDecimal newAmount = BigDecimal.ZERO;

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@Enumerated(EnumType.STRING)
	@JoinColumn(name = "feeClass", nullable = false)
	public FeeClass getFeeClass() {
		return feeClass;
	}

	public void setFeeClass(FeeClass feeClass) {
		this.feeClass = feeClass;
	}

	@ManyToOne
	@JoinColumn(name = "currentLevel", nullable = false)
	public InvoiceFee getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(InvoiceFee currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void setCurrentAmount(BigDecimal currentAmount) {
		this.currentAmount = currentAmount;
	}

	public BigDecimal getCurrentAmount() {
		return currentAmount;
	}

	@ManyToOne
	@JoinColumn(name = "newLevel", nullable = false)
	public InvoiceFee getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(InvoiceFee newLevel) {
		this.newLevel = newLevel;
	}

	public void setNewAmount(BigDecimal newAmount) {
		this.newAmount = newAmount;
	}

	public BigDecimal getNewAmount() {
		return newAmount;
	}

	@Transient
	public boolean isUpgrade() {
		return this.getNewAmount().compareTo(this.getCurrentAmount()) > 0;
	}

	@Transient
	public boolean isHasChanged() {
		return !this.getNewLevel().equals(this.getCurrentLevel());
	}
}
