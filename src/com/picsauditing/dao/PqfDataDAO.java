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
		Query query = em.createQuery("SELECT d FROM PqfData d " + "WHERE contractorAccount = ? AND pqfQuestion.questionID IN ? ORDER BY pqfQuestion.question");
		query.setParameter(1, conID);
		query.setParameter(2, questionIds);
		HashMap<Integer, PqfData> result = new HashMap<Integer, PqfData>();
		for(Object data : query.getResultList()) {
			PqfData data2 = (PqfData)data;
			result.put(data2.getPqfQuestion().getQuestionID(), data2);
		}
		return result;
	}

}
