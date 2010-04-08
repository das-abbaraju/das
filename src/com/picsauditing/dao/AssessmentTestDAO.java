package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AssessmentTest;

@Transactional
@SuppressWarnings("unchecked")
public class AssessmentTestDAO extends PicsDAO {
	public AssessmentTest save(AssessmentTest o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AssessmentTest row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AssessmentTest find(int id) {
		AssessmentTest a = em.find(AssessmentTest.class, id);
		return a;
	}
	
	public List<AssessmentTest> findByAssessmentCenter(int centerID) {
		Query query = em.createQuery("SELECT a FROM AssessmentTest a WHERE assessmentCenterID = ?");
		query.setParameter(1, centerID);
		
		return query.getResultList();
	}
	
	public AssessmentTest findRandom() {
		Query query = em.createQuery("SELECT a FROM AssessmentTest a ORDER BY RAND()");
		query.setMaxResults(1);
		
		return (AssessmentTest) query.getSingleResult();
	}
	
	public List<AssessmentTest> findExpired(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = " AND " + where;
		
		Query query = em.createQuery("SELECT a FROM AssessmentTest a WHERE expirationDate <= NOW()" + where);
		return query.getResultList();
	}
	
	public List<AssessmentTest> findInEffect(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = " AND " + where;
		
		Query query = em.createQuery("SELECT a FROM AssessmentTest a WHERE expirationDate > NOW()" +
				" AND effectiveDate <= NOW()" + where);
		return query.getResultList();
	}
}
