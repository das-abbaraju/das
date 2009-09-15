package com.picsauditing.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.FileUtils;
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

	public void remove(ContractorAudit row, String ftpDir) {
		for (AuditData auditData : row.getData()) {
			if (auditData.getQuestion().getQuestionType().startsWith("File")) {
				String Filepath = ftpDir + "/files/"
				       	+ FileUtils.thousandize(auditData.getId());
				String FileName = "data_" + auditData.getId() + "." + auditData.getAnswer();
				FileUtils.deleteFile(Filepath + FileName);
			}
		}
		remove(row);
	}

	public void remove(int id, String ftpDir) {
		remove(find(id), ftpDir);
	}

	public void remove(int id) {
		ContractorAudit row = find(id);
		remove(row);
	}

	public void remove(ContractorAudit row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void copy(ContractorAudit oCAudit, ContractorAccount nContractor, Map<Integer, AuditData> preToPostAuditDataIdMapper) {
		if (oCAudit != null) {
			List<AuditData> auList = new Vector<AuditData>(oCAudit.getData());
			List<AuditCatData> acList = new Vector<AuditCatData>(oCAudit
					.getCategories());
			clear();
			oCAudit.setId(0);
			oCAudit.setContractorAccount(nContractor);

			oCAudit.getData().clear();
			for (AuditData auditData : auList) {
				preToPostAuditDataIdMapper.put(auditData.getId(), auditData);
				
				auditData.setId(0);
				auditData.setAudit(oCAudit);
			}
			oCAudit.getData().addAll(auList);

			oCAudit.getCategories().clear();
			for (AuditCatData auditCatData : acList) {
				auditCatData.setId(0);
				auditCatData.setAudit(oCAudit);
			}
			oCAudit.getCategories().addAll(acList);
		}
		save(oCAudit);
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findByContractor(int conID) {
		Query query = em.createQuery("SELECT t FROM ContractorAudit t "
				+ "WHERE t.contractorAccount.id = ? " + "ORDER BY auditTypeID");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public ContractorAudit findActiveByContractor(int conID, int auditTypeID) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAudit t "
						+ "WHERE t.contractorAccount.id = ? AND auditType.id = ? "
						+ "AND auditStatus IN ('Active','Exempt')");
		query.setParameter(1, conID);
		query.setParameter(2, auditTypeID);
		return (ContractorAudit) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findNonExpiredByContractor(int conID) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAudit t "
						+ "WHERE t.contractorAccount.id = ? "
						+ "AND auditStatus <> 'Expired' ORDER BY t.auditType.displayOrder, t.auditFor, t.creationDate DESC");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<ContractorAudit> findNewlyAssigned(int limit,
			Permissions permissions) {
		return findWhere(limit,
				"auditStatus IN ('Pending', 'Submitted') AND auditor.id = "
						+ permissions.getUserId(), "assignedDate DESC");
	}

	public List<ContractorAudit> findUpcoming(int limit, Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(
				permissions, PermissionQueryBuilder.HQL);
		return findWhere(limit,
				"auditStatus = 'Pending' AND scheduledDate IS NOT NULL "
						+ permQuery.toString() + getAuditWhere(permissions),
				"scheduledDate");
	}

	public List<ContractorAudit> findNew(int limit, Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(
				permissions, PermissionQueryBuilder.HQL);
		return findWhere(limit, "auditStatus IN ('Pending', 'Submitted') "
				+ permQuery.toString() + getAuditWhere(permissions),
				"creationDate DESC");
	}

	public List<ContractorAudit> findRecentlyClosed(int limit,
			Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(
				permissions, PermissionQueryBuilder.HQL);
		permQuery.setOnlyPendingAudits(false);
		return findWhere(limit,
				"auditStatus = 'Active' AND closedDate < NOW() "
						+ permQuery.toString() + getAuditWhere(permissions),
				"closedDate DESC");
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findAuditsNeedingWebcams() {
		Query q = em.createQuery("FROM ContractorAudit WHERE scheduledDate > NOW() ORDER BY scheduledDate");

		return q.getResultList();
	}

	private String getAuditWhere(Permissions permissions) {
		if (permissions.isPicsEmployee())
			return "";
		if (permissions.isContractor())
			return "";
		if (permissions.getCanSeeAudit() == null)
			return "AND 1=0";

		String where = "AND auditType.id IN (0";
		for (Integer id : permissions.getCanSeeAudit())
			where += "," + id;
		return where += ")";
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findWhere(int limit, String where,
			String orderBy) {
		String hql = "FROM ContractorAudit";
		if (where.length() > 0)
			hql += " WHERE " + where;
		if (orderBy.length() > 0)
			hql += " ORDER BY " + orderBy;
		System.out.println("compiling: " + hql);
		Query query = em.createQuery(hql);
		query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAudit find(int id) {
		return em.find(ContractorAudit.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAccount> findContractorsWithExpiringAudits() {
		int startDay = 59; // between 50 and 70 days in the future
		int range = 10;

		String hql = "SELECT DISTINCT ca.contractorAccount FROM ContractorAudit ca "
				+ "WHERE ca.auditType.id > 1 AND ca.auditType.hasMultiple = 0 "
				+ "AND ca.expiresDate BETWEEN :startDate AND :endDate "
				+ "ORDER BY ca.expiresDate";
		Query query = em.createQuery(hql);
		query.setMaxResults(100);
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_YEAR, startDay);
		query.setParameter("startDate", today.getTime());
		today.add(Calendar.DAY_OF_YEAR, range);
		query.setParameter("endDate", today.getTime());

		return query.getResultList();
	}

	/**
	 * This is for getting a list of policies that we need to send emails on.
	 * The final result of all of this logic below is that we send emails:
	 * 14 days before it expires
	 * 7 days after it expires
	 * 26 days after it expires
	 * for a total of 3 emails
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findExpiredCertificates() {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ "WHERE ca.auditType.classType = 'Policy' "
				+ "AND ca IN (SELECT cao.audit FROM ca.operators cao where cao.status = 'Pending') " 
				+ "AND (ca.contractorAccount NOT IN (SELECT contractorAccount FROM EmailQueue et " 
				+ "WHERE et.sentDate > :Before14Days "
				+ "AND et.emailTemplate.id = 10)"
				+ ") "
				+ "AND EXISTS ( "
				+ "SELECT ca2 FROM ContractorAudit ca2 "
				+ "WHERE ca.auditType = ca2.auditType "
				+ "AND ca.contractorAccount = ca2.contractorAccount "
				+ "AND ca.id > ca2.id "
				+ "AND ca2.expiresDate BETWEEN :Before14Days AND :After26Days "
				+ ") "
				+ "ORDER BY ca.contractorAccount";
		Query query  = em.createQuery(hql);
		query.setMaxResults(100);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(calendar1.WEEK_OF_YEAR, -2);
		query.setParameter("Before14Days", calendar1.getTime());
		calendar1.add(calendar1.DATE, 40);
		query.setParameter("After26Days", calendar1.getTime());
		return query.getResultList();
	}
	
	public List<ContractorAudit> findAuditsNeedingRecalculation() {
		String hql = "SELECT ca FROM ContractorAudit ca " +
				"WHERE (" +
				" ca.lastRecalculation IS NULL " +
				" OR ca.lastRecalculation < :threeMonthsAgo " +
				") ORDER BY ca.auditType.id, ca.contractorAccount.lastLogin DESC";
		Query query = em.createQuery(hql);
		query.setMaxResults(10);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.DAY_OF_YEAR, -90);
		query.setParameter("threeMonthsAgo", calendar.getTime());
		
		return query.getResultList();
	}

	public List<ContractorAudit> findScheduledAudits(Date startDate, Date endDate) {
		
		String hql = "SELECT ca FROM ContractorAudit ca " +
			" WHERE ca.scheduledDate >= :startDate AND ca.scheduledDate <= :endDate" +
			" ORDER BY ca.scheduledDate, ca.id";
		Query query = em.createQuery(hql);
		
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		
		return query.getResultList();
	}

}
