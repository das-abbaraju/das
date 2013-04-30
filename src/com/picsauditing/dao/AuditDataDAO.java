package com.picsauditing.dao;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class AuditDataDAO extends PicsDAO {

	private static final Logger logger = LoggerFactory.getLogger(AuditDataDAO.class);

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		AuditData row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public int remove(Set<Integer> ids) {
		if (ids == null || ids.size() == 0) {
			return 0;
		}
		String idList = Strings.implode(ids, ",");
		Query query = em.createQuery("DELETE AuditData c WHERE c.id IN (" + idList + ")");
		return query.executeUpdate();
	}

	public AuditData find(int id) {
		AuditData a = em.find(AuditData.class, id);
		return a;
	}

	public List<AuditData> findByQuestionID(int questionID) {
		Query query = em.createQuery("FROM AuditData d " + "WHERE d.question.id = ?");
		query.setParameter(1, questionID);
		return query.getResultList();
	}

	public List<AuditData> findByQuestionIDs(List<Integer> questionIDs) {
		String queryString = "FROM AuditData d WHERE d.question.id IN (" + Strings.implode(questionIDs, ", ") + ")";
		return em.createQuery(queryString).getResultList();
	}

	public List<AuditData> findDataByCategory(int auditID, int categoryID) {
		Query query = em.createQuery("FROM AuditData d "
				+ "WHERE d.audit.id = :auditID AND d.question.category.id = :category ");
		query.setParameter("auditID", auditID);
		query.setParameter("category", categoryID);

		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public int removeDataByCategory(int auditID, int categoryID) {
		Query query = em.createQuery("DELETE FROM AuditData d "
				+ "WHERE d.audit.id = :auditID AND d.question.id IN (SELECT id from AuditQuestion where category.id = "
				+ categoryID + ")");
		query.setParameter("auditID", auditID);
		return query.executeUpdate();
	}

	/**
	 * Find all answers for given questions for this contractor Questions can
	 * come from any audit type that is Pending, Submitted, Resubmitted or
	 * Active but must only have one possible answer
	 *
	 * @param conID
	 * @param questionIds
	 * @return Map containing QuestionID => AuditData
	 */
	public Map<Integer, AuditData> findAnswersByContractor(int conID, Collection<Integer> questionIds) {
		Map<Integer, AuditData> indexedResult = new HashMap<Integer, AuditData>();
		if (questionIds.size() == 0) {
			return indexedResult;
		}

		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE d.audit.contractorAccount.id = :conID "
				+ "AND (d.audit.expiresDate IS NULL OR d.audit.expiresDate > :today) " + "AND d.question.id IN ("
				+ Strings.implode(questionIds) + ")");
		query.setParameter("conID", conID);
		query.setParameter("today", new Date());

		List<AuditData> result = query.getResultList();

		for (AuditData row : result) {
			int id = row.getQuestion().getId();
			if (!Strings.isEmpty(row.getAudit().getAuditFor())
					&& !AuditType.CANADIAN_PROVINCES.contains(row.getAudit().getAuditType().getId())) {
				throw new RuntimeException("ERROR AuditDataDAO:findAnswersByContractor(" + conID + "," + id
						+ ") getAuditFor not empty for audit id: " + row.getAudit().getId());
			}
			if (indexedResult.containsKey(id)) {
				if (row.getCreationDate().after(indexedResult.get(id).getCreationDate())) {
					indexedResult.put(id, row);
				}
			} else {
				indexedResult.put(id, row);
			}
		}

		return indexedResult;
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

	/**
	 * Get a list of questions that must be verified for this audit.
	 *
	 * This queries questions that have been answered where one or more of the
	 * operators attached to this contractor require validation for the given
	 * question
	 *
	 * @param auditId
	 * @return
	 */
	public List<AuditData> findCustomPQFVerifications(int auditID) {
		// Get the contractor for this audit
		String sqlContractor = "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.id = :auditID";
		// tryQuery(sqlContractor, auditID);

		// Get the list of operators attached to this contractor
		String sqlOperators = "SELECT t.operatorAccount FROM ContractorOperator t " + "WHERE t.contractorAccount IN ("
				+ sqlContractor + ")";
		// tryQuery(sqlOperators, auditID);

		// Get the list of questions that these operators require
		String sqlQuestions = "SELECT f.question FROM FlagCriteria f "
				+ "WHERE f.requiredStatus = 'Complete' AND f.question IS NOT NULL AND f.id IN "
				+ "(SELECT fo.criteria.id FROM FlagCriteriaOperator fo WHERE fo.operator IN  (" + sqlOperators + ")) ";
		// tryQuery(sqlQuestions, auditID);

		// For each question (including the safetyManual), get the ones answered
		// in this audit
		String sql = "SELECT d FROM AuditData d " + "WHERE d.audit.id = :auditID "
				+ " AND (d.question.id = :safetyManual " + "	OR d.question IN (" + sqlQuestions + ")" + " )";
		Query query = em.createQuery(sql);
		query.setParameter("auditID", auditID);
		query.setParameter("safetyManual", AuditQuestion.MANUAL_PQF);

		return query.getResultList();
	}

	public AnswerMap findAnswers(int auditID) {
		Query query = em.createQuery("SELECT d FROM AuditData d WHERE audit.id = ? ORDER BY d.creationDate");
		query.setParameter(1, auditID);

		return mapData(query.getResultList());
	}

	public AnswerMap findAnswers(int auditID, Collection<Integer> questionIds) {
		if (questionIds.size() == 0) {
			return null;
		}

		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE audit.id = ? AND question.id IN ("
				+ Strings.implode(questionIds) + ") " + "ORDER BY d.creationDate");
		query.setParameter(1, auditID);
		return mapData(query.getResultList());
	}

	public AnswerMap findCurrentAnswers(int conId, Collection<Integer> questionIds) {
		if (questionIds.size() == 0) {
			return new AnswerMap(Collections.<AuditData> emptyList());
		}

		Query query = em
				.createNativeQuery(
						"select d.* from pqfdata d join contractor_audit ca on d.auditID = ca.id "
								+ " left join contractor_audit ca2 on ca.conID = ca2.conID and ca.auditTypeID = ca2.auditTypeID and ca.creationDate < ca2.creationDate "
								+ " where ca2.id is null and ca.conID = " + conId + " and d.questionID in ("
								+ Strings.implode(questionIds) + ")", AuditData.class);
		return mapData(query.getResultList());
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

	public List<AuditData> findAnswerByConQuestions(int conID, Collection<Integer> questionIds) {
		Query query = em
				.createQuery("SELECT d FROM AuditData d WHERE d.audit.contractorAccount.id = ? "
						+ "AND d.audit IN (SELECT cao.audit FROM ContractorAuditOperator cao WHERE cao.status IN ('Pending','Submitted','Resubmitted','Complete') AND cao.visible = 1) "
						+ "AND d.question.id IN (" + Strings.implode(questionIds) + ") " + "ORDER BY d.audit.id DESC");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public Map<Integer, AuditData> findAnswersForSafetyManual(int conID, int questionId) {
		Map<Integer, AuditData> data = new HashMap<Integer, AuditData>();
		Query query = em
				.createQuery("FROM AuditData d "
						+ "WHERE audit.contractorAccount.id = ? AND audit IN "
						+ "(SELECT cao.audit FROM ContractorAuditOperator cao WHERE cao.status IN ('Complete','Submitted','Pending','Resubmit','Resubmitted') AND cao.visible = 1) "
						+ "AND question.id =  " + questionId);
		query.setParameter(1, conID);
		for (Object ad : query.getResultList()) {
			AuditData auditData = (AuditData) ad;
			data.put(auditData.getId(), auditData);
		}
		return data;
	}

	public List<AuditData> findServicesPerformed(int conID) {
		Query query = em.createQuery("SELECT d FROM AuditData d "
				+ "WHERE d.audit.contractorAccount.id = ? AND d.question.category.id = 422 "
				+ "AND d.question.effectiveDate < NOW() AND d.question.expirationDate > NOW() ");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	/**
	 *
	 * @param auditIds
	 * @return Answers in the subcategories named Policy Limits for the given
	 *         auditIds
	 */
	public List<AuditData> findPolicyData(List<Integer> auditIds) {

		StringBuilder sb = new StringBuilder("SELECT d FROM AuditData d JOIN d.question q JOIN q.category cat")
				.append(" WHERE d.audit.id in ( ").append(Strings.implode(auditIds, ",")).append(" ) ");

		Query query = em.createQuery(sb.toString());

		// WARNING!! This is hard coded based on the sub category name of Policy
		// AuditTypes
		// If someone changes the subcategory name, this won't work anymore!!
		return query.getResultList();
	}

	/**
	 * Convert a ResultList into an AnswerMap
	 *
	 * @return
	 */
	static private AnswerMap mapData(List<AuditData> result) {
		return new AnswerMap(result);
	}

	public AuditData findAnswerByConQuestion(int conID, int questionID) {
		Query query = em
				.createQuery("SELECT d FROM AuditData d "
						+ "WHERE d.audit.contractorAccount.id = ? "
						+ "AND d.audit IN (SELECT cao.audit FROM ContractorAuditOperator cao WHERE cao.status IN ('Pending','Submitted','Resubmitted','Complete') AND cao.visible = 1)  "
						+ "AND d.question.id = ? ORDER BY d.audit.id DESC");
		query.setParameter(1, conID);
		query.setParameter(2, questionID);
		try {
			return (AuditData) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
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

	public List<AuditData> findByOptionGroupID(int optionGroupID) {
		Query query = em.createQuery("FROM AuditData d WHERE d.question.option.id = ?");
		query.setParameter(1, optionGroupID);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<AuditData> findByOptionGroupAndValue(int optionGroupID, String value) {
		Query query = em.createQuery("FROM AuditData d WHERE d.question.option.id = ? AND d.answer = ?");
		query.setParameter(1, optionGroupID);
		query.setParameter(2, value);
		query.setMaxResults(10);
		return query.getResultList();
	}

	public List<AuditData> findByCertificateID(int conID, int certID) {
		Query query = em
				.createQuery("FROM AuditData d WHERE d.audit.contractorAccount.id = ? AND d.answer = ? AND d.question.questionType = 'FileCertificate'");
		query.setParameter(1, conID);
		query.setParameter(2, String.valueOf(certID));
		try {
			return query.getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public List<AuditData> findWhere(String where) {
		Query query = em.createQuery("From AuditData d WHERE " + where);

		return query.getResultList();
	}

	public Map<Integer, AuditData> findContractorAuditAnswers(int contractorId, int auditTypeId, Set<Integer> questionIds) {
		TypedQuery<AuditData> query = em
				.createQuery(
						"FROM AuditData d WHERE d.audit.auditType.id = :auditTypeId AND d.audit.contractorAccount.id = :contractorId AND d.question.id IN ( :questionIds ) ORDER BY d.audit.id DESC",
						AuditData.class);
		query.setParameter("contractorId", contractorId);
		query.setParameter("auditTypeId", auditTypeId);
		query.setParameter("questionIds", questionIds);

		try {


			List<AuditData> auditDataList = query.getResultList();
			if (CollectionUtils.isEmpty(auditDataList)) {
				return Collections.emptyMap();
			}

			Map<Integer, AuditData> questionAnswers = new HashMap<>();
			for (AuditData auditData : auditDataList) {
				AuditQuestion question = auditData.getQuestion();
				if (question != null) {
					int questionId = question.getId();
					if (!questionAnswers.containsKey(questionId)) {
						questionAnswers.put(questionId, auditData);
					}
				}
			}
		} catch (Exception e) {
			logger.error(MessageFormat.format(
					"An error occurred while running query contractoID =  {0}, auditTypeId = {1}", contractorId, auditTypeId),
					e);
		}

		return Collections.emptyMap();
	}
}
