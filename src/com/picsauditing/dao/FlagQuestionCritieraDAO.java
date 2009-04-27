package com.picsauditing.dao;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;

@Transactional
public class FlagQuestionCritieraDAO extends PicsDAO {

	public FlagQuestionCriteria save(FlagQuestionCriteria o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		FlagQuestionCriteria row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public FlagQuestionCriteria find(int id) {
		return em.find(FlagQuestionCriteria.class, id);
	}

	public Map<FlagColor, FlagQuestionCriteria> find(int operatorID, int questionID) {
		// TODO fill this in
		return null;
	}

}
