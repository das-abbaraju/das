package com.picsauditing.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.AnswerMap;

@Transactional
@SuppressWarnings("unchecked")
public class AuditDataDAO extends PicsDAO {
	public AuditData save(AuditData o) {
		if (o.getId() == 0) {
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

	public AnswerMap findByCategory(int auditID, AuditCategory category) {
		Query query = em.createQuery("FROM AuditData d "
				+ "WHERE d.audit.id = :auditID AND d.question.subCategory.category = :category");
		query.setParameter("auditID", auditID);
		query.setParameter("category", category);

		return mapData(query.getResultList());
	}

	public AnswerMap findByCategory(int auditID, int categoryID) {
		AuditCategory auditCategory = new AuditCategory();
		auditCategory.setId(categoryID);
		return findByCategory(auditID, auditCategory);
	}

	/**
	 * Find all answers for given questions for this contractor
	 * Each question may include more than one answer if there are multiple audits "for"
	 * Questions can come from any audit type that is Submitted or Active
	 * 
	 * @param conID
	 * @param questionIds
	 * @return Map containing QuestionID => AuditFor => AuditData
	 */
	public Map<Integer, Map<String, AuditData>> findAnswersByContractor(int conID, Collection<Integer> questionIds) {
		if (questionIds.size() == 0)
			return new HashMap<Integer, Map<String, AuditData>>();

		Query query = em.createQuery("SELECT d FROM AuditData d " +
				"WHERE d.audit.contractorAccount.id = ? " +
				"AND d.audit.auditStatus IN ('Submitted', 'Active') " +
				"AND d.question.id IN (" + glue(questionIds) + ") " +
				"ORDER BY d.audit.auditStatus DESC");
		// Sort it first by Submitted, then by Active, so when we load the map
		// the Active values will override the Submitted ones
		query.setParameter(1, conID);

		List<AuditData> result = query.getResultList();

		Map<Integer, Map<String, AuditData>> indexedResult = new HashMap<Integer, Map<String, AuditData>>();
		for (AuditData row : result) {
			int id = row.getQuestion().getId();
			Map<String, AuditData> dataMap = new TreeMap<String, AuditData>();
			if (indexedResult.containsKey(id))
				dataMap = indexedResult.get(id);
			String key = row.getAudit().getAuditFor();
			if (key == null)
				key = "";
			dataMap.put(key, row);
			indexedResult.put(id, dataMap);
		}
		return indexedResult;
	}
	
	public AuditData findAnswerToQuestion(int auditId, int questionId) {
		try {
			Query query = em.createQuery("FROM AuditData d " + "WHERE audit.id = ? AND question.id = ? AND parentAnswer IS NULL");
			query.setParameter(1, auditId);
			query.setParameter(2, questionId);
			return (AuditData) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public AuditData findAnswerToQuestion(int auditId, int questionId, int parentId) {
		if (parentId == 0)
			return findAnswerToQuestion(auditId, questionId);
		
		try {
			Query query = em.createQuery("FROM AuditData d " + "WHERE audit.id = ? AND question.id = ? AND parentAnswer.id = ? ");
			query.setParameter(1, auditId);
			query.setParameter(2, questionId);
			query.setParameter(3, parentId);
			return (AuditData) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
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

	public AnswerMap findAnswers(int auditID) {
		Query query = em.createQuery("SELECT d FROM AuditData d WHERE audit.id = ?");
		query.setParameter(1, auditID);

		return mapData(query.getResultList());
	}

	public AnswerMap findAnswers(int auditID, List<Integer> questionIds) {
		if (questionIds.size() == 0)
			return null;

		/*
		if (questionIds.contains(AuditQuestion.EMR_AVG)) {
			// We need to get the average EMR for the past 3 years
			questionIds.remove(AuditQuestion.EMR_AVG);
			questionIds.add(AuditQuestion.EMR07);
			questionIds.add(AuditQuestion.EMR06);
			questionIds.add(AuditQuestion.EMR05);
		}
		*/

		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE audit.id = ? AND question.id IN ("
				+ glue(questionIds) + ")");
		query.setParameter(1, auditID);
		return mapData(query.getResultList());
	}

	/**
	 * Convert a ResultList into an AnswerMap
	 * 
	 * @return
	 */
	private AnswerMap mapData(List<AuditData> result) {
		AnswerMap indexedResult = new AnswerMap();
		for (AuditData row : result)
			indexedResult.add(row);
		return indexedResult;
	}

	/**
	 * Convert a List into a comma-delimited String Note: this could be a good
	 * candidate to go into a Utility class
	 * 
	 * @return
	 */
	private String glue(Collection<Integer> listIDs) {
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
			data.put(auditData.getId(), auditData);
		}
		return data;
	}
}
