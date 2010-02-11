package com.picsauditing.dao;

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

	// get select FlagCriteria where id in (select id from FlagCriteriaOperator)
	public List<FlagCriteria> getDistinctOperatorFlagCriteria() {
		Query query = em.createQuery("SELECT DISTINCT criteria from FlagCriteriaOperator");
		return query.getResultList();
	}
}
