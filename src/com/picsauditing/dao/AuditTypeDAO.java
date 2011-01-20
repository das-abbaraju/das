package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public List<AuditType> findAll() {
		return findWhere("");
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

	public Map<AuditTypeClass, List<AuditType>> getAuditTypeMap() {
		Map<AuditTypeClass, List<AuditType>> auditTypeMap = new TreeMap<AuditTypeClass, List<AuditType>>();
	
		for (AuditType auditType : findAll()) {
			if (auditTypeMap.get(auditType.getClassType()) == null)
				auditTypeMap.put(auditType.getClassType(), new ArrayList<AuditType>());
			auditTypeMap.get(auditType.getClassType()).add(auditType);
		}
		return auditTypeMap;
	}

}
