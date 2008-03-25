package com.picsauditing.dao;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;
import com.picsauditing.entities.AuditType;

import java.util.List;

@Transactional
public class AuditTypeDAO extends PicsDAO {
	public AuditType save(AuditType o) {
		if (o.getAuditTypeID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		AuditType row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	//@SuppressWarnings("unchecked")
    public List<AuditType> findAll() {
        Query query = em.createQuery("select t FROM AuditType t");
        return query.getResultList();
    }
	
	public AuditType find(int id) {
        return em.find(AuditType.class, id);
    }
}
