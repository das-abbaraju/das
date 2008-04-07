package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.AuditData;

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

	public HashMap<Integer, AuditData> findAnswers(int conID, List<Integer> questionIds) {
		StringBuilder ids = new StringBuilder();
		ids.append(0); // So we have a list like this 0,1,2
		
		for(Integer questionID : questionIds)
			ids.append(",").append(questionID);
		
		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE contractorAccount.id = ? AND question.questionID IN ("+ids.toString()+") ORDER BY question.question");
		query.setParameter(1, conID);
		
		List<AuditData> result = query.getResultList();
		HashMap<Integer, AuditData> indexedResult = new HashMap<Integer, AuditData>();
		for(AuditData row : result)
			indexedResult.put(row.getQuestion().getQuestionID(), row);
		return indexedResult;
	}
}
