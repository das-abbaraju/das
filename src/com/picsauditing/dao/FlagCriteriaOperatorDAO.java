package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagCriteriaOperator;

@Transactional
@SuppressWarnings("unchecked")
public class FlagCriteriaOperatorDAO extends PicsDAO {

	public FlagCriteriaOperator find(int id) {
		return em.find(FlagCriteriaOperator.class, id);
	}

	public List<FlagCriteriaOperator> findByOperator(int opID) {
		Query query = em
				.createQuery("FROM FlagCriteriaOperator d WHERE operator.id = ? ORDER BY criteria.category, criteria.label");
		query.setParameter(1, opID);
		return query.getResultList();
	}
}
