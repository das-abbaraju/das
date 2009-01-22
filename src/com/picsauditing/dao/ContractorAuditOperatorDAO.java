package com.picsauditing.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAuditOperator;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorAuditOperatorDAO extends PicsDAO {
	public ContractorAuditOperator save(ContractorAuditOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	
	public ContractorAuditOperator find(int auditId, int operatorId) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAuditOperator t where t.audit.id = ? and t.operator.id = ? " );
		query.setParameter(1, auditId);
		query.setParameter(2, operatorId);
		
		try {
			return (ContractorAuditOperator) query.getSingleResult();
		}
		catch( NoResultException nre ) {
			return null;
		}
	}
	
	
	public void remove(int id) {
		ContractorAuditOperator row = find(id);
		remove(row);
	}

	public void remove(ContractorAuditOperator row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorAuditOperator find(int id) {
		return em.find(ContractorAuditOperator.class, id);
	}

}
