package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_trade")
public class ContractorTrade extends BaseTable {
	private ContractorAccount contractor;
	private Trade trade;
	private boolean selfPerformed;
	private boolean manufacture;
	private int activityPercent;

	@OneToOne
	@JoinColumn(name = "conID")
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@OneToOne
	@JoinColumn(name = "tradeID")
	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public boolean isSelfPerformed() {
		return selfPerformed;
	}

	public void setSelfPerformed(boolean selfPerformed) {
		this.selfPerformed = selfPerformed;
	}

	public boolean isManufacture() {
		return manufacture;
	}

	public void setManufacture(boolean manufacture) {
		this.manufacture = manufacture;
	}

	public int getActivityPercent() {
		return activityPercent;
	}

	public void setActivityPercent(int activityPercent) {
		this.activityPercent = activityPercent;
	}
}
