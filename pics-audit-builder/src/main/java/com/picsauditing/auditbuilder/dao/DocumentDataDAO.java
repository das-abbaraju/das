package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.ContractorDocument;
import com.picsauditing.auditbuilder.entities.DocumentData;
import com.picsauditing.auditbuilder.entities.DocumentQuestion;
import com.picsauditing.auditbuilder.entities.ContractorAccount;
import com.picsauditing.auditbuilder.util.AnswerMap;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class DocumentDataDAO extends PicsDAO {
	public List<DocumentData> findAnswersByContractorAndQuestion(ContractorAccount contractor, DocumentQuestion question) {
		Query query = em.createQuery("SELECT d FROM com.picsauditing.auditbuilder.entities.AuditData d " +
				"WHERE d.audit.contractorAccount.id = :contractor " +
				"AND d.question = :question " +
				"AND (d.audit.expiresDate IS NULL OR d.audit.expiresDate > :today)");
		query.setParameter("contractor", contractor.getId());
		query.setParameter("question", question);
		query.setParameter("today", new Date());

		return query.getResultList();
	}

	public DocumentData findAnswerToQuestion(int auditId, int questionId) {
		try {
			Query query = em.createQuery("FROM com.picsauditing.auditbuilder.entities.AuditData d " + "WHERE audit.id = ? AND question.id = ? ");
			query.setParameter(1, auditId);
			query.setParameter(2, questionId);
			return (DocumentData) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<DocumentData> findCustomPQFVerifications(int auditID) {
		String sqlContractor = "SELECT t.contractorAccount FROM com.picsauditing.auditbuilder.entities.ContractorAudit t WHERE t.id = :auditID";

		String sqlOperators = "SELECT t.operatorAccount FROM com.picsauditing.auditbuilder.entities.ContractorOperator t " + "WHERE t.contractorAccount IN ("
				+ sqlContractor + ")";

		String sqlQuestions = "SELECT f.question FROM com.picsauditing.auditbuilder.entities.FlagCriteria f "
				+ "WHERE f.requiredStatus = 'Complete' AND f.question IS NOT NULL AND f.id IN "
				+ "(SELECT fo.criteria.id FROM FlagCriteriaOperator fo WHERE fo.operator IN  (" + sqlOperators + ")) ";

		String sql = "SELECT d FROM com.picsauditing.auditbuilder.entities.AuditData d " + "WHERE d.audit.id = :auditID "
				+ " AND (d.question.id = :safetyManual " + "	OR d.question IN (" + sqlQuestions + ")" + " )";
		Query query = em.createQuery(sql);
		query.setParameter("auditID", auditID);
		query.setParameter("safetyManual", DocumentQuestion.MANUAL_PQF);

		return query.getResultList();
	}

	public AnswerMap findAnswersByAuditAndQuestions(ContractorDocument audit, Collection<Integer> questionIds) {
		if (questionIds.size() == 0) {
			return new AnswerMap(Collections.<DocumentData> emptyList());
		}

		Query query = em.createQuery("SELECT d FROM com.picsauditing.auditbuilder.entities.AuditData d"
				+ " WHERE d.audit.id = :auditID  AND d.question.id IN (:questionList)");
		query.setParameter("auditID", audit.getId());
		query.setParameter("questionList", questionIds);
		return mapData(query.getResultList());
	}

	static private AnswerMap mapData(List<DocumentData> result) {
		return new AnswerMap(result);
	}

	public DocumentData findAnswerByAuditQuestion(int auditID, int questionID) {
		Query query = em.createQuery("SELECT d FROM com.picsauditing.auditbuilder.entities.AuditData d " + "WHERE d.audit.id = ? "
				+ "AND d.question.id = ? ORDER BY d.audit.id DESC");
		query.setParameter(1, auditID);
		query.setParameter(2, questionID);
		try {
			return (DocumentData) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
}