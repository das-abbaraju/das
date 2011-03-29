package com.picsauditing.actions.productservices;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ProductServiceDAO;
import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("serial")
public class UpdateProductService extends PicsActionSupport {
	private ProductServiceDAO psDAO;

	private int id;
	private ProductService productService;

	public UpdateProductService(ProductServiceDAO psDAO) {
		this.psDAO = psDAO;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public String execute() throws Exception {
		if (id > 0)
			productService = psDAO.find(id);
		else
			addActionError("Missing Product/Service id");
		// TODO: find productService
		return SUCCESS;
	}

	public String save() throws Exception {
		// TODO: update productService
		return SUCCESS;
	}
}