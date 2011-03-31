package com.picsauditing.actions.trades;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class UpdateTrade extends PicsActionSupport {
	private TradeDAO psDAO;

	private int id;
	private Trade trade;

	public UpdateTrade(TradeDAO psDAO) {
		this.psDAO = psDAO;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTrader(Trade trade) {
		this.trade = trade;
	}

	public String execute() throws Exception {
		if (id > 0)
			trade = psDAO.find(id);
		else
			addActionError("Missing Product/Service id");
		// TODO: find trade
		return SUCCESS;
	}

	public String save() throws Exception {
		// TODO: update trade
		return SUCCESS;
	}
}