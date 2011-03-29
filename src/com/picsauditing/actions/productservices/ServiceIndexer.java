package com.picsauditing.actions.productservices;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ProductServiceDAO;
import com.picsauditing.jpa.entities.ClassificationType;
import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("serial")
public class ServiceIndexer extends PicsActionSupport {

	private ProductServiceDAO productServiceDAO;

	public void setProductServiceDAO(ProductServiceDAO productServiceDAO) {
		this.productServiceDAO = productServiceDAO;
	}

	public String index() throws Exception {
		// s:button ServiceIndexer!index
		indexNode(null, 1);
		return SUCCESS;
	}

	private int indexNode(ProductService parent, int counter) {
		List<ProductService> childNodes;
		int level = 1;
		if (parent == null) {
			// System.out.println("Starting Indexer");
			childNodes = productServiceDAO.findRoot(ClassificationType.Master);
		} else {
			// System.out.println("Indexing " + parent.getId());
			level = parent.getIndexLevel() + 1;
			counter = parent.getIndexStart();
			childNodes = productServiceDAO.findByParent(parent.getId());
		}

		int size = childNodes.size();
		if (size == 0)
			return counter;

		for (ProductService node : childNodes) {
			counter++;
			node.setIndexLevel(level);
			node.setIndexStart(counter);
			counter = indexNode(node, counter);
			counter++;
			node.setIndexEnd(counter);
		}
		
		return counter;
	}
}
