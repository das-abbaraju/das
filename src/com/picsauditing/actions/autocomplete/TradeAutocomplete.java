package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class TradeAutocomplete extends AutocompleteActionSupport<Trade> {
	@Autowired
	private TradeDAO tradeDAO;

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Trade> getItems() {
		if(ids == null)
			return tradeDAO.findByIndexValue(q);
		else
			return (List<Trade>)tradeDAO.findWhere(Trade.class, Strings.implode(ids), 0);
	}
}
