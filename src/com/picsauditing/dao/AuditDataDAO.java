package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;

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

	public Map<Integer, AuditData> findAnswersByContractor(int conID, List<Integer> questionIds) {
		if (questionIds.size() == 0)
			return new HashMap<Integer, AuditData>();
		
		Query query = em.createQuery("FROM AuditData d " +
				"WHERE audit IN (FROM ContractorAudit WHERE contractorAccount.id = ? AND auditStatus = 'Active') " +
				"AND question.questionID IN ("+glue(questionIds)+")");
		query.setParameter(1, conID);
		
		return mapData(query.getResultList());
	}

	public List<AuditData> findCustomPQFVerifications(ContractorAccount contractor) {
		
		StringBuffer queryString = new StringBuffer();
		
		queryString.append("select d FROM AuditData d ");
		queryString.append("inner join fetch d.question q ");
		queryString.append("inner join fetch q.subCategory sc ");
		queryString.append("inner join fetch sc.category cat ");
		queryString.append("where d.audit.contractorAccount.id = ? ");
		queryString.append("and EXISTS ( select a from AuditQuestionOperatorAccount a where a.auditQuestion.questionID = q.questionID ) ");
		
		Query query = em.createQuery(queryString.toString()) ;
		
		query.setParameter(1, contractor.getId());
		
		return query.getResultList();
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
		
		Query query = em.createQuery("SELECT d FROM AuditData d " +
				"WHERE audit.id = ? AND question.questionID IN ("+glue(questionIds)+")");
		query.setParameter(1, auditID);
		
		return mapData(query.getResultList());
	}

	/**
	 * Convert a List into a Map
	 * Note: this could be a good candidate to go into a Utility class
	 * @return
	 */
	private Map<Integer, AuditData> mapData(List<AuditData> result) {
		HashMap<Integer, AuditData> indexedResult = new HashMap<Integer, AuditData>();
		for(AuditData row : result)
			indexedResult.put(row.getQuestion().getQuestionID(), row);
		return indexedResult;
	}
	
	/**
	 * Convert a List into a comma-delimited String
	 * Note: this could be a good candidate to go into a Utility class
	 * @return
	 */
	private String glue(List<Integer> listIDs) {
		StringBuilder ids = new StringBuilder();
		ids.append("-1"); // so we don't have to worry about this ',110,243'
		for(Integer id : listIDs)
			ids.append(",").append(id);
		return ids.toString();
	}
}
