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

	protected TradeDAO serviceDAO;

	private Trade service;
	private ClassificationType classification = ClassificationType.Master;

	private ListType listType = ListType.Master;

	@SuppressWarnings("unchecked")
	public String json() {

		List<Trade> nodes = new ArrayList<Trade>();

		if (listType == ListType.Master) {
			if (service == null) {
				nodes = serviceDAO.findRoot(ClassificationType.Master);
			} else {
				nodes = serviceDAO.findByParent(service.getId());
			}
		} else if (listType == ListType.Suncor) {
			if (service == null) {
				nodes = serviceDAO.findRoot(ClassificationType.Suncor);
			} else {
				nodes = serviceDAO.findByParent(service.getId());
			}
		} else if (listType == ListType.MasterSuncor) {
			nodes = serviceDAO.findByNode(service);
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

	public String serviceAjax() {

		return "service";
	}

	public void setServiceDAO(TradeDAO serviceDAO) {
		this.serviceDAO = serviceDAO;
	}

	public Trade getService() {
		return service;
	}

	public void setService(Trade service) {
		this.service = service;
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
		Master("Master List"), Suncor("Suncor"), MasterSuncor("Master/Suncor");

		private String description;

		ListType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

}
