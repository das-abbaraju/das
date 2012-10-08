package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
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
			return tradeDAO.findByIndexValue(search, RESULT_SET_LIMIT);
		}

		return Collections.emptyList();
	}

	@Override
	protected Object getKey(Trade trade) {
		return trade.getId();
	}

	@Override
	protected Object getValue(Trade trade, Permissions permissions) {
		return I18nCache.getInstance().getText(trade.getI18nKey() + ".name", permissions.getLocale());
	}
}
