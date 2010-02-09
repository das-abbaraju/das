package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditSubCategory;

@Transactional
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
}
