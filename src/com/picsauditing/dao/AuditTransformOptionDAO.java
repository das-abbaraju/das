package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditTransformOption;

@Transactional
@SuppressWarnings("unchecked")
public class AuditTransformOptionDAO extends PicsDAO {
	public AuditTransformOption save(AuditTransformOption o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditTransformOption row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditTransformOption find(int id) {
		return em.find(AuditTransformOption.class, id);
	}


}
