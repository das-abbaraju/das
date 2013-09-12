package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.search.Database;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class ContractorAuditOperatorDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public ContractorAuditOperator save(ContractorAuditOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void insert(ContractorAuditOperatorPermission caop) {
		em.persist(caop);
	}

	/**
	 * CAOPs don't extend BaseTable, so we have to have a custom remove method here.
	 * 
	 * @param caop
	 */
	@Transactional(propagation = Propagation.NESTED)
	public void remove(ContractorAuditOperatorPermission caop) {
		em.remove(caop);
	}

	public ContractorAuditOperator find(int id) {
		return em.find(ContractorAuditOperator.class, id);
	}

	public ContractorAuditOperator find(int auditId, int operatorId) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAuditOperator t WHERE t.audit.id = ? AND t.operator.id = ? ");
		query.setParameter(1, auditId);
		query.setParameter(2, operatorId);

		try {
			return (ContractorAuditOperator) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public List<ContractorAuditOperator> find(List<Integer> caoIDs) {
		Query query = em.createQuery("SELECT t FROM ContractorAuditOperator t WHERE t.id IN ("
				+ Strings.implode(caoIDs) + ")");

		return query.getResultList();
	}

	public List<ContractorAuditOperator> findByContractorOperator(int conID, int opID) {
		String query = "FROM ContractorAuditOperator cao WHERE cao.audit.contractorAccount.id = :conID"
				+ " AND operator.id = :opID";

		Query q = em.createQuery(query);
		q.setParameter("conID", conID);
		q.setParameter("opID", opID);

		return q.getResultList();
	}

	public List<ContractorAuditOperator> find(int opID, AuditStatus status, Date start, Date end) {
		String query = "FROM ContractorAuditOperator WHERE operator.id = :opID AND status = :status AND ";
		if (status.isPending()) {
			query += "creationDate ";
		} else {
			query += "statusChangedDate ";
		}
		query += "BETWEEN :start AND :end";

		Query q = em.createQuery(query);
		q.setParameter("opID", opID);
		q.setParameter("status", status);
		q.setParameter("start", start, TemporalType.TIMESTAMP);
		q.setParameter("end", end, TemporalType.TIMESTAMP);

		return q.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		ContractorAuditOperator row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(ContractorAuditOperator row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void refresh(ContractorAuditOperator row) {
		if (row != null && row.getId() != 0)
			em.refresh(row);
	}

	public static void saveNoteAndEmail(ContractorAuditOperator cao, Permissions permissions) {
		// TODO Make sure this is moved over properly to work flow steps
		/*
		 * if (!cao.getStatus().isTemporary()) { try { EmailBuilder emailBuilder = new EmailBuilder(); // Insurance
		 * Approval Status Change emailBuilder.setTemplate(33); emailBuilder.setPermissions(permissions);
		 * emailBuilder.setFromAddress("\"" + permissions.getName() + "\"<" + permissions.getEmail() + ">");
		 * emailBuilder.setContractor(cao.getAudit().getContractorAccount(), OpPerms.ContractorSafety);
		 * emailBuilder.addToken("cao", cao); EmailQueue email = emailBuilder.build();
		 * email.setViewableBy(cao.getOperator().getTopAccount()); EmailSender.send(email); } catch (Exception e) {
		 * e.printStackTrace(); } }
		 * 
		 * Note note = new Note(); note.setAuditColumns(permissions);
		 * note.setAccount(cao.getAudit().getContractorAccount()); note.setViewableByOperator(permissions);
		 * note.setCanContractorView(true); note.setNoteCategory(NoteCategory.Insurance);
		 * note.setSummary(cao.getAudit().getAuditType().getAuditName() + " status changed to " + cao.getStatus() +
		 * " for " + cao.getOperator().getName());
		 * 
		 * NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO"); noteDAO.save(note);
		 */
	}

	public List<ContractorAuditOperator> findByCaoStatus(int limit, Permissions perm, String where, String orderBy) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(perm, PermissionQueryBuilder.HQL);
		permQuery.setAccountAlias("ca.contractorAccount");
		String query = "SELECT DISTINCT cao FROM ContractorAudit as ca LEFT JOIN ca.operators AS cao ";

		if (perm.isOperatorCorporate()) {
			query += " LEFT JOIN cao.caoPermissions AS caop ";
		}
		query += " WHERE cao.visible = 1 " + permQuery.toString() + " AND " + where;

		if (perm.isOperatorCorporate()) {
			Set<Integer> opIds = new HashSet<Integer>();
			if (perm.isOperator()) {
				opIds.add(perm.getAccountId());
			} else
				opIds.addAll(perm.getOperatorChildren());
			query += " AND caop.operator.id IN (" + Strings.implode(opIds, ",") + ")";
		}

		if (!Strings.isEmpty(orderBy))
			query += " ORDER BY " + orderBy;
		Query q = em.createQuery(query);
		q.setMaxResults(limit);
		return q.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public void resetRenewableAudits() throws SQLException {
		String sql = "";
		String where = "ca.expiresDate BETWEEN DATE_SUB(NOW(),interval 8 day) AND NOW() and atype.renewable = 1";
		Database db = new Database();
		// post contractor audit workflow for renewable audits
		sql = "INSERT INTO contractor_audit_operator_workflow (createdBy,updatedBy,creationDate,updateDate,caoID,status,previousStatus) "
				+ "SELECT 1,1,NOW(),NOW(),cao.id,'Pending',cao.status FROM contractor_audit ca "
				+ "JOIN contractor_audit_operator cao ON cao.auditid = ca.id "
				+ "JOIN audit_type atype ON atype.id = ca.audittypeid WHERE " + where;
		db.executeInsert(sql);

		// update the status for caos for renewable audits
		sql = "UPDATE contractor_audit_operator cao JOIN contractor_audit ca ON ca.id = cao.auditID "
				+ "JOIN audit_type atype ON ca.auditTypeID = atype.id "
				+ "SET cao.status = 'Pending', cao.statusChangedDate = NOW(), ca.expiresDate = NULL WHERE " + where;
		db.executeUpdate(sql);
	}
}
