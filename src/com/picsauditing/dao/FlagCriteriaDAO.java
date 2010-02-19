package com.picsauditing.dao;

import java.util.HashSet;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagCriteria;

@Transactional
@SuppressWarnings("unchecked")
public class FlagCriteriaDAO extends PicsDAO {
	public FlagCriteria find(int id) {
		return em.find(FlagCriteria.class, id);
	}
	
	public List<FlagCriteria> findWhere(String where) {
		Query query = em.createQuery("From FlagCriteria WHERE " + where);
		return query.getResultList();
	}

	// get select FlagCriteria where id in (select id from FlagCriteriaOperator)
	public HashSet<FlagCriteria> getDistinctOperatorFlagCriteria() {
		Query query = em.createQuery("SELECT DISTINCT criteria from FlagCriteriaOperator");
		return new HashSet(query.getResultList());
	}
}
