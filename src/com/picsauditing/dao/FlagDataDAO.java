package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagData;

@Transactional
@SuppressWarnings("unchecked")
public class FlagDataDAO extends PicsDAO {

	public FlagData find(int id) {
		return em.find(FlagData.class, id);
	}

	public List<FlagData> findByContractorAndOperator(int conID, int opID) {
		Query query = em.createQuery("FROM FlagData d WHERE contractor.id = ? AND operator.id = ? ORDER BY d.criteria.category");
		query.setParameter(1, conID);
		query.setParameter(2, opID);
		return query.getResultList();
	}

	public List<FlagData> findByOperator(int opID) {
		Query query = em.createQuery("FROM FlagData d WHERE operator.id = :opID ORDER BY d.contractor.id");
		query.setParameter("opID", opID);
		return query.getResultList();
	}

}
