package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.PermissionQueryBuilder;

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
		remove(row);
	}

	public void remove(ContractorAudit contractorAudit) {
		if (contractorAudit != null)
			em.remove(contractorAudit);
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
				"AND auditStatus <> 'Expired' ORDER BY t.auditType.displayOrder");
		query.setParameter(1, conID);
		return query.getResultList();
	}
	
	public List<ContractorAudit> findNewlyAssigned(int limit, Permissions permissions) {
		return findWhere(limit, "auditStatus IN ('Pending', 'Submitted') AND auditor.id = " + permissions.getUserId(), "assignedDate DESC");
	}

	public List<ContractorAudit> findUpcoming(int limit, Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		return findWhere(limit, "auditStatus = 'Pending' AND scheduledDate IS NOT NULL " + permQuery.toString() + getAuditWhere(permissions), "scheduledDate");
	}

	public List<ContractorAudit> findNew(int limit, Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		return findWhere(limit, "auditStatus IN ('Pending', 'Submitted') " + permQuery.toString() + getAuditWhere(permissions), "createdDate DESC");
	}

	public List<ContractorAudit> findRecentlyClosed(int limit, Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		permQuery.setOnlyPendingAudits(false);
		return findWhere(limit, "auditStatus = 'Active' AND closedDate < NOW() " + permQuery.toString() + getAuditWhere(permissions), "closedDate DESC");
	}
	
	private String getAuditWhere(Permissions permissions) {
		if (permissions.isPicsEmployee())
			return "";
		if (permissions.isContractor())		
			return "";
		if (permissions.getCanSeeAudit() == null)
			return "AND 1=0";
		
		String where = "AND auditType.auditTypeID IN (0";
		for(Integer id : permissions.getCanSeeAudit())
			where += "," + id;
		return where += ")";
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findWhere(int limit, String where, String orderBy) {
		String hql = "FROM ContractorAudit";
		if (where.length() > 0) hql += " WHERE " + where;
		if (orderBy.length() > 0) hql += " ORDER BY " + orderBy;
		System.out.println("compiling: " + hql);
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAudit find(int id) {
		return em.find(ContractorAudit.class, id);
	}
	
	public ContractorAudit addPending(int auditTypeID, ContractorAccount contractor) {
		AuditType auditType = new AuditType();
		auditType.setAuditTypeID(auditTypeID);
		return this.addPending(auditType, contractor);
	}

	public ContractorAudit addPending(AuditType auditType, ContractorAccount contractor) {
		ContractorAudit cAudit = new ContractorAudit();
		cAudit.setContractorAccount(contractor);
		cAudit.setAuditType(auditType);
		return this.save(cAudit);
	}
}
