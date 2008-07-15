package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;

@Transactional
public class AuditCategoryDAO extends PicsDAO {
	public AuditCategory save(AuditCategory o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		AuditCategory row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public AuditCategory find(int id) {
        return em.find(AuditCategory.class, id);
    }
}
