package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class TradeAutocomplete extends AutocompleteActionSupport<Trade> {
	@Autowired
	private TradeDAO tradeDAO;

	@Override
	protected Collection<Trade> getItems() {
		return tradeDAO.findByIndexValue(q);
	}
}
