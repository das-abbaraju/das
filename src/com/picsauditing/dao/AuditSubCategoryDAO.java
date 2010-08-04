package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditSubCategory;

@Transactional
@SuppressWarnings("unchecked")
public class AuditSubCategoryDAO extends PicsDAO {
	public AuditSubCategory save(AuditSubCategory o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditSubCategory row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditSubCategory find(int id) {
		return em.find(AuditSubCategory.class, id);
	}
	
	public List<AuditCategory> findByCategoryID(int id) {
		String sql = "SELECT c FROM AuditSubCategory c WHERE c.category.id = :categoryID";
		Query query = em.createQuery(sql);
		query.setParameter("categoryID", id);
		return query.getResultList();
    }
	
	public List<AuditSubCategory> findSubCategoryNames(String subCategoryName) {
		String sql = "SELECT c FROM AuditSubCategory c WHERE c.subCategory LIKE '%" + subCategoryName + "%'";
		Query query = em.createQuery(sql);
		return query.getResultList();
    }
}
