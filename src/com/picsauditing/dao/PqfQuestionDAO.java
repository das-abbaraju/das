package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.PqfQuestion;

public class PqfQuestionDAO extends PicsDAO {

	public PqfQuestion save(PqfQuestion o) {
		if (o.getQuestionID() == 0) {
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

	public List<PqfQuestion> findBySubCategory(int subCategoryID) {
		Query query = em.createQuery("select t FROM PqfQuestion t WHERE t.auditType.auditTypeID = ?");
		query.setParameter(1, subCategoryID);
		return query.getResultList();
	}
}
