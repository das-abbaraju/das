package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;

public final class TradeAutocompleteService extends AbstractAutocompleteService<Trade> {

	@Autowired
	private TradeDAO tradeDAO;

	@Override
	protected Collection<Trade> getItemsForSearch(String search, Permissions permissions) {
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
		return TranslationServiceFactory.getTranslationService().getText(trade.getI18nKey() + ".name",
				permissions.getLocale());
	}

	@Override
	protected Collection<Trade> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int tradeId = NumberUtils.toInt(searchKey);
		if (tradeId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(tradeDAO.find(tradeId));
	}

}
