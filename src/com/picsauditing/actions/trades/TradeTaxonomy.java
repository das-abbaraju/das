package com.picsauditing.actions.trades;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.TradeAlternateDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.jpa.entities.TradeAlternate;
import com.picsauditing.util.Strings;
import com.picsauditing.util.Tree;

@SuppressWarnings("serial")
public class TradeTaxonomy extends PicsActionSupport {

	protected TradeDAO tradeDAO;
	protected TradeAlternateDAO tradeAlternateDAO;
	private Trade trade;

	private List<Trade> trades;
	private String alternateName;
	private TradeAlternate alternate;

	private String q;

	@SuppressWarnings("unchecked")
	public String json() {

		List<Trade> nodes = new ArrayList<Trade>();

		if (!Strings.isEmpty(q)) {
			Tree<Trade> tradeTree = tradeDAO.findByIndexValue(q);
			JSONArray value = (JSONArray) tradeTree.toJSON(true).get("children");
			if (value.size() == 0) {
				// TODO: Translate this field
				value.add("No Results :(");
			}
			json.put("result", value);
		} else {
			if (trade == null) {
				nodes = tradeDAO.findWhere("p.parent IS NULL");
			} else {
				nodes = tradeDAO.findByParent(trade.getId());
			}

			JSONArray result = new JSONArray();
			for (Trade trade : nodes) {
				result.add(trade.toJSON());
			}
			json.put("result", result);
		}

		return JSON;
	}

	public String treeJson() {

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
			trade.setAuditColumns(permissions);
			tradeDAO.save(trade);
		}

		return "trade";
	}

	@SuppressWarnings("unchecked")
	public String deleteTradeAjax() throws Exception {
		if (trades.size() > 0) {
			for (Trade t : trades) {
				Trade parent = t.getParent();
				for (Trade child : t.getChildren()) {
					child.setParent(parent);
					tradeDAO.save(child);
				}
				tradeDAO.remove(t);
			}
		}

		json = new JSONObject();
		json.put("success", true);

		return JSON;
	}

	public String tradeAjax() {
		if (trade == null)
			trade = new Trade();
		return "trade";
	}

	@SuppressWarnings("unchecked")
	public String moveTradeJson() {
		json = new JSONObject();
		try {
			for (Trade t : trades) {
				t.setParent(trade);
				tradeDAO.save(t);
			}
			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
		}
		return JSON;
	}

	public String addAlternateAjax() {
		if (alternateName == null || alternateName.equals("")) {
			addActionError("Alternate Name cannot be blank.");
			return "alternate";
		}

		TradeAlternate tradeAlternate = new TradeAlternate(trade, alternateName);
		if (trade.getAlternates().contains(tradeAlternate))
			addActionError("Alternate Already Exists.");
		else {
			trade.getAlternates().add(tradeAlternate);
			tradeDAO.save(trade);
		}

		return "alternate";
	}

	public String removeAlternateAjax() {
		tradeAlternateDAO.remove(alternate);
		return "alternate";
	}

	public void setTradeDAO(TradeDAO tradeDAO) {
		this.tradeDAO = tradeDAO;
	}

	public Trade getTrade() {
		return trade;
	}

	public void setTrade(Trade trade) {
		this.trade = trade;
	}

	public List<Trade> getTrades() {
		return trades;
	}

	public void setTrades(List<Trade> trades) {
		this.trades = trades;
	}

	public String getAlternateName() {
		return alternateName;
	}

	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}

	public TradeAlternate getAlternate() {
		return alternate;
	}

	public void setAlternate(TradeAlternate alternate) {
		this.alternate = alternate;
	}

	public void setTradeAlternateDAO(TradeAlternateDAO tradeAlternateDAO) {
		this.tradeAlternateDAO = tradeAlternateDAO;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

}
