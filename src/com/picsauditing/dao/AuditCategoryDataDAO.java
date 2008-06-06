package com.picsauditing.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

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
					where += "AND d.category IN (SELECT o.category FROM PqfOperator o " +
							"WHERE o.category.auditType.id = :auditType AND o.riskLevel = :risk AND o.operatorAccount.id = :id) ";
				}
				if (permissions.isCorporate()) {
					where += "AND d.category IN (SELECT o.category FROM PqfOperator o " +
							"WHERE o.category.auditType.id = :auditType AND o.riskLevel = :risk AND o.operatorAccount IN (" +
							"SELECT operator FROM Facility f WHERE corporate.id = :id)) ";
				}
			}
		}
		
		// There is a strange bug when 
		// If this is an operator or corporate, we already 
		if (!where.contains(":auditType"))
			where += "AND d.category.auditType.id = :auditType ";
		//inner join fetch d.category 
		String queryString = "SELECT d FROM AuditCatData d " +
			"WHERE d.audit.id = :conAudit " + where +
			" ORDER BY d.category.number";
		Query query = em.createQuery(queryString);
		
		query.setParameter("conAudit", contractorAudit.getId());
		setOptionalParameter(query, "auditType", contractorAudit.getAuditType().getAuditTypeID());
		setOptionalParameter(query, "id", permissions.getAccountId());
		setOptionalParameter(query, "risk", contractorAudit.getContractorAccount().getRiskLevel());
		
		return query.getResultList();
	}
	
	public void findByAuditTest() {
		String where = "";
		String queryString;
		Query query;

		queryString = "SELECT d FROM AuditCatData d inner join fetch d.category " +
			"WHERE d.audit.id = :conAudit AND d.category.auditType.id = :auditType " + where +
			" ORDER BY d.category.number";
		query = em.createQuery(queryString);
		
		where += "AND d.applies = 'Yes' "; // Only show the admins the full PQF

		queryString = "SELECT d FROM AuditCatData d inner join fetch d.category " +
			"WHERE d.audit.id = :conAudit AND d.category.auditType.id = :auditType " + where +
			" ORDER BY d.category.number";
		query = em.createQuery(queryString);
	
		if (true)
			where += "AND d.category.id <> " + AuditCategory.WORK_HISTORY + " ";
		
		queryString = "SELECT d FROM AuditCatData d inner join fetch d.category " +
			"WHERE d.audit.id = :conAudit AND d.category.auditType.id = :auditType " + where +
			" ORDER BY d.category.number";
		query = em.createQuery(queryString);
		if (true) {
			where += "AND d.category IN (SELECT o.category FROM PqfOperator o " +
					"WHERE o.riskLevel = :risk AND o.operatorAccount.id = :id) ";
		}
		queryString = "SELECT d FROM AuditCatData d inner join fetch d.category " +
			"WHERE d.audit.id = :conAudit AND d.category.auditType.id = :auditType " + where +
			" ORDER BY d.category.number";
		query = em.createQuery(queryString);
	
		if (true) {
			where += "AND d.category IN (SELECT o.category FROM PqfOperator o " +
					"WHERE o.riskLevel = :risk AND o.operatorAccount IN (" +
					"SELECT operator FROM Facility f WHERE corporate.id = :id)) ";
		}
		queryString = "SELECT d FROM AuditCatData d inner join fetch d.category " +
			"WHERE d.audit.id = :conAudit AND d.category.auditType.id = :auditType " + where +
			" ORDER BY d.category.number";
		query = em.createQuery(queryString);
		
	}

	@SuppressWarnings("unchecked")
	public void fillAuditCategories(ContractorAudit contractorAudit) {
		
		AuditType auditType = contractorAudit.getAuditType();
		
		List<Integer> catIDSet = null;
		if (auditType.isPqf()) {
			Query query = em.createQuery("SELECT DISTINCT c.category.id FROM AuditCatOperator c " +
					"WHERE c.riskLevel = :risk AND c.operatorAccount IN (SELECT co.operatorAccount FROM ContractorOperator co " +
					"WHERE co.contractorAccount = :contractor)");
			query.setParameter("contractor", contractorAudit.getContractorAccount());
			query.setParameter("risk", contractorAudit.getContractorAccount().getRiskLevel());
			catIDSet = query.getResultList();
		}

		if (auditType.getAuditTypeID() == AuditType.DESKTOP) {
			Query query = em.createQuery("SELECT DISTINCT c.category.id FROM AuditCatOperator c " +
					"WHERE c.riskLevel = :risk AND c.operatorAccount IN (SELECT co.operatorAccount FROM ContractorOperator co " +
					"WHERE co.contractorAccount = :contractor)");
			query.setParameter("contractor", contractorAudit.getContractorAccount());
			query.setParameter("risk", contractorAudit.getContractorAccount().getRiskLevel().ordinal());
			catIDSet = query.getResultList();
			/*
			String selectQuery = "SELECT DISTINCT catID FROM desktopMatrix m " +
					"JOIN pqfData d ON (m.qID=d.questionID AND m.auditType='Desktop') "+
					"JOIN pqfQuestions q ON (q.questionID=m.qID) " +
					"WHERE " +
						"((questionType='Service' AND d.answer LIKE 'C%') OR "+
						"(questionType IN ('Industry','Main Work') AND answer='X')) ";
			*/
		}
		
		Query query = em.createQuery("FROM AuditCategory c WHERE c.auditType = ?");
		query.setParameter(1, contractorAudit.getAuditType());
		List<AuditCategory> categories = query.getResultList();
		
		// Add any missing categories
		for(AuditCategory category : categories) {
			boolean contains = false;
			for(AuditCatData catData : contractorAudit.getCategories()) {
				if (catData.getCategory().equals(category)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				AuditCatData data = new AuditCatData();
				data.setAudit(contractorAudit);
				data.setCategory(category);
				data.setApplies(YesNo.Yes);
				if (catIDSet != null && !catIDSet.contains(category.getId()))
					data.setApplies(YesNo.No);
				save(data);
				contractorAudit.getCategories().add(data);
			}
		}
		
	}

	public List<AuditCatData> findAllAuditCatData(int auditID, int catID) {
		String selectQuery = "SELECT * FROM pqfCatData "+
		"WHERE catID="+catID+" AND auditID="+auditID;
		Query query = em.createQuery(selectQuery);
		return query.getResultList();
	}




}
