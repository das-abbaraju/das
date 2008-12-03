package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

@Transactional
public class AuditDataDAO extends PicsDAO {
	public AuditData save(AuditData o) {
		if (o.getDataID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditData row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditData find(int id) {
		AuditData a = em.find(AuditData.class, id);
		return a;
	}

	public List<AuditData> findByQuestionID(int questionID) {
		Query query = em.createQuery("FROM AuditData d "
				+ "WHERE d.question.id = ?");
		query.setParameter(1, questionID);
		return query.getResultList();
	}

	public Map<Integer, AuditData> findByCategory(int auditID, AuditCategory category) {
		Query query = em.createQuery("FROM AuditData d "
				+ "WHERE d.audit.id = :auditID AND d.question.subCategory.category = :category");
		query.setParameter("auditID", auditID);
		query.setParameter("category", category);

		return mapData(query.getResultList());
	}

	public Map<Integer, AuditData> findByCategory(int auditID, int categoryID) {
		AuditCategory auditCategory = new AuditCategory();
		auditCategory.setId(categoryID);
		return findByCategory(auditID, auditCategory);
	}

	public Map<Integer, AuditData> findAnswersByContractor(int conID, List<Integer> questionIds) {
		if (questionIds.size() == 0)
			return new HashMap<Integer, AuditData>();

		Query query = em.createQuery("FROM AuditData d "
				+ "WHERE audit IN (FROM ContractorAudit WHERE contractorAccount.id = ? AND auditStatus = 'Active') "
				+ "AND question.id IN (" + glue(questionIds) + ")");
		query.setParameter(1, conID);

		return mapData(query.getResultList());

	}

	public AuditData findAnswerToQuestion(int auditId, int questionId) {
		try {
			Query query = em.createQuery("FROM AuditData d " + "WHERE audit.id = ? AND question.id =? ");
			query.setParameter(1, auditId);
			query.setParameter(2, questionId);
			return (AuditData) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	private void tryQuery(String sql, int auditID) {
		Query query = em.createQuery(sql);
		query.setParameter("auditID", auditID);
		System.out.println(sql);
		List list = query.getResultList();
		return;
	}
	
	/**
	 * Get a list of questions that must be verified for this audit.
	 * 
	 * This queries questions that have been answered where one or more 
	 * of the operators attached to this contractor require validation for the given question
	 * @param auditId
	 * @return
	 */
	public List<AuditData> findCustomPQFVerifications(int auditID) {
		// Get the contractor for this audit
		String sqlContractor = "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.id = :auditID";
		//tryQuery(sqlContractor, auditID);
		
		// Get the list of operators attached to this contractor
		String sqlOperators = "SELECT t.operatorAccount FROM ContractorOperator t " +
			"WHERE t.contractorAccount IN (" + sqlContractor + ")";
		//tryQuery(sqlOperators, auditID);
		
		// Get the list of questions that these operators require
		String sqlQuestions = "SELECT t.auditQuestion FROM AuditQuestionOperatorAccount t " +
			"WHERE t.operatorAccount IN (" + sqlOperators + ")";
		//tryQuery(sqlQuestions, auditID);
		
		// For each question (including the safetyManual), get the ones answered in this audit
		String sql = "SELECT d FROM AuditData d " +
				"WHERE d.audit.id = :auditID " +
				" AND (d.question.id = :safetyManual " +
				"	OR d.question IN (" + sqlQuestions + ")" +
				" )";
		//System.out.println(sql);
		Query query = em.createQuery(sql);
		query.setParameter("auditID", auditID);
		query.setParameter("safetyManual", AuditQuestion.MANUAL_PQF);
		
		return query.getResultList();
	}

	public Map<Integer, AuditData> findAnswers(int auditID) {
		Query query = em.createQuery("SELECT d FROM AuditData d WHERE audit.id = ?");
		query.setParameter(1, auditID);

		return mapData(query.getResultList());
	}

	public Map<Integer, AuditData> findAnswers(int auditID, List<Integer> questionIds) {
		if (questionIds.size() == 0)
			return new HashMap<Integer, AuditData>();

		if (questionIds.contains(AuditQuestion.EMR_AVG)) {
			// We need to get the average EMR for the past 3 years
			questionIds.remove(AuditQuestion.EMR_AVG);
			questionIds.add(AuditQuestion.EMR07);
			questionIds.add(AuditQuestion.EMR06);
			questionIds.add(AuditQuestion.EMR05);
		}

		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE audit.id = ? AND question.id IN ("
				+ glue(questionIds) + ")");
		query.setParameter(1, auditID);

		return mapData(query.getResultList());
	}

	/**
	 * Convert a List into a Map Note: this could be a good candidate to go into
	 * a Utility class
	 * 
	 * @return
	 */
	private Map<Integer, AuditData> mapData(List<AuditData> result) {
		HashMap<Integer, AuditData> indexedResult = new HashMap<Integer, AuditData>();
		for (AuditData row : result)
			indexedResult.put(row.getQuestion().getId(), row);
		return indexedResult;
	}

	/**
	 * Convert a List into a comma-delimited String Note: this could be a good
	 * candidate to go into a Utility class
	 * 
	 * @return
	 */
	private String glue(List<Integer> listIDs) {
		StringBuilder ids = new StringBuilder();
		ids.append("-1"); // so we don't have to worry about this ',110,243'
		for (Integer id : listIDs)
			ids.append(",").append(id);
		return ids.toString();
	}

	public Map<Integer, AuditData> findAnswersForSafetyManual(int conID, int questionId) {
		Map<Integer, AuditData> data = new HashMap<Integer, AuditData>();
		Query query = em
				.createQuery("FROM AuditData d "
						+ "WHERE audit IN (FROM ContractorAudit WHERE contractorAccount.id = ? AND auditStatus IN ('Active','Submitted','Pending')) "
						+ "AND question.id =  " + questionId + "" + ")");
		query.setParameter(1, conID);
		for (Object ad : query.getResultList()) {
			AuditData auditData = (AuditData) ad;
			data.put(auditData.getDataID(), auditData);
		}
		return data;
	}
}
