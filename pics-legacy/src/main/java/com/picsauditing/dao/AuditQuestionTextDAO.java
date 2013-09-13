package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditQuestionText;

@SuppressWarnings("unchecked")
public class AuditQuestionTextDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public AuditQuestionText save(AuditQuestionText o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		AuditQuestionText row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditQuestionText find(int id) {
		return em.find(AuditQuestionText.class, id);
	}

	
	public List<AuditQuestionText> findByQuestion(int qid) {
		Query q = em.createQuery("FROM AuditQuestionText t WHERE t.question.id = :qid").setParameter("qid", qid);

		return q.getResultList();
	}

}
