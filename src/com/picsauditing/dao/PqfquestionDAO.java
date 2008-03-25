package com.picsauditing.dao;

import com.picsauditing.jpa.entities.Pqfquestion;

public class PqfquestionDAO extends PicsDAO {
	public Pqfquestion save(Pqfquestion o) {
		if (o.getQuestionId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		Pqfquestion row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public Pqfquestion find(int id) {
        return em.find(Pqfquestion.class, id);
    }

}
