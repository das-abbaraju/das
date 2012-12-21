package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditDataHistory;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PermissionQueryBuilder;

@SuppressWarnings("unchecked")
public class ContractorAuditDAO extends PicsDAO {

	@Transactional(propagation = Propagation.NESTED)
	public ContractorAudit save(ContractorAudit o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
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

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id, String ftpDir) {
		remove(find(id), ftpDir);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		ContractorAudit row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void copy(ContractorAudit oCAudit, ContractorAccount nContractor,
			Map<Integer, AuditData> preToPostAuditDataIdMapper) {
		if (oCAudit != null) {
			List<AuditData> auList = new Vector<AuditData>(oCAudit.getData());
			List<AuditCatData> acList = new Vector<AuditCatData>(oCAudit.getCategories());
			clear();
			oCAudit.setId(0);
			oCAudit.setContractorAccount(nContractor);
			oCAudit.setManuallyAdded(true);

			oCAudit.getData().clear();
			for (AuditData auditData : auList) {
				preToPostAuditDataIdMapper.put(auditData.getId(), auditData);

				auditData.setId(0);
				auditData.setAudit(oCAudit);
				auditData.setDataHistory(new ArrayList<AuditDataHistory>());
			}
			oCAudit.getData().addAll(auList);

			oCAudit.getCategories().clear();
			for (AuditCatData auditCatData : acList) {
				auditCatData.setId(0);
				auditCatData.setAudit(oCAudit);
			}
			oCAudit.getCategories().addAll(acList);
			oCAudit.setOperators(new ArrayList<ContractorAuditOperator>());
		}
		save(oCAudit);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void copyAuditForNewEmployee(ContractorAudit oCAudit, Employee employee,
			Map<Integer, AuditData> preToPostAuditDataIdMapper) {
		if (oCAudit != null) {
			List<AuditData> auList = new Vector<AuditData>(oCAudit.getData());
			List<AuditCatData> acList = new Vector<AuditCatData>(oCAudit.getCategories());
			List<ContractorAuditOperator> caoList = new Vector<ContractorAuditOperator>(oCAudit.getOperators());

			for (AuditData auditData : auList) {
				preToPostAuditDataIdMapper.put(auditData.getId(), auditData);

				auditData.setId(0);
				auditData.setAudit(oCAudit);
				auditData.setDataHistory(new ArrayList<AuditDataHistory>());
			}
			oCAudit.setData(new ArrayList<AuditData>());
			oCAudit.getData().addAll(auList);

			for (AuditCatData auditCatData : acList) {
				auditCatData.setId(0);
				auditCatData.setAudit(oCAudit);
			}
			oCAudit.setCategories(new ArrayList<AuditCatData>());
			oCAudit.getCategories().addAll(acList);

			for (ContractorAuditOperator cao : caoList) {
				cao.setId(0);
				cao.setAudit(oCAudit);

				List<ContractorAuditOperatorWorkflow> caowList = new Vector<ContractorAuditOperatorWorkflow>(
						cao.getCaoWorkflow());
				List<ContractorAuditOperatorPermission> caopList = new Vector<ContractorAuditOperatorPermission>(
						cao.getCaoPermissions());

				cao.getCaoWorkflow().clear();
				for (ContractorAuditOperatorWorkflow caow : caowList) {
					caow.setId(0);
					caow.setCao(cao);
				}
				cao.setCaoWorkflow(new ArrayList<ContractorAuditOperatorWorkflow>());
				cao.getCaoWorkflow().addAll(caowList);

				cao.getCaoPermissions().clear();
				for (ContractorAuditOperatorPermission caop : caopList) {
					caop.setId(0);
					caop.setCao(cao);
				}
				cao.setCaoPermissions(new ArrayList<ContractorAuditOperatorPermission>());
				cao.getCaoPermissions().addAll(caopList);
			}
			oCAudit.setOperators(new ArrayList<ContractorAuditOperator>());
			oCAudit.getOperators().addAll(caoList);

			clear();
			oCAudit.setId(0);
			oCAudit.setEmployee(employee);
		}
		save(oCAudit);
	}

	public List<ContractorAudit> findByContractor(int conID) {
		Query query = em.createQuery("SELECT t FROM ContractorAudit t " + "WHERE t.contractorAccount.id = ? "
				+ "ORDER BY auditTypeID");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public boolean isNeedsWelcomeCall(int conID) {
		Query query = em.createNativeQuery("SELECT a.id FROM accounts a " +
				"JOIN contractor_info ci ON ci.id = a.id " +
				"LEFT JOIN contractor_audit ca2 ON ca2.conID = a.id AND ca2.auditTypeID = 9 AND ca2.creationDate >= DATE_SUB(ci.membershipDate, INTERVAL 1 YEAR) " +
				"LEFT JOIN contractor_audit ca3 ON ca3.conID = a.id AND ca3.auditTypeID = 9 " +
				"WHERE ca2.id is null " +
				"AND a.type = 'Contractor' " +
				"AND a.status = 'Active' " +
				"AND ci.accountLevel = 'Full' " +
				"AND ci.membershipDate > DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
				"AND a.id=" + conID);
		
		List<Integer> list = query.getResultList();
		if (list.size() > 0)
			return true;
		
		return false;
	}
	
	public List<ContractorAudit> findByAuditType(int conID, AuditTypeClass classType) {
		Query query = em.createQuery("SELECT t FROM ContractorAudit t "
				+ "WHERE t.contractorAccount.id = ? AND t.auditType.classType = '?'" + "ORDER BY auditTypeID");
		query.setParameter(1, conID);
		query.setParameter(2, classType.toString());
		return query.getResultList();
	}

	public List<ContractorAudit> findNonExpiredByContractor(int conID) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAudit t "
						+ "WHERE t.contractorAccount.id = ? "
						+ "AND (expiresDate is null OR expiresDate > Now()) ORDER BY t.auditType.displayOrder, t.auditFor, t.creationDate DESC");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<ContractorAudit> findExpiredByContractor(int conID) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAudit t "
						+ "WHERE t.contractorAccount.id = ? "
						+ "AND (expiresDate < Now()) ORDER BY t.auditType.displayOrder, t.auditFor, t.expiresDate DESC LIMIT 50");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<ContractorAudit> findAuditsNeedingWebcams() {
		Query q = em
				.createQuery("FROM ContractorAudit WHERE scheduledDate > NOW() AND needsCamera = TRUE AND auditLocation = 'Web' ORDER BY scheduledDate");

		return q.getResultList();
	}

	public List<ContractorAudit> findWhere(int limit, String where, String orderBy) {
		String hql = "FROM ContractorAudit";
		if (where.length() > 0)
			hql += " WHERE " + where;
		if (orderBy.length() > 0)
			hql += " ORDER BY " + orderBy;
		Query query = em.createQuery(hql);
		if (limit > 0)
			query.setMaxResults(limit);
		return query.getResultList();
	}

	public ContractorAudit find(int id) {
		return em.find(ContractorAudit.class, id);
	}

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
	 * Returns a list of policies that will expire 14 days from now or today or
	 * expired 7 days ago, where a new pending policy of that type is ready.
	 * 
	 * @return
	 */
	public List<ContractorAudit> findExpiredCertificates(int offset) {
		SelectSQL sql = new SelectSQL("contractor_audit ca");
		sql.addField("ca.*");
		sql.addJoin("JOIN audit_type aty ON ca.auditTypeID = aty.id");
		sql.addJoin("JOIN contractor_audit_operator cao ON ca.id = cao.auditID");
		sql.addWhere("aty.classType = 'Policy'");
		sql.addWhere("(cao.status = 'Pending' AND cao.visible = 1)");
		sql.addWhere("EXISTS (SELECT id FROM contractor_audit ca1 WHERE ca1.auditTypeID = ca.auditTypeID"
				+ " AND ca1.conID = ca.conID AND ca.id > ca1.id AND ca1.expiresDate IN (:Before14, CURDATE(), :After7))");
		sql.addOrderBy("ca.id");
		Query query = em.createNativeQuery(sql.toString(), ContractorAudit.class);
		query.setMaxResults(100);
		query.setFirstResult(offset);

		// today's date
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		// 2 weeks before
		calendar1.add(Calendar.WEEK_OF_YEAR, 2);
		// 1 week after
		calendar2.add(Calendar.WEEK_OF_YEAR, -1);

		query.setParameter("Before14", calendar1.getTime(), TemporalType.DATE);
		query.setParameter("After7", calendar2.getTime(), TemporalType.DATE);
		return query.getResultList();
	}

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

	public List<ContractorAudit> findScheduledAudits(int auditorID, Date startDate, Date endDate) {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ " WHERE ca.auditType.scheduled = true AND ca.scheduledDate >= :startDate AND ca.scheduledDate <= :endDate "
				+ " AND ca.contractorAccount.status = 'Active'";
		if (auditorID > 0)
			hql += " AND ca.auditor.id = :auditorID";
		hql += " AND ca IN (SELECT cao.audit FROM ca.operators cao where cao.status != 'NotApplicable' AND cao.visible = 1)";
		hql += " ORDER BY ca.scheduledDate, ca.id";
		Query query = em.createQuery(hql);

		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		if (auditorID > 0)
			query.setParameter("auditorID", auditorID);

		return query.getResultList();
	}

	public List<ContractorAudit> findScheduledAudits(int auditorID, Date startDate, Date endDate,
			Permissions permissions) {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ " WHERE ca.auditType.scheduled = true AND ca.scheduledDate >= :startDate AND ca.scheduledDate <= :endDate "
				+ " AND ca.contractorAccount.status = 'Active'";
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

	public List<ContractorAudit> findScheduledAuditsByAuditId(int auditId, Date startDate, Date endDate) {
		String hql = "SELECT ca FROM ContractorAudit ca "
				+ " WHERE ca.auditType.scheduled = true AND ca.scheduledDate >= :startDate AND ca.scheduledDate <= :endDate "
				+ " AND ca.contractorAccount.status = 'Active'" + " AND ca.auditType.id = :auditId ";
		hql += " AND ca IN (SELECT cao.audit FROM ca.operators cao where cao.status != 'NotApplicable' AND cao.visible = 1)";
		hql += " ORDER BY ca.scheduledDate, ca.id";
		Query query = em.createQuery(hql);
		query.setParameter("auditId", auditId);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return query.getResultList();
	}

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

	public List<BasicDynaBean> findAuditedContractorsByCountrySubdivisionCount() {
		List<BasicDynaBean> data = null;

		try {
			Report report = new Report();
			SelectSQL sql = new SelectSQL("contractor_audit ca");
			sql.addField("a.country");
			sql.addField("a.countrySubdivision");
			sql.addField("COUNT(DISTINCT ca.conID) AS cnt");
			sql.addJoin("JOIN users u ON ca.auditorID = u.id");
			sql.addJoin("JOIN accounts a ON ca.conID = a.id AND a.status = 'Active'");
			sql.addJoin("JOIN contractor_audit_operator cao ON ca.id = cao.auditID AND cao.visible = 1");
			sql.addWhere("ca.auditorID IS NOT NULL");
			sql.addWhere("ca.auditTypeID IN (2,3)");
			sql.addWhere("cao.status NOT IN ('NotApplicable')");
			sql.addGroupBy("a.country, a.countrySubdivision");
			sql.addOrderBy("a.country, a.countrySubdivision");
			report.setSql(sql);

			data = report.getPage(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public List<BasicDynaBean> findAuditByID(int auditID) {
		List<BasicDynaBean> data = null;

		try {
			Report report = new Report();
			SelectSQL sql = new SelectSQL("contractor_audit ca");
			sql.addField("*");
			sql.addWhere("id = " + auditID);
			report.setSql(sql);

			data = report.getPage(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public List<ContractorAudit> findCancelledScheduledAudits() {
		String sql = "SELECT * " + "FROM contractor_audit ca " + "WHERE ca.scheduledDate > NOW() " + "AND (NOT EXISTS "
				+ "(SELECT 'x' " + "FROM   contractor_audit_operator cao "
				+ "WHERE  ca.id = cao.auditID AND cao.visible = 1) " + ")";
		Query query = em.createNativeQuery(sql, ContractorAudit.class);

		return query.getResultList();
	}

	public List<ContractorAudit> findUpcomingScheduledAudits(int auditTypeId) {
		String hql = "SELECT ca FROM ContractorAudit ca " + "WHERE ca.auditType.id = :auditTypeID "
				+ "AND ca.contractorAccount.status='Active' " + "AND ca.scheduledDate < DATEADD";
		Query query = em.createQuery(hql);
		query.setParameter("auditTypeID", auditTypeId);
		return query.getResultList();
	}

	public ContractorAudit findPQF(int id) {
		String hql = "SELECT ca FROM ContractorAudit ca " + "WHERE ca.contractorAccount.id = " + id
				+ " AND ca.auditType.id = 1 " + " AND ca.expiresDate > NOW() ";

		return (ContractorAudit) em.createQuery(hql).getSingleResult();
	}
}
