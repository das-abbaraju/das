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
}
