package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.PqfData;

public class PqfDataDAO extends PicsDAO {
	public PqfData save(PqfData o) {
		if (o.getDataID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		PqfData row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public PqfData find(int id) {
		PqfData a = em.find(PqfData.class, id);
		return a;
	}

	public HashMap<Integer, PqfData> findAnswers(int conID, List<Integer> questionIds) {
		StringBuilder ids = new StringBuilder();
		ids.append(0); // So we have a list like this 0,1,2
		
		for(Integer questionID : questionIds)
			ids.append(",").append(questionID);
		
		Query query = em.createQuery("SELECT d FROM PqfData d " + "WHERE contractorAccount.id = ? AND pqfQuestion.questionID IN ("+ids.toString()+") ORDER BY pqfQuestion.question");
		query.setParameter(1, conID);
		
		List<PqfData> result = query.getResultList();
		HashMap<Integer, PqfData> indexedResult = new HashMap<Integer, PqfData>();
		for(PqfData row : result)
			indexedResult.put(row.getPqfQuestion().getQuestionID(), row);
		return indexedResult;
	}
}
