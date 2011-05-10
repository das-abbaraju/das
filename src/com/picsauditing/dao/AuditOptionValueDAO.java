package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.jpa.entities.AuditOptionValue;

@Transactional
public class AuditOptionValueDAO extends PicsDAO {
	public AuditOptionValue save(AuditOptionValue o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public AuditOptionValue find(int id) {
		Query query = em.createQuery("FROM AuditOptionValue aqo WHERE aqo.id = ?");
		query.setParameter(1, id);
		return (AuditOptionValue) query.getSingleResult();
	}

	public List<AuditOptionGroup> getAllOptionTypes() {
		return findOptionTypeWhere(null);
	}

	public AuditOptionGroup findOptionType(int id) {
		Query query = em.createQuery("SELECT o FROM AuditOptionGroup o WHERE o.id = ?");
		query.setParameter(1, id);
		return (AuditOptionGroup) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<AuditOptionGroup> findOptionTypeWhere(String where) {
		if (where != null && !where.isEmpty())
			where = " WHERE " + where;
		else
			where = "";

		Query query = em.createQuery("SELECT o FROM AuditOptionGroup o" + where);
		// TODO Add sort here
		return query.getResultList();
	}
}