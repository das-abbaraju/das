package com.picsauditing.actions.productservices;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ProductServiceDAO;
import com.picsauditing.jpa.entities.ClassificationType;
import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("serial")
public class ServiceTaxonomy extends PicsActionSupport {

	protected ProductServiceDAO serviceDAO;

	private ProductService service;
	private ClassificationType classification = ClassificationType.Master;

	private ListType listType = ListType.Master;

	@SuppressWarnings("unchecked")
	public String json() {

		List<ProductService> nodes = new ArrayList<ProductService>();

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
		for (ProductService productService : nodes) {
			JSONObject o = new JSONObject();
			o.put("data", productService.getDescription());

			if (!productService.isLeaf()) {
				o.put("state", "closed");
			}

			JSONObject attr = new JSONObject();
			attr.put("id", productService.getId());
			attr.put("rel", productService.getClassificationType().toString());
			attr.put("class", "Master");
			o.put("attr", attr);

			JSONObject data = new JSONObject();
			data.put("id", productService.getId());
			o.put("metadata", data);

			result.add(o);
		}

		json.put("result", result);

		return JSON;
	}

	public String serviceAjax() {

		return "service";
	}

	public void setServiceDAO(ProductServiceDAO serviceDAO) {
		this.serviceDAO = serviceDAO;
	}

	public ProductService getService() {
		return service;
	}

	public void setService(ProductService service) {
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
