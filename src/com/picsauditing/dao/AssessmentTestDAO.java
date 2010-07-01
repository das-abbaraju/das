package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AssessmentTest;

@Transactional
@SuppressWarnings("unchecked")
public class AssessmentTestDAO extends PicsDAO {
	public AssessmentTest find(int id) {
		return em.find(AssessmentTest.class, id);
	}

	public List<AssessmentTest> findAll() {
		Query q = em.createQuery("FROM AssessmentTest");
		return q.getResultList();
	}

	public List<AssessmentTest> findWhere(String where) {
		Query query = em.createQuery("From AssessmentTest WHERE " + where);
		return query.getResultList();
	}

	public List<AssessmentTest> findByAssessmentCenter(int centerID) {
	 	Query query = em.createQuery("SELECT a FROM AssessmentTest a WHERE assessmentCenterID = ? " +
	 			"ORDER BY qualificationType, qualificationMethod");
	 	query.setParameter(1, centerID);
	 	
	 	return query.getResultList();
	}
	
	public List<AssessmentTest> findRandom(int limit) {
		Query query = em.createQuery("SELECT a FROM AssessmentTest a ORDER BY RAND()");
		query.setMaxResults(limit);

		return query.getResultList();
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

		Query query = em.createQuery("SELECT a FROM AssessmentTest a WHERE expirationDate > NOW()"
				+ " AND effectiveDate <= NOW()" + where);
		return query.getResultList();
	}
	
	public List<AssessmentResultStage> findStaged(int centerID) {
		Query query = em.createQuery("SELECT a FROM AssessmentResultStage a WHERE a.center.id = ?");
		query.setParameter(1, centerID);
		
		return query.getResultList();
	}
	
	public List<AssessmentResultStage> findStagedByAccount(int accountID) {
		Query query = em.createQuery("SELECT a FROM AssessmentResultStage a WHERE a.picsAccountID = ?");
		query.setParameter(1, accountID);
		
		return query.getResultList();
	}
	
	public List<AssessmentResultStage> findUnmappedTests(int centerID) {
		String queryString = "SELECT a.* FROM assessment_result_stage a " +
				"LEFT JOIN assessment_test t " +
				"USING (qualificationType, qualificationMethod, description) " +
				"WHERE a.centerID = " + centerID + " " +
				"AND t.qualificationType IS NULL " +
				"AND t.qualificationMethod IS NULL " +
				"AND t.description IS NULL " +
				"GROUP BY a.qualificationType, a.qualificationMethod, a.description " +
				"ORDER BY a.qualificationType, a.qualificationMethod, a.description";
		
		Query query = em.createNativeQuery(queryString, AssessmentResultStage.class);	
		return query.getResultList();
	}
	
	/**
	 * Returns a list of AssessmentResultStage where the picsAccountID is 0 or the 
	 * assessment test doesn't exist.
	 */
	public List<AssessmentResultStage> findUnmatched(int centerID) {
		// There's no group by like there was in findUnmappedTests();
		String queryString = "(SELECT a.* FROM assessment_result_stage a " +
				"LEFT JOIN assessment_test t USING (qualificationType, qualificationMethod, description) " +
				"WHERE a.centerID = " + centerID + " AND t.qualificationType IS NULL " +
				"AND t.qualificationMethod IS NULL AND t.description IS NULL " +
				"ORDER BY a.qualificationType, a.qualificationMethod, a.description) " +
				"UNION " +
				"(SELECT * FROM assessment_result_stage WHERE picsAccountID IS NULL OR picsAccountID = 0) " +
				"ORDER BY creationDate";
		
		Query query = em.createNativeQuery(queryString, AssessmentResultStage.class);	
		return query.getResultList();
	}
}
