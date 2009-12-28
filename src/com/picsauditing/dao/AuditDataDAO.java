package com.picsauditing.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.AnswerMapByAudits;
import com.picsauditing.util.Strings;

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

	public int remove(Set<Integer> ids) {
		if (ids == null || ids.size() == 0)
			return 0;
		String idList = Strings.implode(ids, ",");
		Query query = em.createQuery("DELETE AuditData c WHERE c.id IN (" + idList + ")");
		return query.executeUpdate();
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
				+ "WHERE d.audit.id = :auditID AND d.question.subCategory.category = :category ORDER BY d.creationDate");
		query.setParameter("auditID", auditID);
		query.setParameter("category", category);

		return mapData(query.getResultList());
	}

	public List<AuditData> findDataByCategory(int auditID, int categoryID) {
		Query query = em.createQuery("FROM AuditData d "
				+ "WHERE d.audit.id = :auditID AND d.question.subCategory.category.id = :category ");
		query.setParameter("auditID", auditID);
		query.setParameter("category", categoryID);

		return query.getResultList();
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
		Map<Integer, Map<String, AuditData>> indexedResult = new HashMap<Integer, Map<String, AuditData>>();
		if (questionIds.size() == 0)
			return indexedResult;

		Query query = em.createQuery("SELECT d FROM AuditData d " +
				"WHERE d.audit.contractorAccount.id = ? " +
				"AND d.audit.auditStatus IN ('Pending','Submitted','Resubmitted','Active') " +
				"AND d.question.id IN (" + glue(questionIds) + ") " +
				"ORDER BY d.audit.auditStatus DESC");
		// Sort it first by Submitted, then by Active, so when we load the map
		// the Active values will override the Submitted ones
		query.setParameter(1, conID);

		List<AuditData> result = query.getResultList();

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
			Query query = em.createQuery("FROM AuditData d " + "WHERE audit.id = ? AND question.id = ? ");
			query.setParameter(1, auditId);
			query.setParameter(2, questionId);
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
		Query query = em.createQuery("SELECT d FROM AuditData d WHERE audit.id = ? ORDER BY d.creationDate");
		query.setParameter(1, auditID);

		return mapData(query.getResultList());
	}

	public AnswerMap findAnswers(int auditID, List<Integer> questionIds) {
		if (questionIds.size() == 0)
			return null;

		Query query = em.createQuery("SELECT d FROM AuditData d " + 
				"WHERE audit.id = ? AND question.id IN (" + glue(questionIds) + ") " +
				"ORDER BY d.creationDate");
		query.setParameter(1, auditID);
		return mapData(query.getResultList());
	}

	public Map<Integer, AnswerMap> findAnswersQuestionList(List<Integer> auditIds, List<AuditQuestion> questions) {
		List<Integer> questionIDs = new Vector<Integer>();
		for (AuditQuestion q : questions) {
			questionIDs.add(q.getId());
		}
		return findAnswers(auditIds, questionIDs);
	}

	public Map<Integer, AnswerMap> findAnswers(List<Integer> auditIds, List<Integer> questionIds) {
		if (questionIds.size() == 0)
			return null;
		
		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE audit.id in (" + glue( auditIds ) + " ) and question.id IN ("
				+ glue(questionIds) + ") ");

		Map<Integer, AnswerMap> response = new HashMap<Integer, AnswerMap>();
		List<AuditData> results = query.getResultList();
		
		for(AuditData row : results) {
			int auditID = row.getAudit().getId();
			if (!response.containsKey(auditID))
				response.put(auditID, new AnswerMap());
			response.get(auditID).add(row);
		}
		return response;
	}
	
	public AnswerMapByAudits findAnswersByAudits(List<ContractorAudit> audits, List<Integer> questionIds) {
		if (questionIds != null && questionIds.size() == 0)
			return null;
		
		List<Integer> auditIds = new Vector<Integer>();
		
		for( ContractorAudit audit : audits ) {
			auditIds.add(audit.getId());
		}
		
		Query query = null;
		
		if( questionIds != null && questionIds.size() > 0 ) {
			query = em.createQuery("SELECT d FROM AuditData d " + "WHERE audit.id in (" + glue( auditIds ) + " ) and question.id IN ("
				+ glue(questionIds) + ") ");
		}
		else {
			query = em.createQuery("SELECT d FROM AuditData d " + "WHERE audit.id in (" + glue( auditIds ) + " ) ");
			
		}
		
		List<AuditData> results = query.getResultList();
		return AuditDataDAO.buildAnswerMapByAudits(results);
	}

	public Map<Integer, AuditData> findAnswersForSafetyManual(int conID, int questionId) {
		Map<Integer, AuditData> data = new HashMap<Integer, AuditData>();
		Query query = em
				.createQuery("FROM AuditData d "
						+ "WHERE audit IN (FROM ContractorAudit WHERE contractorAccount.id = ? AND auditStatus IN ('Active','Submitted','Pending','Resubmitted')) "
						+ "AND question.id =  " + questionId + "" + ")");
		query.setParameter(1, conID);
		for (Object ad : query.getResultList()) {
			AuditData auditData = (AuditData) ad;
			data.put(auditData.getId(), auditData);
		}
		return data;
	}
	
	public List<AuditData> findServicesPerformed(int conID) {
		Query query = em.createQuery("SELECT d FROM AuditData d " +
				"WHERE d.audit.contractorAccount.id = ? and d.question.subCategory.id = 40 AND d.question.isVisible = 'Yes' ");
		query.setParameter(1, conID);
		return  query.getResultList();
	}

	public AnswerMap findAnswersByAuditAndUniqueCode( int auditId, String uniqueCode ) {
		
			Query query = em.createQuery("SELECT d FROM AuditData d JOIN AuditQuestion q WHERE d.audit.id = ? AND q.uniqueCode = ? ");
					
			query.setParameter(1, auditId);
			query.setParameter(2, uniqueCode);
			return mapData(query.getResultList());
	}

	public List<AuditData> findAnswersByContractorAndUniqueCode( int conId, String uniqueCode ) {
		Query query = em.createQuery("SELECT d FROM AuditData d JOIN d.question q " +
				"WHERE d.audit.contractorAccount.id = ? AND q.uniqueCode = ? ");
		
		query.setParameter(1, conId);
		query.setParameter(2, uniqueCode);
		return query.getResultList();
	}

	
	/**
	 * 
	 * @param auditIds
	 * @return Answers in the subcategories named Policy Limits for the given auditIds
	 */
	public List<AuditData> findPolicyData( List<Integer> auditIds) {
		
		StringBuilder sb = new StringBuilder("SELECT d FROM AuditData d JOIN d.question q JOIN q.subCategory sub")
			.append( " WHERE d.audit.id in ( " )
			.append( Strings.implode(auditIds, "," ) ) 
			.append(" ) " );
		
		Query query = em.createQuery(  sb.toString() );
		
		// WARNING!! This is hard coded based on the sub category name of Policy AuditTypes
		// If someone changes the subcategory name, this won't work anymore!!
		return query.getResultList();
	}
	
	static public AnswerMapByAudits buildAnswerMapByAudits(List<AuditData> results) {
		AnswerMapByAudits response = new AnswerMapByAudits();
		
		ContractorAudit audit = null;
		List<AuditData> temp = new Vector<AuditData>();
		
		for( AuditData data : results ) {
			if( audit == null ) {
				audit = data.getAudit();
			}
			
			if( data.getAudit().getId() != audit.getId() ) {
				response.put( audit, mapData( temp ) );
				temp = new Vector< AuditData >();
				audit = data.getAudit();
			}
			
			temp.add(data);
		}
		
		if( audit != null ) {
			response.put( audit, mapData( temp ) );
		}
		return response;
	}
	
	/**
	 * Convert a ResultList into an AnswerMap
	 * 
	 * @return
	 */
	static private AnswerMap mapData(List<AuditData> result) {
		AnswerMap indexedResult = new AnswerMap();
		for (AuditData row : result)
			indexedResult.add(row);
		return indexedResult;
	}
	
	public List<AuditData> findAnswerByConQuestions(int conID, Collection<Integer> questionIds) {
		Query query = em.createQuery("SELECT d FROM AuditData d " +
				"WHERE d.audit.contractorAccount.id = ? " +
				"AND d.audit.auditStatus IN ('Pending','Submitted','Resubmitted','Active') " +
				"AND d.question.id IN (" + glue(questionIds) + ") " +
				"ORDER BY d.audit.auditStatus DESC");
		query.setParameter(1, conID);
		return query.getResultList();
	}
}
