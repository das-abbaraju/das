package com.picsauditing.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class AuditDecisionTableDAO extends PicsDAO {

	public <T extends BaseDecisionTreeRule> List<T> findAllRules(Class<T> clazz) {
		Query query = em.createQuery("FROM " + clazz.getName()
				+ " WHERE effectiveDate <= NOW() AND expirationDate > NOW() ORDER BY priority DESC");
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
				" AND r.id > 1 AND (r.auditType IS NULL OR r.auditType = :auditType)" + where));
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
		Query query;
		if (tags.size() > 0) {
			query = em.createQuery(findByQuery("AuditTypeRule", " AND (r.tag IN (:tags))"));
			query.setParameter("tags", tags);
		} else {
			query = em.createQuery(findByQuery("AuditTypeRule", ""));
		}

		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByTags(List<OperatorTag> tags) {
		Query query = em.createQuery(findByQuery("AuditCategoryRule", " AND (r.tag IN (:tags))"));
		query.setParameter("tags", tags);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByQuestion(int questionID) {
		Query query = em.createQuery(findByQuery("AuditCategoryRule", " AND r.question.id = :questionID"));
		query.setParameter("questionID", questionID);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findCategoryRulesByQuestionCategory(AuditCategory category) {
		Query query = em.createQuery(findByQuery("AuditCategoryRule", " AND r.question.category = ?"));
		query.setParameter(1, category);

		return query.getResultList();
	}

	public List<AuditTypeRule> findAuditTypeRulesByOperator(int opID, String where) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " AND " + where;

		Query query = em.createQuery(findByQuery("AuditTypeRule", " AND (r.operatorAccount.id = :operatorID)" + where));
		query.setParameter("operatorID", opID);
		query.setMaxResults(250);
		return query.getResultList();
	}

	public List<AuditCategoryRule> findAuditCategoryRulesByOperator(int opID, String where) {
		if (Strings.isEmpty(where))
			where = "";
		else
			where = " AND " + where;

		Query query = em.createQuery(findByQuery("AuditCategoryRule", " AND (r.operatorAccount.id = :operatorID)"
				+ where));
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

		where += " AND (safetyRisk IS NULL";
		if (rule.getSafetyRisk() != null)
			where += " OR safetyRisk = '" + rule.getSafetyRisk() + "'";
		where += " )";

		where += " AND (productRisk IS NULL";
		if (rule.getProductRisk() != null)
			where += " OR productRisk = '" + rule.getProductRisk() + "'";
		where += " )";

		where += " AND (accountLevel IS NULL";
		if (rule.getAccountLevel() != null)
			where += " OR accountLevel = '" + rule.getAccountLevel().toString() + "'";
		where += " )";

		where += " AND (soleProprietor IS NULL";
		if (rule.getSoleProprietor() != null)
			where += " OR soleProprietor = " + (rule.getSoleProprietor() ? 1 : 0);
		where += " )";

		where += " AND (contractorType IS NULL";
		if (rule.getContractorType() != null)
			where += " OR contractorType = '" + rule.getContractorType().toString() + "'";
		where += " )";

		where += " AND (question IS NULL";
		if (rule.getQuestion() != null)
			where += " OR question.id = " + rule.getQuestion().getId();
		where += " )";

		where += " AND (trade IS NULL";
		if (rule.getTrade() != null)
			where += " OR trade.id = " + rule.getTrade().getId();
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
		if (rule.getTrade() != null)
			where += " AND trade.id = " + rule.getTrade().getId();
		if (rule.getAccountLevel() != null)
			where += " AND accountLevel = '" + rule.getAccountLevel().toString() + "'";
		if (rule.getSoleProprietor() != null)
			where += " AND soleProprietor = " + (rule.getSoleProprietor() ? 1 : 0);
		if (rule.getQuestion() != null)
			where += " AND question.id = " + rule.getQuestion().getId();
		if (rule.getTag() != null)
			where += " AND tag.id = " + rule.getTag().getId();
		if (rule.getSafetyRisk() != null)
			where += " AND safetyRisk = '" + rule.getSafetyRisk() + "'";
		if (rule.getProductRisk() != null)
			where += " AND productRisk = '" + rule.getProductRisk() + "'";
		return where;
	}

	@Transactional(propagation = Propagation.NESTED)
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

	@Transactional(propagation = Propagation.NESTED)
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
			operatorIDs.add(operator.getId());
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

}
