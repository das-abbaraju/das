package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade_alt")
public class TradeAlternate extends BaseTable {

	private Trade trade;
	private String name;

	@ManyToOne
	@JoinColumn(name = "tradeID")
	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}