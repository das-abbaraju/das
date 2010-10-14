package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;

@Transactional
@SuppressWarnings("unchecked")
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
	
	public List<AuditCategory> findCategoryNames(String categoryName) {
		String sql = "SELECT c FROM AuditCategory c WHERE c.name LIKE '%" + categoryName + "%'";
		Query query = em.createQuery(sql);
		return query.getResultList();
    }
	public List<AuditCategory> findCategoryNames(String categoryName, int limit) {
		String sql = "SELECT c FROM AuditCategory c WHERE c.name LIKE '%" + categoryName + "%'";
		Query query = em.createQuery(sql);
		if(limit>0)
			query.setMaxResults(limit);
		return query.getResultList();
    }

	public List<AuditCategory> findByAuditTypeID(int id) {
		String sql = "SELECT c FROM AuditCategory c WHERE c.auditType.id = :auditTypeID";
		Query query = em.createQuery(sql);
		query.setParameter("auditTypeID", id);
		return query.getResultList();
    }
}
