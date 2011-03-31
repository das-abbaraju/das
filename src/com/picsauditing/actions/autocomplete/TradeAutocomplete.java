package com.picsauditing.actions.autocomplete;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class TradeAutocomplete extends AutocompleteActionSupport<Trade> {
	private TradeDAO psDAO;

	public TradeAutocomplete(TradeDAO psDAO) {
		this.psDAO = psDAO;
	}

	@Override
	protected void findItems() {
		items = psDAO.findWhere("p.classificationType = 'Master' AND (p.classificationCode LIKE '" + q + "%' OR p.description LIKE '%" + q + "%')");
	}
	
	@Override
	protected String createOutputAutocomplete(Trade item){
		return item.toString() + "\n";
	}
}
