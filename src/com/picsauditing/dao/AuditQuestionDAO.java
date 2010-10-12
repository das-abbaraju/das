package com.picsauditing.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;

@Transactional
public class AuditQuestionDAO extends PicsDAO {

	public AuditQuestion save(AuditQuestion o) {
		if (o.getId() == 0) {
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

	/**
	 * Get the first 100 results that match the criteria
	 * 
	 * @param where
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findWhere(String where) {
		if (where == null)
			where = "";
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE " + where + " ORDER BY "
				+ "t.category, t.number");
		query.setMaxResults(100);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findFlaggableQuestions() {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE flaggable = TRUE ORDER BY "
				+ "t.category,t.number");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findByQuestion(String question, Permissions permissions, Set<AuditCategory> auditCats) {
		String where = "SELECT t FROM AuditQuestion t WHERE t.name LIKE :name AND t.effectiveDate < NOW() AND t.expirationDate > NOW() ORDER BY t.number";
		Query query = em.createQuery(where);
		query.setParameter("name", "%" + Utilities.escapeQuotes(question) + "%");

		List<AuditQuestion> result = query.getResultList();

		if (permissions.isOperatorCorporate()) {
			Iterator<AuditQuestion> iter = result.iterator();
			while (iter.hasNext()) {
				AuditQuestion q = iter.next();
				if (!auditCats.contains(q.getCategory()))
					iter.remove();
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findByCategory(int categoryID) {
		Query query = em.createQuery("select t FROM AuditQuestion t WHERE t.category.id = ? " + "ORDER BY t.number");
		query.setParameter(1, categoryID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findQuestionByType(String questionType) {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE t.questionType = ? ORDER BY "
				+ "t.category, t.number");
		query.setParameter(1, questionType);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<AuditQuestion> findQuestionsByUniqueCodes(List<String> uniqueCodes) {
		StringBuilder sb = new StringBuilder("SELECT t FROM AuditQuestion t WHERE t.uniqueCode in ( ");

		for (String code : uniqueCodes) {
			sb.append("'");
			sb.append(code);
			sb.append("',");
		}
		sb.deleteCharAt(sb.length() - 1); // kill the last comma
		sb.append(")");

		Query query = em.createQuery(sb.toString());

		return query.getResultList();

	}

}
