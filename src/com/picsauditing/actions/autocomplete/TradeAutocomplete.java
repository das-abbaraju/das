package com.picsauditing.actions.autocomplete;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class TradeAutocomplete extends AutocompleteActionSupport<Trade> {
	@Autowired
	private TradeDAO tradeDAO;

	
	public void findItems() {
		items = tradeDAO.findByIndexValue(q);
	}
	
}
