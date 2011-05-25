package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public final class TradeAutocomplete extends AutocompleteActionSupport<Trade> {
	@Autowired
	private TradeDAO tradeDAO;

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Trade> getItems() {
		if (itemKeys == null) {
			if (!Strings.isEmpty(q))
				return tradeDAO.findByIndexValue(q, limit);
		} else if (itemKeys.length > 0) {
			return (List<Trade>) tradeDAO.findWhere(Trade.class, "t.id = " + Strings.implode(itemKeys), 0);
		}
		return Collections.emptyList();
	}
}
