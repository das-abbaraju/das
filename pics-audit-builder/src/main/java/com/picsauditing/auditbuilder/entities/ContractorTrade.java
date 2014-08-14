package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorTrade")
@Table(name = "contractor_trade")
public class ContractorTrade extends BaseTable {
    private ContractorAccount contractor;

	private Trade trade;

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

}