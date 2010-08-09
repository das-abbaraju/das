package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.util.Strings;

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
		if (contractorAudit.getAuditType().isPqf()) {
			// This is a PQF, so it has special query criteria
			if (!permissions.isAdmin()) // TODO change this to be a permission
				where += "AND d.applies = 'Yes' "; // Show only the admins the
			// full
			// PQF

			// Contractors can see their full PQF
			if (!permissions.isContractor()) {
				if (!permissions.hasPermission(OpPerms.ViewFullPQF))
					where += "AND d.category.id <> " + AuditCategory.WORK_HISTORY + " ";

				if (permissions.isOperatorCorporate()) {
					Set<Integer> inheritCategories = new HashSet<Integer>();
					Query query;
					if (permissions.isOperator()) {
						query = em.createQuery("SELECT inheritAuditCategories.id FROM OperatorAccount WHERE id = :id");
					} else {
						query = em
								.createQuery("SELECT operator.inheritAuditCategories.id FROM Facility f WHERE corporate.id = :id");
					}
					query.setParameter("id", permissions.getAccountId());
					for (Object row : query.getResultList()) {
						inheritCategories.add(Integer.parseInt(row.toString()));
					}

					where += "AND d.category IN (SELECT o.category FROM AuditCatOperator o "
							+ "WHERE o.category.auditType.id = 1 AND o.riskLevel = :risk "
							+ "AND o.operatorAccount.id IN (" + Strings.implode(inheritCategories, ",") + ") )";
				}
			}
		}

		// There is a strange bug when
		// If this is an operator or corporate, we already
		if (!where.contains(":auditType"))
			where += "AND d.category.auditType.id = :auditType ";
		// inner join fetch d.category
		try {
			String queryString = "SELECT d FROM AuditCatData d " + "WHERE d.audit.id = :conAudit " + where
					+ " ORDER BY d.category.number";
			Query query = em.createQuery(queryString);

			query.setParameter("conAudit", contractorAudit.getId());
			setOptionalParameter(query, "auditType", contractorAudit.getAuditType().getId());
			setOptionalParameter(query, "risk", contractorAudit.getContractorAccount().getRiskLevel());

			return query.getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			List<AuditCatData> categories = new ArrayList<AuditCatData>();
			AuditCatData data = new AuditCatData();
			data.setAudit(contractorAudit);
			data.setApplies(true);
			AuditCategory cat = new AuditCategory();
			cat.setName("Error Occurred Getting Categories");
			data.setCategory(cat);
			categories.add(data);
			return categories;
		}
	}

	public Map<AuditCategory, AuditCatData> findByAuditMap(ContractorAudit contractorAudit, Permissions permissions) {
		Map<AuditCategory, AuditCatData> map = new HashMap<AuditCategory, AuditCatData>();
		for (AuditCatData data : findByAudit(contractorAudit, permissions)) {
			map.put(data.getCategory(), data);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public List<AuditCatData> findAllAuditCatData(int auditID, int catID) {
		String selectQuery = "FROM AuditCatData d " + "WHERE d.category.id=" + catID + " AND d.audit.id=" + auditID;
		Query query = em.createQuery(selectQuery);
		return query.getResultList();
	}

	/**
	 * Get a list of NCMS categories and their statuses for a given contractor.
	 */
	@SuppressWarnings("unchecked")
	public List<NcmsCategory> findNcmsCategories(int conID) throws Exception {
		List<NcmsCategory> categories = new ArrayList<NcmsCategory>();
		if (conID == 0)
			return categories;

		StringBuffer sql = new StringBuffer("SELECT ");
		for (String columnName : NcmsCategory.columns)
			sql.append("`").append(columnName).append("`,");
		sql.append(" 1 FROM NCMS_Desktop WHERE conID=" + conID);

		Query query = em.createNativeQuery(sql.toString());

		List<Object> results = query.getResultList();

		for (Object o : results) {
			Object[] data = (Object[]) o;
			int i = 0;
			for (String columnName : NcmsCategory.columns) {
				NcmsCategory cat = new NcmsCategory(columnName, data[i].toString());
				categories.add(cat);
				i++;
			}
		}
		return categories;
	}

}
