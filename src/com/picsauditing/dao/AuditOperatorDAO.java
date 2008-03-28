package com.picsauditing.dao;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditOperator;

import java.util.List;

@Transactional
public class AuditOperatorDAO extends PicsDAO {
	public AuditOperator save(AuditOperator o) {
		if (o.getAuditOperatorID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		AuditOperator row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	@SuppressWarnings("unchecked")
    public List<AuditOperator> findByOperator(int operatorID) {
        Query query = em.createQuery("select t FROM AuditOperator t WHERE t.account.id = ?");
        query.setParameter(1, operatorID);
        return query.getResultList();
    }
	
	@SuppressWarnings("unchecked")
    public List<AuditOperator> findByAudit(int auditId) {
        Query query = em.createQuery("select t FROM AuditOperator t WHERE t.auditType.auditTypeID = ?");
        query.setParameter(1, auditId);
        return query.getResultList();
    }

	public AuditOperator find(int id) {
        return em.find(AuditOperator.class, id);
    }
}
