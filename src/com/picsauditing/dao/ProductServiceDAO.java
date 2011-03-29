package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ClassificationType;
import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("unchecked")
public class ProductServiceDAO extends PicsDAO {

	public ProductService find(int id) {
		ProductService a = em.find(ProductService.class, id);
		return a;
	}

	public List<ProductService> findByParent(int productID) {
		Query query = em.createQuery("SELECT p FROM ProductService p WHERE p.parent.id =  ?");
		query.setParameter(1, productID);
		return query.getResultList();
	}

	public List<ProductService> findByBestMatch(int productID) {
		Query query = em.createQuery("SELECT p FROM ProductService p WHERE p.bestMatch.id =  ?");
		query.setParameter(1, productID);
		return query.getResultList();
	}

	public List<ProductService> findRoot(ClassificationType type) {
		Query query = em.createQuery("SELECT p FROM ProductService p "
				+ "WHERE p.parent IS NULL AND p.classificationType = ?");
		query.setParameter(1, type);
		return query.getResultList();
	}

	public List<ProductService> findWhere(String where) {
		if (where == null)
			where = "";
		else
			where = " WHERE " + where;
		
		Query query = em.createQuery("SELECT p FROM ProductService p" + where);
		query.setMaxResults(100);
		return query.getResultList();
	}
	
	public List<ProductService> findByNode(ProductService productService) {
		String parentString = "";
		if (productService == null)
			parentString = "is null";
		else
			parentString = "= " + productService.getParent().getId();
		
		String sql = "SELECT distinct t0.* " +
					"FROM ref_product_service t0 " +
					"JOIN ref_product_service t1 " +
					"ON t1.classificationType = t0.classificationType AND t0.indexStart <= t1.indexStart AND t0.indexEnd >= t1.indexEnd " +
					"JOIN ref_product_service t2 " +
					"ON t1.id = t2.bestMatchID AND t2.classificationType = 'Suncor' " +
					"WHERE t0.classificationType = 'Master'  " +
					"and t0.parentID = ? " +
					"ORDER BY t0.classificationCode, t1.classificationCode;";
		
		Query query = em.createNativeQuery(sql, com.picsauditing.jpa.entities.ProductService.class);
		query.setParameter(1, parentString);
		return query.getResultList();
	}
}
