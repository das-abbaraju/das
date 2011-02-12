package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditQuestionOption;

@Transactional
public class AuditQuestionOptionDAO extends PicsDAO {

	public AuditQuestionOption save(AuditQuestionOption o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
}
