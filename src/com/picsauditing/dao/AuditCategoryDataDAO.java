package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
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
		String where = "";
		if (contractorAudit.getAuditType().getAuditTypeID() == AuditType.PQF) {
			// This is a PQF, so it has special query criteria
			if (!permissions.isAdmin()) // TODO change this to be a permission
				where += "AND applies = 'Yes' "; // Only show the admins the full PQF

			// Contractors can see their full PQF
			if (!permissions.isContractor()) {
				if (!permissions.hasPermission(OpPerms.ViewFullPQF))
					where += "AND d.category.id <> " + AuditCategory.WORK_HISTORY + " ";
				
				if (permissions.isOperator()) {
					where += "AND d.category IN (SELECT category FROM PqfOperator o " +
							"WHERE o.riskLevel = :risk AND o.operatorAccount.id = :id) ";
				}
				if (permissions.isCorporate()) {
					where += "AND d.category IN (SELECT category FROM PqfOperator o " +
							"WHERE o.riskLevel = :risk AND o.operatorAccount IN (" +
							"SELECT operator FROM Facility f WHERE corporate.id = :id)) ";
				}
			}
		}
		
		Query query = em.createQuery(" select d FROM AuditCatData d inner join fetch d.category " +
				"WHERE d.audit.id = :conAudit AND d.category.auditType.id = :auditType " + where +
				"ORDER BY d.category.number");
		
		query.setParameter("conAudit", contractorAudit.getId());
		query.setParameter("auditType", contractorAudit.getAuditType().getAuditTypeID());
		setOptionalParameter(query, "id", permissions.getAccountId());
		setOptionalParameter(query, "risk", contractorAudit.getContractorAccount().getRiskLevel());
		
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
