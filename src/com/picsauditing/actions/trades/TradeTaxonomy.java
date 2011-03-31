package com.picsauditing.actions.trades;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.ClassificationType;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class TradeTaxonomy extends PicsActionSupport {

	protected TradeDAO tradeDAO;

	private Trade trade;
	private ClassificationType classification = ClassificationType.Master;

	private ListType listType = ListType.Master;

	@SuppressWarnings("unchecked")
	public String json() {

		List<Trade> nodes = new ArrayList<Trade>();

		if (listType == ListType.Master) {
			if (trade == null) {
				nodes = tradeDAO.findRoot(ClassificationType.Master);
			} else {
				nodes = tradeDAO.findByParent(trade.getId());
			}
		} else if (listType == ListType.Suncor) {
			if (trade == null) {
				nodes = tradeDAO.findRoot(ClassificationType.Suncor);
			} else {
				nodes = tradeDAO.findByParent(trade.getId());
			}
		} else if (listType == ListType.MasterSuncor) {
			nodes = tradeDAO.findByNode(trade);
		}

		JSONArray result = new JSONArray();
		for (Trade trade : nodes) {
			JSONObject o = new JSONObject();
			o.put("data", trade.getDescription());

			if (!trade.isLeaf()) {
				o.put("state", "closed");
			}

			JSONObject attr = new JSONObject();
			attr.put("id", trade.getId());
			attr.put("rel", trade.getClassificationType().toString());
			attr.put("class", "Master");
			o.put("attr", attr);

			JSONObject data = new JSONObject();
			data.put("id", trade.getId());
			o.put("metadata", data);

			result.add(o);
		}

		json.put("result", result);

		return JSON;
	}

	public String tradeAjax() {

		return "trade";
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

	public ClassificationType getClassification() {
		return classification;
	}

	public void setClassification(ClassificationType classification) {
		this.classification = classification;
	}

	public ListType getListType() {
		return listType;
	}

	public void setListType(ListType listType) {
		this.listType = listType;
	}

	public ListType[] getListTypes() {
		return ListType.values();
	}

	enum ListType {
		Master("Master List"),
		Suncor("Suncor"),
		MasterSuncor("Master/Suncor");

		private String description;

		ListType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

}
