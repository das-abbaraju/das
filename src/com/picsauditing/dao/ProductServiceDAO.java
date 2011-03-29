package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.ProductService;

@SuppressWarnings("unchecked")
public class ProductServiceDAO extends PicsDAO {

	public ProductService find(int id) {
		ProductService a = em.find(ProductService.class, id);
		return a;
	}

	public List<ProductService> findByParent(int parentID) {
		Query query = em.createQuery("SELECT p FROM ProductService p WHERE p.parent.id =  ?");
		query.setParameter(1, parentID);
		return query.getResultList();
	}

	public List<ProductService> findRoot(String type) {
		Query query = em.createQuery("SELECT p FROM ProductService p " +
				"WHERE p.parent IS NULL AND p.classificationType = ?");
		query.setParameter(1, type);
		return query.getResultList();
	}
}
