package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorTrade")
@Table(name = "contractor_trade")
public class ContractorTrade extends BaseTable {
	private ContractorAccount contractor;
	private Trade trade;
	private boolean selfPerformed = true;
	private int activityPercent = 5;

	@ManyToOne
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

	/**
	 * Number 1-9 that represents the frequency of that trade for a given business
	 * @return
	 */
	public int getActivityPercent() {
		return activityPercent;
	}

	public void setActivityPercent(int activityPercent) {
		this.activityPercent = activityPercent;
	}
}
