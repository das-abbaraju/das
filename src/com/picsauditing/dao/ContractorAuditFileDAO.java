package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAuditFile;

@Transactional
public class ContractorAuditFileDAO extends PicsDAO {

	public ContractorAuditFile save(ContractorAuditFile o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public ContractorAuditFile find(int id) {
		return em.find(ContractorAuditFile.class, id);
	}

	public void remove(int id) {
		ContractorAuditFile row = find(id);
		if (row != null)
			remove(row);
	}

	public void remove(ContractorAuditFile row) {
		if (row != null)
			em.remove(row);
	}
	
	@SuppressWarnings("unchecked")
	public List<ContractorAuditFile> findByAudit(int auditID) {
		Query q = em.createQuery("SELECT caf FROM ContractorAuditFile caf WHERE caf.audit.id = ? ");
		q.setParameter(1, auditID);
		return q.getResultList();
	}

}
