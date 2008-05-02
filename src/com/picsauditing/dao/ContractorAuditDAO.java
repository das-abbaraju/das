package com.picsauditing.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;

@Transactional
public class ContractorAuditDAO extends PicsDAO {
	public ContractorAudit save(ContractorAudit o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		ContractorAudit row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findByContractor(int conID) {
		Query query = em.createQuery("SELECT t FROM ContractorAudit t " +
				"WHERE t.contractorAccount.id = ? " +
				"ORDER BY auditTypeID");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public ContractorAudit findActiveByContractor(int conID, int auditTypeID) {
		Query query = em.createQuery("SELECT t FROM ContractorAudit t "
				+ "WHERE t.contractorAccount.id = ? AND auditType.auditTypeID = ? " + 
				"AND auditStatus IN ('Active','Exempt')");
		query.setParameter(1, conID);
		query.setParameter(2, auditTypeID);
		return (ContractorAudit) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findNonExpiredByContractor(int conID) {
		Query query = em.createQuery("SELECT t FROM ContractorAudit t "
				+ "WHERE t.contractorAccount.id = ? " + 
				"AND auditStatus <> 'Expired'");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findUpcoming(int limit, Permissions permissions) {
		// TODO handle the permissions correctly
		Query query = em.createQuery("FROM ContractorAudit t "
				+ "WHERE scheduledDate > NOW() ORDER BY scheduledDate ASC ");
		query.setMaxResults(limit);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findRecentlyClosed(int limit, Permissions permissions) {
		// TODO handle the permissions correctly
		Query query = em.createQuery("FROM ContractorAudit t "
				+ "WHERE auditStatus = 'Active' AND closedDate < NOW() ORDER BY closedDate DESC");
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAudit find(int id) {
		return em.find(ContractorAudit.class, id);
	}
}
