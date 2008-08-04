package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.YesNo;

@Transactional
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
				where += "AND d.applies = 'Yes' "; // Show only the admins the full PQF

			// Contractors can see their full PQF
			if (!permissions.isContractor()) {
				if (!permissions.hasPermission(OpPerms.ViewFullPQF))
					where += "AND d.category.id <> " + AuditCategory.WORK_HISTORY + " ";
				
				if (permissions.isOperator()) {
					where += "AND d.category IN (SELECT o.category FROM AuditCatOperator o " +
							"WHERE o.category.auditType.id = :auditType AND o.riskLevel = :risk AND o.operatorAccount.id = :id) ";
				}
				if (permissions.isCorporate()) {
					where += "AND d.category IN (SELECT o.category FROM AuditCatOperator o " +
							"WHERE o.riskLevel = :risk AND o.operatorAccount IN (" +
							"SELECT operator FROM Facility f WHERE corporate.id = :id)) ";
				}
			}
		}
		
		// There is a strange bug when 
		// If this is an operator or corporate, we already 
		if (!where.contains(":auditType"))
			where += "AND d.category.auditType.id = :auditType ";
		//inner join fetch d.category 
		try {
			String queryString = "SELECT d FROM AuditCatData d " +
				"WHERE d.audit.id = :conAudit " + where +
				" ORDER BY d.category.number";
			Query query = em.createQuery(queryString);
			
			query.setParameter("conAudit", contractorAudit.getId());
			setOptionalParameter(query, "auditType", contractorAudit.getAuditType().getAuditTypeID());
			setOptionalParameter(query, "id", permissions.getAccountId());
			setOptionalParameter(query, "risk", contractorAudit.getContractorAccount().getRiskLevel());
			
			return query.getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			List<AuditCatData> categories = new ArrayList<AuditCatData>();
			AuditCatData data = new AuditCatData();
			data.setAudit(contractorAudit);
			data.setApplies(YesNo.Yes);
			AuditCategory cat = new AuditCategory();
			cat.setCategory("Error Occurred Getting Categories");
			data.setCategory(cat);
			categories.add(data);
			return categories;
		}
	}

	@SuppressWarnings("unchecked")
	public List<AuditCatData> findAllAuditCatData(int auditID, int catID) {
		String selectQuery = "SELECT * FROM pqfCatData "+
		"WHERE catID="+catID+" AND auditID="+auditID;
		Query query = em.createQuery(selectQuery);
		return query.getResultList();
	}




}
