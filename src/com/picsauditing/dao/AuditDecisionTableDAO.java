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

	public List<AuditCategoryRule> findRules() {
		Query query = em
				.createQuery("FROM AuditCategoryRule WHERE effectiveDate <= NOW() AND expirationDate > NOW() ORDER BY priority DESC");
		// query.setMaxResults(500);
		return query.getResultList();
	}

	public AuditCategoryRule findAuditCategoryRule(int id) {
		return em.find(AuditCategoryRule.class, id);
	}

	public AuditTypeRule findAuditTypeRule(int id) {
		return em.find(AuditTypeRule.class, id);
	}

	static private String findByQuery(String table, String where) {
		return "FROM " + table + " r WHERE (effectiveDate <= NOW() AND expirationDate > NOW()) " + where
				+ " ORDER BY r.priority DESC";
	}

	public List<AuditTypeRule> findByAuditType(AuditType auditType) {
		return findByAuditType(auditType, null);
	}

	public List<AuditTypeRule> findByAuditType(AuditType auditType, OperatorAccount operator) {
		String where = "";
		if (operator != null)
			where = " AND (r.operatorAccount.id IN (" + Strings.implode(operator.getOperatorHeirarchy())
					+ ") OR r.operatorAccount IS NULL)";

		Query query = em.createQuery(findByQuery("AuditTypeRule",
				" AND (r.auditType IS NULL OR r.auditType = :auditType)" + where));
		query.setParameter("auditType", auditType);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findByCategory(AuditCategory category) {
		return findByCategory(category, null);
	}

	public List<AuditCategoryRule> findByCategory(AuditCategory category, OperatorAccount operator) {
		String where = "";
		if (operator != null)
			where = " AND (r.operatorAccount IS NULL OR r.operatorAccount.id IN ("
					+ Strings.implode(operator.getOperatorHeirarchy()) + "))";

		Query query = em.createQuery(findByQuery("AuditCategoryRule",
				" AND (r.auditType IS NULL OR r.auditType = :auditType)"
						+ " AND (r.rootCategory IS NULL OR r.rootCategory = :rootCategory)"
						+ " AND (r.auditCategory IS NULL OR r.auditCategory = :category)" + where));
		query.setParameter("auditType", category.getAuditType());
		query.setParameter("category", category);
		query.setParameter("rootCategory", category.getParent() == null);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByTags(List<OperatorTag> tags) {
		Query query = em.createQuery(findByQuery("AuditTypeRule", " AND (r.tag IS NULL OR r.tag IN (:tags))"));
		query.setParameter("tags", tags);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByTags(List<OperatorTag> tags) {
		Query query = em.createQuery(findByQuery("AuditCategoryRule", " AND (t.tag IS NULL OR r.tag IN (:tags))"));
		query.setParameter("tags", tags);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByQuestion(int questionID) {
		Query query = em.createQuery(findByQuery("AuditCategoryRule", " AND r.question.id = :questionID"));
		query.setParameter("questionID", questionID);
		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByOperator(int opID) {
		Query query = em.createQuery(findByQuery("AuditTypeRule",
				" AND (r.operatorAccount IS NULL OR r.operatorAccount.id = :operatorID)"));
		query.setParameter("operatorID", opID);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findAuditCategoryRulesByOperator(int opID) {
		Query query = em.createQuery(findByQuery("AuditCategoryRule",
				" AND (r.operatorAccount IS NULL OR r.operatorAccount.id = :operatorID)"));
		query.setParameter("operatorID", opID);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> getLessGranular(AuditCategoryRule rule, Date queryDate) {
		String where = getLessGranularWhere(rule);

		where += " AND (auditCategory IS NULL";
		if (rule.getAuditCategory() != null)
			where += " OR auditCategory.id = " + rule.getAuditCategory().getId();
		where += " )";

		where += " AND (rootCategory IS NULL";
		if (rule.getAuditCategory() != null)
			where += " OR rootCategory = " + (rule.getAuditCategory().getParent() == null ? 1 : 0);
		else if (rule.getRootCategory() != null)
			where += " OR rootCategory = " + (rule.getRootCategory() ? 1 : 0);
		where += " )";

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY priority");
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditTypeRule> getLessGranular(AuditTypeRule rule, Date queryDate) {
		String where = getLessGranularWhere(rule);

		where += " AND (dependentAuditType IS NULL";
		if (rule.getDependentAuditType() != null)
			where += " OR dependentAuditType.id = " + rule.getDependentAuditType().getId();
		where += " )";

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

		where += " AND (acceptsBids IS NULL";
		if (rule.getAcceptsBids() != null)
			where += " OR acceptsBids = " + (rule.getAcceptsBids() ? 1 : 0);
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

	public List<AuditCategoryRule> getMoreGranular(AuditCategoryRule rule, Date queryDate) {
		String where = getMoreGranularWhere(rule);

		if (rule.getAuditCategory() != null)
			where += " AND auditCategory.id = " + rule.getAuditCategory().getId();
		else if (rule.getRootCategory() != null) {
			if (rule.getRootCategory()) {
				where += " AND ((rootCategory = 1 AND auditCategory IS NULL) "
						+ "OR (auditCategory.id > 0 AND auditCategory.parent IS NULL))";
			} else {
				where += " AND ((rootCategory = 0 AND auditCategory IS NULL) "
						+ "OR (auditCategory.id > 0 AND auditCategory.parent.id > 0))";
			}
		}

		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY priority");
		query.setMaxResults(250);
		query.setParameter("queryDate", queryDate);
		return query.getResultList();
	}

	public List<AuditTypeRule> getMoreGranular(AuditTypeRule rule, Date queryDate) {
		String where = getMoreGranularWhere(rule);

		if (rule.getDependentAuditType() != null)
			where += " AND dependentAuditType.id = " + rule.getDependentAuditType().getId();

		Query query = em.createQuery("SELECT a FROM AuditTypeRule a " + where + " ORDER BY priority");
		query.setMaxResults(250);
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
		if (rule.getAcceptsBids() != null)
			where += " AND acceptsBids = " + (rule.getAcceptsBids() ? 1 : 0);
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

		where += " AND (acceptsBids IS NULL OR acceptsBids = " + (contractor.isAcceptsBids() ? 1 : 0) + ")";

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
			// There's a bug where corporate accounts not associated with this
			// operator get added to this contractor. So just skip all
			// corporates and pick them up in the
			// co.getOperatorAccount().getCorporateFacilities()
			if (co.getOperatorAccount().isOperator()) {
				operatorIDs.add(co.getOperatorAccount().getId());
				for (Facility facility : co.getOperatorAccount().getCorporateFacilities()) {
					operatorIDs.add(facility.getCorporate().getId());
				}
			}
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

		where += " AND (acceptsBids IS NULL OR acceptsBids = " + (contractor.isAcceptsBids() ? 1 : 0) + ")";

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
			// There's a bug where corporate accounts not associated with this
			// operator get added to this contractor. So just skip all
			// corporates and pick them up in the
			// co.getOperatorAccount().getCorporateFacilities()
			if (co.getOperatorAccount().isOperator()) {
				operatorIDs.add(co.getOperatorAccount().getId());
				for (Facility facility : co.getOperatorAccount().getCorporateFacilities()) {
					operatorIDs.add(facility.getCorporate().getId());
				}
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
	 * 
	 * @param operator
	 * @return
	 */
	public Set<Integer> getAuditTypes(OperatorAccount operator) {
		String where = "WHERE effectiveDate <= NOW() AND expirationDate > NOW() AND include = 1 AND auditType.id > 0";

		Set<Integer> operatorIDs = new HashSet<Integer>();
		if (operator.isCorporate()) {
			for (Facility facility : operator.getOperatorFacilities()) {
				operatorIDs.addAll(facility.getOperator().getOperatorHeirarchy());
			}
		} else
			operatorIDs.addAll(operator.getOperatorHeirarchy());

		where += " AND (opID IS NULL";
		if (operatorIDs.size() > 0)
			where += " OR opID IN (" + Strings.implode(operatorIDs, ",") + ")";
		where += ")";

		Query query = em.createQuery("SELECT DISTINCT a.auditType.id FROM AuditTypeRule a " + where
				+ " ORDER BY priority DESC");

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
		String sql = "SELECT auditType.id, operatorAccount.id FROM AuditTypeRule r " + "WHERE operatorAccount.id IN ("
				+ Strings.implode(operator.getOperatorHeirarchy())
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
		return getCategoriesByOperator(operator, permissions, false);
	}

	public List<AuditCategory> getCategoriesByOperator(OperatorAccount operator, Permissions permissions,
			boolean topLevel) {
		String where = "";
		List<Integer> operatorIDs = new ArrayList<Integer>();
		if (permissions.isOperator())
			operatorIDs = operator.getOperatorHeirarchy();
		if (permissions.isCorporate()) {
			operatorIDs.addAll(permissions.getOperatorChildren());
		}
		if (operatorIDs.size() > 0)
			where += " WHERE a.opID IN (" + Strings.implode(operatorIDs, ",") + ")";
		if (topLevel)
			where += (operatorIDs.size() > 0 ? " AND" : " WHERE") + " a.auditCategory.parent IS NULL";

		Query query = em.createQuery("SELECT DISTINCT a.auditCategory FROM AuditCategoryRule a" + where
				+ " ORDER BY a.priority DESC");

		return query.getResultList();
	}
}
