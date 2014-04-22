package com.picsauditing.flagcalculator.dao;

import com.picsauditing.flagcalculator.entities.FlagCriteria;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class FlagCriteriaDAO extends PicsDAO {
//	public FlagCriteria find(int id) {
//		return em.find(FlagCriteria.class, id);
//	}
//
//	public List<FlagCriteria> findAll() {
//		Query q = em.createQuery("FROM FlagCriteria t ORDER BY t.displayOrder, t.category");
//		return q.getResultList();
//	}
//
	public List<FlagCriteria> findWhere(String where) {
		Query query = em.createQuery("From FlagCriteria WHERE " + where);
		return query.getResultList();
	}

//	/**
//	 * This method pulls back all {@link FlagCriteria} that is in use by {@link OperatorAccount}s.
//	 */
//	public HashSet<FlagCriteria> getDistinctOperatorFlagCriteria() {
//		Query query = em.createQuery("SELECT DISTINCT criteria from FlagCriteriaOperator");
//		return new HashSet(query.getResultList());
//	}
}
