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
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PermissionQueryBuilder;

@Transactional
public class ContractorAuditDAO extends IndexableDAO {

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
				String Filepath = ftpDir + "/files/" + FileUtils.thousandize(auditData.getId());
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

	public void copy(ContractorAudit oCAudit, ContractorAccount nContractor,
			Map<Integer, AuditData> preToPostAuditDataIdMapper) {
		if (oCAudit != null) {
			List<AuditData> auList = new Vector<AuditData>(oCAudit.getData());
			List<AuditCatData> acList = new Vector<AuditCatData>(oCAudit.getCategories());
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
		Query query = em.createQuery("SELECT t FROM ContractorAudit t " + "WHERE t.contractorAccount.id = ? "
				+ "ORDER BY auditTypeID");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findNonExpiredByContractor(int conID) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAudit t "
						+ "WHERE t.contractorAccount.id = ? "
						+ "AND expiresDate > Now() ORDER BY t.auditType.displayOrder, t.auditType.auditName, t.auditFor, t.creationDate DESC");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findAuditsNeedingWebcams() {
		Query q = em
				.createQuery("FROM ContractorAudit WHERE scheduledDate > NOW() AND needsCamera = TRUE AND auditLocation = 'Web' ORDER BY scheduledDate");

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findWhere(int limit, String where, String orderBy) {
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
				+ "AND ca.expiresDate BETWEEN :startDate AND :endDate " + "ORDER BY ca.expiresDate";
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
	 * The final result of all of this logic below is that we send emails: 14
	 * days before it expires 7 days after it expires 26 days after it expires
	 * for a total of 3 emails
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findExpiredCertificates() {
		String hql = "SELECT ca FROM ContractorAudit ca " + "WHERE ca.auditType.classType = 'Policy' "
				+ "AND ca IN (SELECT cao.audit FROM ca.operators cao where cao.status = 'Pending') "
				+ "AND (ca.contractorAccount NOT IN (SELECT contractorAccount FROM EmailQueue et "
				+ "WHERE et.sentDate > :Before14Days " + "AND et.emailTemplate.id = 10)" + ") " + "AND EXISTS ( "
				+ "SELECT ca2 FROM ContractorAudit ca2 " + "WHERE ca.auditType = ca2.auditType "
				+ "AND ca.contractorAccount = ca2.contractorAccount " + "AND ca.id > ca2.id "
				+ "AND ca2.expiresDate BETWEEN :Before14Days AND :After26Days " + ") "
				+ "AND ca.contractorAccount.status = 'Active' " + "ORDER BY ca.contractorAccount";
		Query query = em.createQuery(hql);
		query.setMaxResults(100);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.WEEK_OF_YEAR, -2);
		query.setParameter("Before14Days", calendar1.getTime());
		calendar1.add(Calendar.DATE, 40);
		query.setParameter("After26Days", calendar1.getTime());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findAuditsByOperator(int opID, int auditTypeID, WaitingOn waitingOnStatus) {
		String hql = "SELECT ca FROM ContractorAudit ca " + "WHERE ca.auditType.id = :auditTypeID "
				+ "AND ca.contractorAccount IN (" + "SELECT contractorAccount FROM ContractorOperator co "
				+ "WHERE co.operatorAccount.id = :opID "
				+ ((waitingOnStatus != null) ? "AND co.waitingOn = :waitingOnStatus " : "") + ")"
				+ " ORDER BY ca.creationDate ASC";
		Query query = em.createQuery(hql);
		query.setParameter("auditTypeID", auditTypeID);
		query.setParameter("opID", opID);
		if (waitingOnStatus != null)
			query.setParameter("waitingOnStatus", waitingOnStatus);
		query.setMaxResults(100);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findAuditsNeedingRecalculation() {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ "WHERE (ca.lastRecalculation IS NULL OR ca.lastRecalculation < :threeMonthsAgo) "
				+ " AND ca.expiresDate > NOW()";
		Query query = em.createQuery(hql);
		query.setMaxResults(100);

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.DAY_OF_YEAR, -90);
		query.setParameter("threeMonthsAgo", calendar.getTime());

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findScheduledAudits(int auditorID, Date startDate, Date endDate) {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ " WHERE ca.auditType.scheduled = true AND ca.scheduledDate >= :startDate AND ca.scheduledDate <= :endDate";
		if (auditorID > 0)
			hql += " AND ca.auditor.id = :auditorID";
		// For now, we'll show all scheduled audits. We don't want to just pull
		// a audit that was previously scheduled without warning
		// hql +=
		// " AND ca IN (SELECT cao.audit FROM ca.operators cao where cao.status IN ('Pending','Submitted','Complete') AND cao.visible = 1)";
		hql += " ORDER BY ca.scheduledDate, ca.id";
		Query query = em.createQuery(hql);

		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		if (auditorID > 0)
			query.setParameter("auditorID", auditorID);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAudit> findScheduledAudits(int auditorID, Date startDate, Date endDate,
			Permissions permissions) {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ " WHERE ca.auditType.scheduled = true AND ca.scheduledDate >= :startDate AND ca.scheduledDate <= :endDate ";
		if (auditorID > 0)
			hql += " AND ca.auditor.id = :auditorID ";
		hql += " AND ca IN (SELECT cao.audit FROM ca.operators cao where cao.status != 'NotApplicable' AND cao.visible = 1)";
		if (permissions.isOperatorCorporate()) {
			PermissionQueryBuilder pqb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
			pqb.setAccountAlias("ca.contractorAccount");
			hql += pqb.toString() + " AND ca IN (SELECT caop.cao.audit FROM ContractorAuditOperatorPermission caop "
					+ "WHERE caop.operator.id = " + permissions.getAccountId() + ")";
		}
		hql += " ORDER BY ca.scheduledDate, ca.id";
		Query query = em.createQuery(hql);

		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		if (auditorID > 0)
			query.setParameter("auditorID", auditorID);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List findAuditorBatches(int auditorID, Date startDate) {
		String hql = "SELECT NEW MAP(ca.paidDate as paidDate, ca.auditor as auditor, COUNT(*) as total) FROM ContractorAudit ca WHERE ";
		if (auditorID > 0) {
			hql += "ca.auditor.id = :ID AND ";
		} else {
			hql += "ca.auditor IN (SELECT user FROM UserGroup WHERE group.id = :ID) AND ";
		}

		hql += "ca.paidDate > :startDate " + "GROUP BY ca.paidDate, ca.auditor.id "
				+ "ORDER BY ca.paidDate DESC, ca.auditor.name";

		Query q = em.createQuery(hql);
		if (auditorID > 0)
			q.setParameter("ID", auditorID);
		else
			q.setParameter("ID", User.INDEPENDENT_CONTRACTOR);
		q.setParameter("startDate", startDate);
		return q.getResultList();
	}
}
