package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

public final class TradeAutocompleteService extends AbstractAutocompleteService<Trade> {
	
	@Autowired
	private TradeDAO tradeDAO;

	@Override
	protected Collection<Trade> getItems(String search, Permissions permissions) {
		if (!Strings.isEmpty(search)) {
			return tradeDAO.findByIndexValue(search, 20);
		}

		return Collections.emptyList();
	}

	@Override
	protected Object getAutocompleteItem(Trade trade) {
		return trade.getAutocompleteItem();
	}

	@Override
	protected Object getAutocompleteValue(Trade trade) {
		return trade.getAutocompleteValue();
	}
}
