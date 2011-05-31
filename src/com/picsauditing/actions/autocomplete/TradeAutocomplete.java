package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public final class TradeAutocomplete extends AutocompleteActionSupport<Trade> {
	@Autowired
	private TradeDAO tradeDAO;
	private boolean restrictTrades;

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Trade> getItems() {
		if (itemKeys == null) {
			if (!Strings.isEmpty(q))
				if (restrictTrades) {
					List<Trade> tradeList = tradeDAO.findByIndexValue(q, limit);
					Iterator<Trade> itr = tradeList.iterator();
					while (itr.hasNext()) {
						Trade t = itr.next();
						if (!t.isSelectable() || t.getContractorCount() == 0)
							itr.remove();
					}
					return tradeList;
				} else {
					return tradeDAO.findByIndexValue(q, limit);
				}
		} else if (itemKeys.length > 0) {
			if (restrictTrades)
				return (List<Trade>) tradeDAO.findWhere(Trade.class, "t.id = " + Strings.implode(itemKeys) + " AND t.selectable = 1 AND t.contractorCount > 0", 0);
			else
				return (List<Trade>) tradeDAO.findWhere(Trade.class, "t.id = " + Strings.implode(itemKeys), 0);
		}
		return Collections.emptyList();
	}
	
}
