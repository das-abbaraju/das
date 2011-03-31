package com.picsauditing.actions.trades;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class TradeTaxonomy extends PicsActionSupport {

	protected TradeDAO tradeDAO;
	private Trade trade;

	@SuppressWarnings("unchecked")
	public String json() {

		List<Trade> nodes = new ArrayList<Trade>();

		if (trade == null) {
			nodes = tradeDAO.findWhere("p.parent IS NULL");
		} else {
			nodes = tradeDAO.findByParent(trade.getId());
		}

		JSONArray result = new JSONArray();
		for (Trade trade : nodes) {
			JSONObject o = new JSONObject();
			o.put("data", getText(trade.getI18nKey("name")));

			if (!trade.isLeaf()) {
				o.put("state", "closed");
			}

			JSONObject data = new JSONObject();
			data.put("id", trade.getId());
			o.put("metadata", data);

			result.add(o);
		}

		json.put("result", result);

		return JSON;
	}
	
	@Anonymous
	public String index() throws Exception {
		indexNode(null, 1);
		return SUCCESS;
	}

	private int indexNode(Trade parent, int counter) {
		List<Trade> childNodes;
		int level = 1;
		if (parent == null) {
			childNodes = tradeDAO.findWhere("p.parent IS NULL");
		} else {
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

	public String saveTradeAjax() throws Exception {
		if (trade != null) {
			System.out.println(trade.getName());
		}
		
		return "trade";
	}

	public String tradeAjax() {
		return "trade";
	}

	public void setTradeDAO(TradeDAO tradeDAO) {
		this.tradeDAO = tradeDAO;
	}

	public Trade getTrade() {
		if (trade != null && trade.getName() == null) {
			trade.setName(getText(trade.getI18nKey("name")));
//			String name2 = getText(trade.getI18nKey("name2"), "");
//			if (Strings.isEmpty(name2))
//				name2 = trade.getName();
//			trade.setName2(name2);
		}
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

}
