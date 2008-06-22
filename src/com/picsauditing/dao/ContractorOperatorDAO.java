package com.picsauditing.dao;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorOperator;

@Transactional
public class ContractorOperatorDAO extends PicsDAO {
	public ContractorOperator save(ContractorOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorOperator row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public ContractorOperator find(int id) {
		return em.find(ContractorOperator.class, id);
	}

	public ContractorOperator find(int conID, int opID) {
		Query query = em.createQuery("FROM ContractorOperator WHERE contractorAccount.id = ? AND operatorAccount.id = ?");
		query.setParameter(1, conID);
		query.setParameter(2, opID);
		return (ContractorOperator)query.getSingleResult();
	}
}
