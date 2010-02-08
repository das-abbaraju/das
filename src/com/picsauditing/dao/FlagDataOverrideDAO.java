package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagDataOverride;

@Transactional
@SuppressWarnings("unchecked")
public class FlagDataOverrideDAO extends PicsDAO {

	public FlagDataOverride find(String id) {
		return em.find(FlagDataOverride.class, id);
	}

	public List<FlagDataOverride> findByContractorAndOperator(int conID, int opID) {
		Query query = em.createQuery("FROM FlagDataOverride d WHERE contractor.id = ? AND operator.id = ?");
		query.setParameter(1, conID);
		query.setParameter(2, opID);
		return query.getResultList();
	}
}
