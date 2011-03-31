package com.picsauditing.actions.trades;

import java.util.List;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.ClassificationType;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class TradeIndexer extends PicsActionSupport {

	private TradeDAO tradeDAO;

	public void setTradeDAO(TradeDAO tradeDAO) {
		this.tradeDAO = tradeDAO;
	}

	@Anonymous
	public String index() throws Exception {
		// s:button ServiceIndexer!index
		indexNode(null, 1);
		return SUCCESS;
	}

	private int indexNode(Trade parent, int counter) {
		List<Trade> childNodes;
		int level = 1;
		if (parent == null) {
			// System.out.println("Starting Indexer");
			childNodes = tradeDAO.findRoot(ClassificationType.Master);
		} else {
			// System.out.println("Indexing " + parent.getId());
			level = parent.getIndexLevel() + 1;
			counter = parent.getIndexStart();
			childNodes = tradeDAO.findByParent(parent.getId());
		}

		int size = childNodes.size();
		if (size == 0)
			return counter;

		for (Trade node : childNodes) {
			counter++;
			node.setIndexLevel(level);
			node.setIndexStart(counter);
			counter = indexNode(node, counter);
			counter++;
			node.setIndexEnd(counter);
		}

		return counter;
	}
}
