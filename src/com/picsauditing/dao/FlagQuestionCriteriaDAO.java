package com.picsauditing.dao;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;

@Transactional
public class FlagQuestionCriteriaDAO extends PicsDAO {

	public FlagQuestionCriteria save(FlagQuestionCriteria o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		remove(find(id));
	}

	public void remove(FlagQuestionCriteria row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public FlagQuestionCriteria find(int id) {
		return em.find(FlagQuestionCriteria.class, id);
	}

	@SuppressWarnings("unchecked")
	public Map<FlagColor, FlagQuestionCriteria> find(int operatorID, int questionID) {
		// TODO fill this in
		Query query = em.createQuery("SELECT f FROM FlagQuestionCriteria f WHERE operatorAccount.id = ? and auditQuestion.id = ?");
		query.setParameter(1, operatorID);
		query.setParameter(2, questionID);

		List<FlagQuestionCriteria> resultList = query.getResultList();
		if (resultList.size() > 0) {
			Map<FlagColor, FlagQuestionCriteria> resultMap = new TreeMap<FlagColor, FlagQuestionCriteria>();
			for (FlagQuestionCriteria result : resultList) {
				resultMap.put(result.getFlagColor(), result);
			}
			return resultMap;
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	public List<FlagQuestionCriteria> findByOperator(OperatorAccount operator) {
		Query query = em.createQuery("SELECT t FROM FlagQuestionCriteria t "
				+ "WHERE (t.operatorAccount = :flagID AND t.auditQuestion.subCategory.category.auditType.classType <> 'Policy')" +
					" OR (t.operatorAccount = :insureID AND t.auditQuestion.subCategory.category.auditType.classType = 'Policy')");
		query.setParameter("flagID", operator.getInheritFlagCriteria());
		query.setParameter("insureID", operator.getInheritInsuranceCriteria());
		return query.getResultList();
	}

}
