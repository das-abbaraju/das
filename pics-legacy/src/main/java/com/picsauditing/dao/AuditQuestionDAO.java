package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditExtractOption;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionFunction;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.QuestionFunctionType;

@SuppressWarnings("unchecked")
public class AuditQuestionDAO extends PicsDAO {
	private final Logger logger = LoggerFactory.getLogger(AuditQuestionDAO.class);
			
	@Transactional(propagation = Propagation.NESTED)
	public AuditQuestion save(AuditQuestion o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
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
	public List<AuditQuestion> findWhere(String where) {
		if (where == null)
			where = "";
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE " + where + " ORDER BY "
				+ "t.category, t.number");
		query.setMaxResults(100);
		return query.getResultList();

	}

	public List<AuditQuestion> findFlaggableQuestions() {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE flaggable = TRUE ORDER BY "
				+ "t.category,t.number");
		return query.getResultList();
	}

	public List<AuditQuestion> findByCategory(int categoryID) {
		Query query = em.createQuery("select t FROM AuditQuestion t WHERE t.category.id = ? " + "ORDER BY t.number");
		query.setParameter(1, categoryID);
		return query.getResultList();
	}

	public List<AuditQuestion> findQuestionByType(String questionType) {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE t.questionType = ? ORDER BY "
				+ "t.category, t.number");
		query.setParameter(1, questionType);
		return query.getResultList();

	}

	public Map<AuditQuestion, AuditQuestionFunction> findFunctionsByAudit(AuditType auditType) {
		Query query = em.createQuery("SELECT t FROM AuditQuestionFunction t WHERE t.type = ? AND t.question.category.auditType = ?");
		query.setParameter(1, QuestionFunctionType.Calculation);
		query.setParameter(2, auditType);
		
		List<AuditQuestionFunction> list = query.getResultList();
		
		Map<AuditQuestion, AuditQuestionFunction> map = new HashMap<AuditQuestion, AuditQuestionFunction>();
		for (AuditQuestionFunction auditQuestionFunction : list) {
			map.put(auditQuestionFunction.getQuestion(), auditQuestionFunction);
		}
		
		return map;
	}
	
	public Map<AuditQuestion, AuditQuestionFunction> findFunctionsByCategory(AuditCategory category) {
		Query query = em.createQuery("SELECT t FROM AuditQuestionFunction t WHERE t.type = ? AND t.question.category = ?");
		query.setParameter(1, QuestionFunctionType.Calculation);
		query.setParameter(2, category);
		
		List<AuditQuestionFunction> list = query.getResultList();
		
		Map<AuditQuestion, AuditQuestionFunction> map = new HashMap<AuditQuestion, AuditQuestionFunction>();
		for (AuditQuestionFunction auditQuestionFunction : list) {
			map.put(auditQuestionFunction.getQuestion(), auditQuestionFunction);
		}
		
		return map;
	}
	
	public List<AuditQuestion> findQuestionByOptionGroup(int optionGroupID) {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE t.option.id = ? ORDER BY "
				+ "t.category, t.number");
		query.setParameter(1, optionGroupID);
		return query.getResultList();
	}

	public List<AuditQuestion> findQuestionByOptionGroupByUniqueCode(String uniqueCode) {
		Query query = em.createQuery("SELECT t FROM AuditQuestion t WHERE t.option.uniqueCode = ? ORDER BY "
				+ "t.category, t.number");
		query.setParameter(1, uniqueCode);
		return query.getResultList();
	}

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
	
	public AuditExtractOption findAuditExtractOptionByQuestionId(int questionId) {
		Query query = em.createQuery("from AuditExtractOption where question.id = ?");
		query.setParameter(1, questionId);
		List<AuditExtractOption> results = query.getResultList();
		if (results == null || results.size() == 0) {
			return null;
		} else if (results.size() > 1) {
			logger.error("more than one AuditExtractOption found for question {}", questionId);
			// throw? if we don't we'll return the first one returned below
		}
		return results.get(0);
	}
}
