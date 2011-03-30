package com.picsauditing.actions.autocomplete;

import com.picsauditing.dao.ProductServiceDAO;
import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("serial")
public class ProductServiceAutocomplete extends AutocompleteActionSupport<ProductService> {
	private ProductServiceDAO psDAO;

	public ProductServiceAutocomplete(ProductServiceDAO psDAO) {
		this.psDAO = psDAO;
	}

	@Override
	protected void findItems() {
		items = psDAO.findWhere("p.classificationCode LIKE '" + q + "%' OR p.description LIKE '%" + q + "%'");
	}
	
	@Override
	protected String createOutputAutocomplete(ProductService item){
		return item.toString() + "\n";
	}
}
