package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.ContractorTrade")
@Table(name = "contractor_trade")
public class ContractorTrade extends BaseTable {
	private ContractorAccount contractor;
	private Trade trade;
	private boolean selfPerformed = true;
//	private boolean manufacture = true;
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

//	@ReportField(importance = FieldImportance.Average, type = FieldType.Boolean)
//	public boolean isManufacture() {
//		return manufacture;
//	}
//
//	public void setManufacture(boolean manufacture) {
//		this.manufacture = manufacture;
//	}
//
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

//	@Override
//	public int compareTo(ContractorTrade o) {
//		if (o == null)
//			throw new NullPointerException();
//		return getTrade().getName().toString().compareTo(o.getTrade().getName().toString());
//	}
//
//	@Transient
//	public int getPercentOfTotal() {
//		Set<ContractorTrade> trades = contractor.getTrades();
//		float total = 0;
//		for (ContractorTrade c : trades) {
//			total += c.getActivityPercent();
//		}
//		DecimalFormat formatter = new DecimalFormat("###");
//		return Integer.parseInt(formatter.format(activityPercent / total * 100));
//	}
}
