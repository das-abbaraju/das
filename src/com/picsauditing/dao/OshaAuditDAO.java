package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;

@Transactional
public class OshaAuditDAO extends PicsDAO {

	public OshaAudit save(OshaAudit o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OshaAudit row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public OshaAudit find(int id) {
		return em.find(OshaAudit.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<OshaAudit> findByContractor(ContractorAudit conAudit) {
		Query query = em.createQuery("SELECT o FROM OshaAudit o " + "WHERE o.conAudit = ? "
				+ "ORDER BY id");
		query.setParameter(1, conAudit);
		return query.getResultList();

	}

}
