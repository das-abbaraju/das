package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
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

	public List<AuditTypeRule> findByAuditType(AuditType auditType) {
		Query query = em
				.createQuery("FROM AuditTypeRule r WHERE (effectiveDate <= NOW() AND expirationDate > NOW()) AND (r.auditType IS NULL OR r.auditType = :auditType) ORDER BY r.priority");
		query.setParameter("auditType", auditType);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findByCategory(AuditCategory category) {
		Query query = em
				.createQuery("FROM AuditCategoryRule r WHERE (effectiveDate <= NOW() AND expirationDate > NOW()) AND (r.auditType IS NULL OR r.auditType = :auditType) AND (r.auditCategory IS NULL OR r.auditCategory = :category) ORDER BY r.priority");
		query.setParameter("auditType", category.getAuditType());
		query.setParameter("category", category);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByTags(List<OperatorTag> tags) {
		Query query = em
				.createQuery("FROM AuditTypeRule r WHERE (effectiveDate <= NOW() AND expirationDate > NOW())AND (r.tag IS NULL OR r.tag IN (:tags) ) ORDER BY r.priority");
		query.setParameter("tags", tags);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByTags(List<OperatorTag> tags) {
		Query query = em
				.createQuery("FROM AuditCategoryRule r WHERE (effectiveDate <= NOW() AND expirationDate > NOW()) AND (t.tag IS NULL OR r.tag IN (:tags) ) ORDER BY r.priority");
		query.setParameter("tags", tags);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByOperator(int opID) {
		Query query = em
				.createQuery("FROM AuditTypeRule r WHERE (effectiveDate <= NOW() AND expirationDate > NOW()) AND (r.operatorAccount IS NULL OR r.operatorAccount.id = :operatorID) ORDER BY r.priority");
		query.setParameter("operatorID", opID);
		query.setMaxResults(50);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findAuditCategoryRulesByOperator(int opID) {
		Query query = em
				.createQuery("FROM AuditCategoryRule r WHERE (effectiveDate <= NOW() AND expirationDate > NOW()) AND (r.operatorAccount IS NULL OR r.operatorAccount.id = :operatorID) ORDER BY r.priority");
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

	public List<AuditTypeRule> getApplicableAuditRules(ContractorAccount contractor) {
		String where = "WHERE effectiveDate <= NOW() AND expirationDate > NOW()";

		where += " AND (risk IS NULL";
		if (contractor.getRiskLevel() != null)
			where += " OR risk = " + contractor.getRiskLevel().ordinal();
		where += ")";
		
		if(contractor.isAcceptsBids())
			where += " AND acceptsBids = 1";
		else {
			where += " AND (acceptsBids IS NULL OR acceptsBids = 0)";
		}
		
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
			operatorIDs.addAll(co.getOperatorAccount().getOperatorHeirarchy());
		}
		where += " AND (opID IS NULL";
		if (operatorIDs.size() > 0)
			where += " OR opID IN (" + Strings.implode(operatorIDs, ",") + ")";
		where += ")";

		Query query = em.createQuery("SELECT a FROM AuditTypeRule a " + where + " ORDER BY priority DESC");
		return query.getResultList();
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAccount contractor, AuditType auditType) {
		Set<AuditType> auditTypes = new HashSet<AuditType>();
		auditTypes.add(auditType);
		return getApplicableCategoryRules(contractor, auditTypes);
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAccount contractor, Set<AuditType> auditTypes) {
		String where = "WHERE effectiveDate <= NOW() AND expirationDate > NOW()";

		where += " AND (risk IS NULL";
		if (contractor.getRiskLevel() != null)
			where += " OR risk = " + contractor.getRiskLevel().ordinal();
		where += ")";

		if(contractor.isAcceptsBids())
			where += " AND acceptsBids = 1";
		else {
			where += " AND (acceptsBids IS NULL OR acceptsBids = 0)";
		}
		
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

		where += " AND (auditType IS NULL OR auditType.id IN (0";
		for (AuditType auditType : auditTypes) {
			where += "," + auditType.getId();
		}
		where += "))";

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

		where += " AND include = :include AND level = :level";

		String updateString = "UPDATE AuditTypeRule SET expirationDate = :queryDate, updateDate = :queryDate, updatedBy.id = :userID "
				+ where;

		Query query = em.createQuery(updateString);
		query.setParameter("queryDate", new Date());
		query.setParameter("userID", permissions.getUserId());
		query.setParameter("include", rule.isInclude());
		query.setParameter("level", rule.getLevel() + 1);
		return query.executeUpdate();
	}

	/**
	 * Get a list of audits that are potentially visible to an operator
	 * @param operator
	 * @return
	 */
	public Set<Integer> getAuditTypes(OperatorAccount operator) {
		String where = "WHERE effectiveDate <= NOW() AND expirationDate > NOW() AND include = 1 AND auditType.id > 0";
		
		Set<Integer> operatorIDs = new HashSet<Integer>();
		if(operator.isCorporate()) {
			for(Facility facility : operator.getOperatorFacilities()) {
				operatorIDs.addAll(facility.getOperator().getOperatorHeirarchy());
			}
		} else
			operatorIDs.addAll(operator.getOperatorHeirarchy());
		
		where += " AND (opID IS NULL";
		if (operatorIDs.size() > 0)
			where += " OR opID IN (" + Strings.implode(operatorIDs, ",") + ")";
		where += ")";

		Query query = em.createQuery("SELECT DISTINCT a.auditType.id FROM AuditTypeRule a " + where + " ORDER BY priority DESC");
		
		Set<Integer> ids = new HashSet<Integer>();
		for (Object id : query.getResultList()) {
			ids.add(Integer.parseInt(id.toString()));
		}
		return ids;
	}
	
	/**
	 * 
	 * @param operator
	 * @return Map of AuditTypeID to OperatorID (aka governing body)
	 */
	public Map<Integer, Integer> getGoverningBodyMap(OperatorAccount operator) {
		String sql = "SELECT auditType.id, operatorAccount.id FROM AuditTypeRule r "
				+ "WHERE operatorAccount.id IN (" + Strings.implode(operator.getOperatorHeirarchy())
				+ ") AND include = 1 GROUP BY auditType.id, operatorAccount.id";
		Query query = em.createQuery(sql);
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Object row : query.getResultList()) {
			System.out.println(row);
			int auditTypeID = row.hashCode();
			int opID = row.hashCode();
			if (!map.containsKey(auditTypeID))
				map.put(auditTypeID, opID);
		}
		return map;
	}
	
	public List<AuditCategory> getCategoriesByOperator(OperatorAccount operator, Permissions permissions) {
		String where = ""; 
		List<Integer> operatorIDs = new ArrayList<Integer>();
		if(permissions.isOperator())
			operatorIDs = operator.getOperatorHeirarchy();
		if(permissions.isCorporate()) {
			operatorIDs.addAll(permissions.getOperatorChildren());
		}
		if (operatorIDs.size() > 0)
			where += " WHERE opID IN (" + Strings.implode(operatorIDs, ",") + ")";
		Query query = em.createQuery("SELECT DISTINCT a.auditCategory FROM AuditCategoryRule a " + where + " ORDER BY priority DESC");
		
		return query.getResultList();
	}
}
