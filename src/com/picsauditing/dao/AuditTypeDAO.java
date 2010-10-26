package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;

@Transactional
public class AuditTypeDAO extends PicsDAO {
	public AuditType save(AuditType o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditType row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditType find(int id) {
		return em.find(AuditType.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<AuditType> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;

		Query query = em
				.createQuery("FROM AuditType t " + where + " ORDER BY t.classType, t.displayOrder, t.auditName");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<AuditType> findAll() {
		return findWhere("");
	}
	
	public List<AuditType> findAll(Permissions permissions, boolean canEdit, AuditTypeClass auditClass) {
		return findAll(permissions, canEdit, auditClass, true);
	}

	@SuppressWarnings("unchecked")
	public List<AuditType> findAll(Permissions permissions, boolean canEdit, AuditTypeClass auditClass, boolean both) {
		String where = "";

		if (permissions.isOperatorCorporate()) {
			if (permissions.isOperator()) {
				where = "operatorAccount.id = ?";
			} else {
				where = "operatorAccount IN (SELECT operator FROM Facility WHERE corporate.id = ?)";
			}
			if (canEdit)
				where += " AND canEdit = 1";
			else
				where += " AND canSee = 1";

			where += " AND minRiskLevel != 1 ";

			where = "WHERE t IN (SELECT auditType FROM AuditOperator WHERE " + where + ")";
		}

		if (auditClass != null) {
			if (where.length() > 0)
				where += " AND ";
			else
				where = "WHERE ";
			if(auditClass.equals(AuditTypeClass.Audit) && both) {
				where += "t.classType IN ('PQF','Audit')";
			}
			else
				where += "t.classType = '" + auditClass.toString() + "'";
		}

		Query query = em
				.createQuery("FROM AuditType t " + where + " ORDER BY t.classType, t.displayOrder, t.auditName");
		if (permissions.isOperatorCorporate())
			query.setParameter(1, permissions.getAccountId());
		return query.getResultList();
	}

	public void updateAllAudits(int auditTypeId) {
		String where = "UPDATE ContractorAudit ca SET ca.lastRecalculation = NULL WHERE ca.auditType.id = ?";
		Query query = em.createQuery(where);
		query.setParameter(1, auditTypeId);
		query.executeUpdate();
	}

	public void updateAllCategories(int auditTypeId, int categoryId) {
		String where = "UPDATE ContractorAudit ca SET ca.lastRecalculation = NULL WHERE EXISTS (SELECT acd.audit FROM ca.categories acd WHERE acd.category.id = :categoryId AND acd.applies = 1) "
				+ " AND ca.auditType.id = :auditTypeId";
		Query query = em.createQuery(where);
		query.setParameter("auditTypeId", auditTypeId);
		query.setParameter("categoryId", categoryId);
		query.executeUpdate();
	}

}
