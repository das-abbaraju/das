package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

public final class TradeAutocompleteService extends AutocompleteService<Trade> {
	@Autowired
	private TradeDAO tradeDAO;

	@Override
	protected Collection<Trade> getItems(String q) {
		if (!Strings.isEmpty(q)) {
			return tradeDAO.findByIndexValue(q, 20);
		}

		return Collections.emptyList();
	}
}
