package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.AuditData;
import com.picsauditing.auditbuilder.entities.AuditQuestion;
import com.picsauditing.auditbuilder.entities.ContractorAccount;
import com.picsauditing.auditbuilder.entities.ContractorAudit;
import com.picsauditing.auditbuilder.util.AnswerMap;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class AuditDataDAO extends PicsDAO {
	public List<AuditData> findAnswersByContractorAndQuestion(ContractorAccount contractor, AuditQuestion question) {
		Query query = em.createQuery("SELECT d FROM AuditData d " +
				"WHERE d.audit.contractorAccount.id = :contractor " +
				"AND d.question = :question " +
				"AND (d.audit.expiresDate IS NULL OR d.audit.expiresDate > :today)");
		query.setParameter("contractor", contractor.getId());
		query.setParameter("question", question);
		query.setParameter("today", new Date());

		return query.getResultList();
	}

	public AuditData findAnswerToQuestion(int auditId, int questionId) {
		try {
			Query query = em.createQuery("FROM AuditData d " + "WHERE audit.id = ? AND question.id = ? ");
			query.setParameter(1, auditId);
			query.setParameter(2, questionId);
			return (AuditData) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<AuditData> findCustomPQFVerifications(int auditID) {
		String sqlContractor = "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.id = :auditID";

		String sqlOperators = "SELECT t.operatorAccount FROM ContractorOperator t " + "WHERE t.contractorAccount IN ("
				+ sqlContractor + ")";

		String sqlQuestions = "SELECT f.question FROM FlagCriteria f "
				+ "WHERE f.requiredStatus = 'Complete' AND f.question IS NOT NULL AND f.id IN "
				+ "(SELECT fo.criteria.id FROM FlagCriteriaOperator fo WHERE fo.operator IN  (" + sqlOperators + ")) ";

		String sql = "SELECT d FROM AuditData d " + "WHERE d.audit.id = :auditID "
				+ " AND (d.question.id = :safetyManual " + "	OR d.question IN (" + sqlQuestions + ")" + " )";
		Query query = em.createQuery(sql);
		query.setParameter("auditID", auditID);
		query.setParameter("safetyManual", AuditQuestion.MANUAL_PQF);

		return query.getResultList();
	}

	public AnswerMap findAnswersByAuditAndQuestions(ContractorAudit audit, Collection<Integer> questionIds) {
		if (questionIds.size() == 0) {
			return new AnswerMap(Collections.<AuditData> emptyList());
		}

		Query query = em.createQuery("SELECT d FROM AuditData d"
				+ " WHERE d.audit.id = :auditID  AND d.question.id IN (:questionList)");
		query.setParameter("auditID", audit.getId());
		query.setParameter("questionList", questionIds);
		return mapData(query.getResultList());
	}

	static private AnswerMap mapData(List<AuditData> result) {
		return new AnswerMap(result);
	}

	public AuditData findAnswerByAuditQuestion(int auditID, int questionID) {
		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE d.audit.id = ? "
				+ "AND d.question.id = ? ORDER BY d.audit.id DESC");
		query.setParameter(1, auditID);
		query.setParameter(2, questionID);
		try {
			return (AuditData) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}