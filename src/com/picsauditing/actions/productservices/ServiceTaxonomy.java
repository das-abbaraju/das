package com.picsauditing.actions.productservices;

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
	private Integer parentID = null;

	@SuppressWarnings("unchecked")
	public String json() {

		final List<ProductService> nodes;

		if (parentID == null) {
			nodes = serviceDAO.findRoot(ClassificationType.Master);
		} else {
			nodes = serviceDAO.findByParent(parentID);
		}

		JSONArray result = new JSONArray();
		for (ProductService productService : nodes) {
			JSONObject o = new JSONObject();
			o.put("data", String.format("[%s] %s", productService.getClassificationCode(), productService
					.getDescription()));
			o.put("state", "closed");

			JSONObject attr = new JSONObject();
			attr.put("id", productService.getId());
			attr.put("rel", productService.getClassificationType());
			o.put("attr", attr);

			result.add(o);
		}

		json.put("result", result);

		return JSON;
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

	public Integer getParentID() {
		return parentID;
	}

	public void setParentID(Integer parentID) {
		this.parentID = parentID;
	}
}
