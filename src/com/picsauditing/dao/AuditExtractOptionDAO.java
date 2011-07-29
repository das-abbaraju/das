package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditExtractOption;

@Transactional
@SuppressWarnings("unchecked")
public class AuditExtractOptionDAO extends PicsDAO {
	public AuditExtractOption save(AuditExtractOption o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditExtractOption row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditExtractOption find(int id) {
		return em.find(AuditExtractOption.class, id);
	}


}
