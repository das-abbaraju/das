package com.picsauditing.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OperatorCompetency;

@SuppressWarnings("unchecked")
public class OperatorCompetencyDAO extends PicsDAO {
	
	public OperatorCompetency find(int id) {
		OperatorCompetency o = em.find(OperatorCompetency.class, id);
		return o;
	}

	public List<OperatorCompetency> findAll() {
		Query q = em.createQuery("FROM OperatorCompetency o ORDER BY o.label");
		return q.getResultList();
	}

	public List<OperatorCompetency> findByOperator(int opID) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o WHERE opID = :opID " +
				"ORDER BY o.category, o.label");

		query.setParameter("opID", opID);
		return query.getResultList();
	}
	
	public List<OperatorCompetency> findByOperatorHierarchy(Set<Integer> operatorIDs) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o WHERE o.operator.id IN (:operatorHierarchy) " +
				"ORDER BY o.label");
		
		operatorIDs.removeAll(Account.PICS_CORPORATE);
		
		query.setParameter("operatorHierarchy", operatorIDs);
		return query.getResultList();
	}
	
	public List<OperatorCompetency> findByContractor(int conID) {
		Query query = em.createQuery("SELECT o FROM OperatorCompetency o " +
				"WHERE o IN (SELECT j.competency FROM JobCompetency j WHERE j.jobRole.account.id = ? " +
				"AND j.jobRole.active = 1) ORDER BY o.category, o.label");

		query.setParameter(1, conID);
		return query.getResultList();
	}
	
	public List<String> findDistinctCategories(){
		Query query = em.createQuery("SELECT DISTINCT o.category FROM OperatorCompetency o ORDER BY o.category");
		return query.getResultList();
	}
	
	public List<OperatorCompetency> findMostUsed(int accountID, boolean active) {
		String queryString = "SELECT jc.competency FROM JobCompetency jc WHERE jc.jobRole.account.id = ?";
		
		if (active)
			queryString += " AND jc.jobRole.active = 1";
		
		queryString += " GROUP BY jc.competency ORDER BY COUNT(*) DESC, jc.competency.category, jc.competency.label";
		Query query = em.createQuery(queryString);
		query.setParameter(1, accountID);
		
		return query.getResultList();
	}
}
