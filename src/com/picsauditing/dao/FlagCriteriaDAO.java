package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagCriteria;

@Transactional
@SuppressWarnings("unchecked")
public class FlagCriteriaDAO extends PicsDAO {

	public List<FlagCriteria> findAll() {
		Query query = em.createQuery("FROM FlagCriteria f ORDER BY f.id");
		return query.getResultList();
	}

	public FlagCriteria find(String id) {
		return em.find(FlagCriteria.class, id);
	}
}
