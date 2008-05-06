package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditCategoryDataDAO extends PicsDAO {
	public AuditCatData save(AuditCatData o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		AuditCatData row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public AuditCatData find(int id) {
		AuditCatData a = em.find(AuditCatData.class, id);
		return a;
	}

	@SuppressWarnings("unchecked")
	public List<AuditCatData> findByAudit(ContractorAudit contractorAudit, Permissions permissions) {
		Query query = em.createQuery("FROM AuditCategory c " +
				"LEFT JOIN AuditCatData d ON d.category = c" +
				"WHERE c.auditType = ? ");
		query.setParameter(1, contractorAudit.getAuditType());
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public void fillAuditCategories(ContractorAudit contractorAudit) {
		Query query = em.createQuery("FROM AuditCategory c " +
				"LEFT JOIN AuditCatData d ON d.category = c" +
				"WHERE c.auditType = ? AND d = null");
		query.setParameter(1, contractorAudit.getAuditType());
		List<AuditCategory> missingCategories = query.getResultList();
		for(AuditCategory category : missingCategories) {
			AuditCatData data = new AuditCatData();
			data.setAudit(contractorAudit);
			data.setCategory(category);
			//data.setApplies(YesNo.Yes);
			save(data);
		}
	}
}
