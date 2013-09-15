package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;

@SuppressWarnings("unchecked")
public class FlagCriteriaOperatorDAO extends PicsDAO {

	public FlagCriteriaOperator find(int id) {
		return em.find(FlagCriteriaOperator.class, id);
	}

	public List<FlagCriteriaOperator> findByOperator(int opID) {
		Query query = em.createQuery("FROM FlagCriteriaOperator d "
				+ "WHERE operator.id = ? ORDER BY criteria.category, criteria.label");
		query.setParameter(1, opID);
		return query.getResultList();
	}

	public List<FlagCriteriaContractor> getContractorCriteria(FlagCriteriaOperator fco) {
		Query query = em.createQuery("FROM FlagCriteriaContractor d WHERE d.criteria = :criteria "
				+ "AND d.contractor IN (SELECT co.contractorAccount "
				+ "FROM ContractorOperator co WHERE co.operatorAccount = :operator)");
		query.setParameter("criteria", fco.getCriteria());
		query.setParameter("operator", fco.getOperator());
		return query.getResultList();
	}
	
	public List<FlagCriteriaOperator> findByCriteriaID(int criteriaID) {
		Query query = em.createQuery("FROM FlagCriteriaOperator fco " + 
									"WHERE fco.criteria.id=? " +
									"ORDER BY fco.operator.status, fco.operator.name");
		query.setParameter(1, criteriaID);
		return query.getResultList();
	}
	
	public FlagCriteriaOperator findByOperatorAndCriteriaId(int operatorId, int criteriaId) {
		Query query = em.createQuery("FROM FlagCriteriaOperator fco " + 
				"WHERE fco.criteria.id = ? " +
				"AND fco.operator.id = ?");
		query.setParameter(1, criteriaId);
		query.setParameter(2, operatorId);
		return (FlagCriteriaOperator) query.getSingleResult();
	}
	
	public List<FlagCriteria> findWhere(String where) {
		Query query = em.createQuery("From FlagCriteriaOperator WHERE " + where);
		return query.getResultList();
	}
}