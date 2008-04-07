package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.AuditQuestion;

public class AuditQuestionDAO extends PicsDAO {

	public AuditQuestion save(AuditQuestion o) {
		if (o.getQuestionID() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditQuestion row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditQuestion find(int id) {
		return em.find(AuditQuestion.class, id);
	}

	public List<AuditQuestion> findBySubCategory(int subCategoryID) {
		Query query = em.createQuery("select t FROM AuditQuestion t WHERE t.auditType.auditTypeID = ?");
		query.setParameter(1, subCategoryID);
		return query.getResultList();
	}
}
