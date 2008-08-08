package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;

@Transactional
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

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findBySubCategory(int subCategoryID) {
		Query query = em.createQuery("select t FROM AuditQuestion t WHERE t.subCategory.id = ?" +
				"ORDER BY t.number");
		query.setParameter(1, subCategoryID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findBySubCategories(int[] subCategoryIDs) {
		String ids = Strings.implode(subCategoryIDs, ",");
		Query query = em.createQuery("select t FROM AuditQuestion t WHERE t.subCategory.id IN ("+ids+") " +
				"ORDER BY t.subCategory.category.number, t.subCategory.number, t.number");
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findQuestionByType(String questionType) {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE t.questionType = ? ORDER BY "
				+ "t.subCategory.category.number,t.subCategory.number,t.number");
		query.setParameter(1, questionType);
		return query.getResultList();

	}

}
