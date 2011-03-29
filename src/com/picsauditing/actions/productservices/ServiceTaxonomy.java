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
	private ClassificationType classification = ClassificationType.Master;

	@SuppressWarnings("unchecked")
	public String json() {

		final List<ProductService> nodes;

		System.out.println(classification);

		if (service == null) {
			nodes = serviceDAO.findRoot(ClassificationType.Master);
		} else {
			nodes = serviceDAO.findByParent(service.getId());
		}

		JSONArray result = new JSONArray();
		for (ProductService productService : nodes) {
			JSONObject o = new JSONObject();
			o.put("data",
					String.format("[%s] %s", productService.getClassificationCode(), productService.getDescription()));

			if (!productService.isLeaf()) {
				o.put("state", "closed");
			}

			JSONObject attr = new JSONObject();
			attr.put("id", productService.getId());
			attr.put("rel", productService.getClassificationType().toString());
			attr.put("class", "Master");
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

	public ClassificationType getClassification() {
		return classification;
	}

	public void setClassification(ClassificationType classification) {
		this.classification = classification;
	}

}
