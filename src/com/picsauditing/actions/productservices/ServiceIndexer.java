package com.picsauditing.actions.productservices;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ProductServiceDAO;
import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("serial")
public class ServiceIndexer extends PicsActionSupport {

	private ProductServiceDAO productServiceDAO;

	public String index() throws Exception {
		// s:button ServiceIndexer!index
		index(null, 0, Integer.MAX_VALUE);
		return SUCCESS;
	}

	private void index(ProductService parent, int indexStart, int indexEnd) {
		List<ProductService> childNodes;
		int level = 1;
		if (parent == null)
			childNodes = productServiceDAO.findRoot();
		else {
			level = parent.getIndexLevel() + 1;
			childNodes = productServiceDAO.findByParent(parent.getId());
		}

		int size = childNodes.size();
		if (size == 0)
			return;

		int gap = (int) Math.floor((indexEnd - indexStart) / size);
		int childStart = indexStart;
		int childEnd = childStart + gap - 1;

		for (ProductService node : childNodes) {
			node.setIndexLevel(level);
			node.setIndexStart(childStart);
			node.setIndexEnd(childEnd);
			childStart += gap;
			childEnd += gap;
			index(node, childStart + 1, childEnd - 1);
		}
	}

	public void setProductServiceDAO(ProductServiceDAO productServiceDAO) {
		this.productServiceDAO = productServiceDAO;
	}
}
