package com.picsauditing.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
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
			if (auditData.getQuestion().getQuestionType().equals("File")) {
				String FileName = ftpDir + "/files/pqf/qID_"
						+ auditData.getQuestion().getId() + "/"
						+ auditData.getQuestion().getId() + "_"
						+ row.getContractorAccount().getId() + "."
						+ auditData.getAnswer();
				FileUtils.deleteFile(FileName);
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

	public void copy(ContractorAudit oCAudit, ContractorAccount nContractor) {
		if (oCAudit != null) {
			List<AuditData> auList = new Vector<AuditData>(oCAudit.getData());
			List<AuditCatData> acList = new Vector<AuditCatData>(oCAudit
					.getCategories());
			clear();
			oCAudit.setId(0);
			oCAudit.setContractorAccount(nContractor);

			oCAudit.getData().clear();
			for (AuditData auditData : auList) {
				auditData.setDataID(0);
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
						+ "WHERE t.contractorAccount.id = ? AND auditType.auditTypeID = ? "
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
						+ "AND auditStatus <> 'Expired' ORDER BY t.auditType.displayOrder");
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
				"createdDate DESC");
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

	private String getAuditWhere(Permissions permissions) {
		if (permissions.isPicsEmployee())
			return "";
		if (permissions.isContractor())
			return "";
		if (permissions.getCanSeeAudit() == null)
			return "AND 1=0";

		String where = "AND auditType.auditTypeID IN (0";
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

	public ContractorAudit addPending(int auditTypeID,
			ContractorAccount contractor) {
		AuditType auditType = new AuditType();
		auditType.setAuditTypeID(auditTypeID);
		return this.addPending(auditType, contractor);
	}

	public ContractorAudit addPending(AuditType auditType,
			ContractorAccount contractor) {
		return addPending(auditType, contractor, null, null);
	}

	public ContractorAudit addPending(AuditType auditType,
			ContractorAccount contractor, String auditFor, Date startDate) {
		ContractorAudit cAudit = new ContractorAudit();
		cAudit.setContractorAccount(contractor);
		cAudit.setAuditType(auditType);
		cAudit.setAuditFor(auditFor);

		if (startDate != null) {
			Date dateToExpire = DateBean.addMonths(startDate, auditType
					.getMonthsToExpire());
			cAudit.setExpiresDate(dateToExpire);
		}
		return this.save(cAudit);
	}

	@SuppressWarnings("unchecked")
	public List<ContractorAccount> findContractorsWithExpiringAudits() {
		int startDay = 59; // between 50 and 70 days in the future
		int range = 10;

		String hql = "SELECT DISTINCT ca.contractorAccount FROM ContractorAudit ca "
				+ "WHERE ca.auditType.auditTypeID > 1 AND ca.auditType.hasMultiple = 0 "
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

	// returns true if the percent verified becomes 100 and the status is
	// changed
	public void calculateVerifiedPercent(ContractorAudit conAudit) {

		int verified = 0;
		int verifiedTotal = 0;

		if (conAudit.getAuditType().isAnnualAddendum()) {

			for (OshaAudit oshaAudit : conAudit.getOshas()) {
				if (oshaAudit != null && oshaAudit.isCorporate()) {
					if ((oshaAudit.isVerified() || !oshaAudit.isApplicable())) {
						verified++;
					}
					verifiedTotal++;
				}
			}

			for (AuditData auditData : conAudit.getData()) {
				// either the pqf or the EMF for the annual addendum
				if (auditData.isVerified()) {
					verified++;
				}

				verifiedTotal++;
			}
		}

		if (conAudit.getAuditType().isPqf()) {

			
			
			
			
		}

		conAudit.setPercentVerified(Math.round((float) (100 * verified)
				/ verifiedTotal));


		save(conAudit);
	}

}
