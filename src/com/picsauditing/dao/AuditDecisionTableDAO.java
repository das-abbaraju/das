package com.picsauditing.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class AuditDecisionTableDAO extends PicsDAO {

	public AuditCategoryRule findAuditCategoryRule(int id) {
		return em.find(AuditCategoryRule.class, id);
	}

	public List<AuditCategoryRule> getLessGranular(AuditCategoryRule rule) {
		return getLessGranular(rule, new Date());
	}

	public List<AuditCategoryRule> getLessGranular(AuditCategoryRule rule, Date queryDate) {
		String where = "WHERE a.priority < " + rule.getPriority();
		where += " AND effectiveDate <= :queryDate AND expirationDate > :queryDate";

		if (rule.getAuditType() == null)
			where += " AND auditType IS NULL";
		if (rule.getAuditCategory() == null)
			where += " AND auditCategory IS NULL";
		if (rule.getContractorType() == null)
			where += " AND contractorType IS NULL";

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY a.priority");
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditCategoryRule> getMoreGranular(AuditCategoryRule rule) {
		return getMoreGranular(rule, new Date());
	}

	public List<AuditCategoryRule> getMoreGranular(AuditCategoryRule rule, Date queryDate) {
		String where = "WHERE a.priority > " + rule.getPriority();
		where += " AND effectiveDate <= :queryDate AND expirationDate > :queryDate";

		if (rule.getAuditType() != null)
			where += " AND auditType.id = " + rule.getAuditType().getId();
		if (rule.getAuditCategory() != null)
			where += " AND auditCategory.id = " + rule.getAuditCategory().getId();
		if (rule.getContractorType() != null)
			where += " AND contractorType = '" + rule.getContractorType().toString() + "'";

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY a.priority");
		query.setMaxResults(100);
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditCategoryRule> getSimilar(AuditCategoryRule rule) {
		return getSimilar(rule, new Date());
	}

	public List<AuditCategoryRule> getSimilar(AuditCategoryRule rule, Date queryDate) {
		String where = "WHERE a.priority = " + rule.getPriority();
		where += " AND effectiveDate <= :queryDate AND expirationDate > :queryDate";

		if (rule.getAuditType() != null)
			where += " AND auditType.id = " + rule.getAuditType().getId();
		if (rule.getAuditCategory() != null)
			where += " AND auditCategory.id = " + rule.getAuditCategory().getId();
		if (rule.getContractorType() != null)
			where += " AND contractorType = '" + rule.getContractorType().toString() + "'";

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY a.priority");
		query.setMaxResults(100);
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditTypeRule> getApplicable(ContractorAccount contractor) {
		String where = "WHERE effectiveDate <= NOW() AND expirationDate > NOW()";

		where += " AND (risk IS NULL";
		if (contractor.getRiskLevel() != null)
			where += " OR risk = " + contractor.getRiskLevel().ordinal();
		where += ")";

		where += " AND (contractorType IS NULL";
		if (contractor.isOnsiteServices())
			where += " OR contractorType = 'Onsite'";
		if (contractor.isOffsiteServices())
			where += " OR contractorType = 'Offsite'";
		if (contractor.isMaterialSupplier())
			where += " OR contractorType = 'Supplier'";
		where += ")";

		Set<Integer> operatorIDs = new HashSet<Integer>();
		for (ContractorOperator co : contractor.getOperators()) {
			operatorIDs.add(co.getOperatorAccount().getId());
			for (Facility facility : co.getOperatorAccount().getCorporateFacilities()) {
				operatorIDs.add(facility.getCorporate().getId());
			}
		}
		where += " AND (opID IS NULL";
		if (operatorIDs.size() > 0)
			where += " OR opID IN (" + Strings.implode(operatorIDs, ",") + ")";
		where += ")";

		Query query = em.createQuery("SELECT a FROM AuditTypeRule a " + where + " ORDER BY a.priority");
		return query.getResultList();
	}
}
