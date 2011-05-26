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
	private String category;

	public TradeAlternate() {
	}

	public TradeAlternate(Trade trade, String name, String category) {
		this.trade = trade;
		this.name = name;
		this.category = category;
	}

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

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TradeAlternate) {
			TradeAlternate tradeAlternate = (TradeAlternate) obj;
			if (tradeAlternate.getTrade().equals(trade) && tradeAlternate.getName().equals(name))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// return Strings.hash(trade.getId() + name);
		return super.hashCode();
	}

}