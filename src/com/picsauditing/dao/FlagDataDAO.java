package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagData;

@Transactional
@SuppressWarnings("unchecked")
public class FlagDataDAO extends PicsDAO {

	public FlagData find(String id) {
		return em.find(FlagData.class, id);
	}

	public List<FlagData> findByContractorAndOperator(int conID, int opID) {
		Query query = em.createQuery("FROM FlagData d WHERE contractor.id = ? AND operator.id = ? ORDER BY d.flag");
		query.setParameter(1, conID);
		query.setParameter(2, opID);
		return query.getResultList();
	}

}
