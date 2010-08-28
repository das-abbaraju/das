package com.picsauditing.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class AuditDecisionTableDAO extends PicsDAO {

	public AuditCategoryRule findAuditCategoryRule(int id) {
		return em.find(AuditCategoryRule.class, id);
	}

	public AuditTypeRule findAuditTypeRule(int id) {
		return em.find(AuditTypeRule.class, id);
	}

	public List<AuditTypeRule> findByAuditType(int auditTypeID) {
		Query query = em.createQuery("FROM AuditTypeRule r WHERE r.auditType.id = :auditTypeID ORDER BY r.priority");
		query.setParameter("auditTypeID", auditTypeID);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findByCategory(int categoryID) {
		Query query = em
				.createQuery("FROM AuditCategoryRule r WHERE r.auditCategory.id = :categoryID ORDER BY r.priority");
		query.setParameter("categoryID", categoryID);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByTags(List<OperatorTag> tags) {
		Query query = em.createQuery("FROM AuditTypeRule r WHERE r.tag IN (:tags) ORDER BY r.priority");
		query.setParameter("tags", tags);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByTags(List<OperatorTag> tags) {
		Query query = em.createQuery("FROM AuditCategoryRule r WHERE r.tag IN (:tags) ORDER BY r.priority");
		query.setParameter("tags", tags);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByOperator(int opID) {
		Query query = em
				.createQuery("FROM AuditTypeRule r WHERE r.operatorAccount.id = :operatorID ORDER BY r.priority");
		query.setParameter("operatorID", opID);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findAuditCategoryRulesByOperator(int opID) {
		Query query = em
				.createQuery("FROM AuditCategoryRule r WHERE r.operatorAccount.id = :operatorID ORDER BY r.priority");
		query.setParameter("operatorID", opID);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> getLessGranular(AuditCategoryRule rule, Date queryDate) {
		String where = getLessGranularWhere(rule);

		where += " AND (auditCategory IS NULL";
		if (rule.getAuditCategory() != null)
			where += " OR auditCategory.id = " + rule.getAuditCategory().getId();
		where += " )";

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY priority");
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditTypeRule> getLessGranular(AuditTypeRule rule, Date queryDate) {
		String where = getLessGranularWhere(rule);

		Query query = em.createQuery("SELECT a FROM AuditTypeRule a " + where + " ORDER BY priority");
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	private String getLessGranularWhere(AuditRule rule) {
		String where = "WHERE id != " + rule.getId();
		where += " AND effectiveDate <= :queryDate AND expirationDate > :queryDate";

		where += " AND (auditType IS NULL";
		if (rule.getAuditType() != null)
			where += " OR auditType.id = " + rule.getAuditType().getId();
		where += " )";

		where += " AND (operatorAccount IS NULL";
		if (rule.getOperatorAccount() != null)
			where += " OR operatorAccount.id = " + rule.getOperatorAccount().getId();
		where += " )";

		where += " AND (risk IS NULL";
		if (rule.getRisk() != null)
			where += " OR risk = " + rule.getRisk().ordinal();
		where += " )";

		where += " AND (contractorType IS NULL";
		if (rule.getContractorType() != null)
			where += " OR contractorType = '" + rule.getContractorType().toString() + "'";
		where += " )";

		where += " AND (question IS NULL";
		if (rule.getQuestion() != null)
			where += " OR question.id = " + rule.getQuestion().getId();
		where += " )";

		where += " AND (tag IS NULL";
		if (rule.getTag() != null)
			where += " OR tag.id = " + rule.getTag().getId();
		where += " )";
		return where;
	}

	/*
	 * public List<AuditCategoryRule> getSimilar(AuditCategoryRule rule, Date
	 * queryDate) { String where = "WHERE id != " + rule.getId() +
	 * " AND priority = " + rule.getPriority(); where +=
	 * " AND effectiveDate <= :queryDate AND expirationDate > :queryDate";
	 * 
	 * where += " AND auditType.id " + (rule.getAuditType() == null ? "IS NULL"
	 * : "= " + rule.getAuditType().getId()); where += " AND auditCategory.id "
	 * + (rule.getAuditCategory() == null ? "IS NULL" : "= " +
	 * rule.getAuditCategory().getId()); where += " AND operatorAccount.id " +
	 * (rule.getOperatorAccount() == null ? "IS NULL" : "= " +
	 * rule.getOperatorAccount().getId()); where += " AND contractorType " +
	 * (rule.getContractorType() == null ? "IS NULL" : "= '" +
	 * rule.getContractorType().toString() + "'"); where += " AND risk " +
	 * (rule.getRisk() == null ? "IS NULL" : "= " + rule.getRisk().ordinal());
	 * 
	 * Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where
	 * + " ORDER BY priority"); query.setMaxResults(100);
	 * query.setParameter("queryDate", queryDate); return query.getResultList();
	 * }
	 */

	public List<AuditCategoryRule> getMoreGranular(AuditCategoryRule rule, Date queryDate) {
		String where = getMoreGranularWhere(rule);

		if (rule.getAuditCategory() != null)
			where += " AND auditCategory.id = " + rule.getAuditCategory().getId();

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY priority");
		query.setMaxResults(100);
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditTypeRule> getMoreGranular(AuditTypeRule rule, Date queryDate) {
		String where = getMoreGranularWhere(rule);

		Query query = em.createQuery("SELECT a FROM AuditTypeRule a " + where + " ORDER BY priority");
		query.setMaxResults(100);
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	private String getMoreGranularWhere(AuditRule rule) {
		String where = "WHERE id != " + rule.getId();
		where += " AND effectiveDate <= :queryDate AND expirationDate > :queryDate";

		if (rule.getAuditType() != null)
			where += " AND auditType.id = " + rule.getAuditType().getId();
		if (rule.getOperatorAccount() != null)
			where += " AND operatorAccount.id = " + rule.getOperatorAccount().getId();
		if (rule.getContractorType() != null)
			where += " AND contractorType = '" + rule.getContractorType().toString() + "'";
		if (rule.getQuestion() != null)
			where += " AND question.id = " + rule.getQuestion().getId();
		if (rule.getTag() != null)
			where += " AND tag.id = " + rule.getTag().getId();
		if (rule.getRisk() != null)
			where += " AND risk = " + rule.getRisk().ordinal();
		return where;
	}

	public List<AuditTypeRule> getApplicableAuditTypeRules(ContractorAccount contractor) {
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

		Query query = em.createQuery("SELECT a FROM AuditTypeRule a " + where + " ORDER BY priority DESC");
		return query.getResultList();
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAudit conAudit) {
		String where = "WHERE effectiveDate <= NOW() AND expirationDate > NOW()";

		ContractorAccount contractor = conAudit.getContractorAccount();
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

		where += " AND (auditType IS NULL OR auditType.id = " + conAudit.getAuditType().getId() + ")";

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY priority DESC");
		return query.getResultList();
	}

	public int deleteChildren(AuditCategoryRule rule, Permissions permissions) {
		String where = getMoreGranularWhere(rule);

		if (rule.getAuditCategory() != null)
			where += " AND auditCategory.id = " + rule.getAuditCategory().getId();

		where += " AND include = :include AND level = :level";

		String updateString = "UPDATE AuditCategoryRule SET expirationDate = :queryDate, updateDate = :queryDate, updatedBy.id = :userID "
				+ where;

		Query query = em.createQuery(updateString);
		query.setParameter("queryDate", new Date());
		query.setParameter("userID", permissions.getUserId());
		query.setParameter("include", rule.isInclude());
		query.setParameter("level", rule.getLevel() + 1);
		return query.executeUpdate();
	}

	public int deleteChildren(AuditTypeRule rule, Permissions permissions) {
		String where = getMoreGranularWhere(rule);

		if (rule.getAuditType() != null)
			where += " AND auditType = " + rule.getAuditType().getId();

		where += " AND include = :include AND level = :level";

		String updateString = "UPDATE AuditCategoryRule SET expirationDate = :queryDate, updateDate = :queryDate, updatedBy.id = :userID "
				+ where;

		Query query = em.createQuery(updateString);
		query.setParameter("queryDate", new Date());
		query.setParameter("userID", permissions.getUserId());
		query.setParameter("include", rule.isInclude());
		query.setParameter("level", rule.getLevel() + 1);
		return query.executeUpdate();
	}

}
