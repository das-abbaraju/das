package com.picsauditing.dao;

import com.picsauditing.jpa.entities.PqfQuestion;

public class PqfquestionDAO extends PicsDAO {
	public PqfQuestion save(PqfQuestion o) {
		if (o.getQuestionId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		PqfQuestion row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public PqfQuestion find(int id) {
        return em.find(PqfQuestion.class, id);
    }

}
