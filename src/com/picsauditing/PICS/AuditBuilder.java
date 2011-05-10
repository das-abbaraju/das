package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.log.PicsLogger;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditBuilder {

	public class AuditTypeDetail {

		public AuditTypeRule rule;
		/**
		 * Operator Accounts, not corporate, may be the same as the CAO
		 */
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	}

	public class AuditCategoriesDetail {

		public List<AuditCategoryRule> rules;
		public Map<OperatorAccount, AuditCategoryRule> operators = new HashMap<OperatorAccount, AuditCategoryRule>();
		public Set<OperatorAccount> governingBodies = new HashSet<OperatorAccount>();
		public Set<AuditCategory> categories = new HashSet<AuditCategory>();
	}

	/**
	 * 
	 * @param rules
	 * @param operators
	 *            a list of operators (not corporate) accounts associated with this contractor
	 * @return
	 */
	public Map<AuditType, AuditTypeDetail> calculateRequiredAuditTypes(List<AuditTypeRule> rules,
			Collection<OperatorAccount> operators) {
		Map<AuditType, AuditTypeDetail> auditTypes = new HashMap<AuditType, AuditTypeDetail>();

		PicsLogger.start("getRequiredAuditTypes");

		sortRules(rules);

		Set<AuditType> allCandidateAuditTypes = new HashSet<AuditType>();
		for (AuditTypeRule rule : rules) {
			if (rule.isInclude()) {
				// We will never have a rule that says to include all audit
				// types. So assuming that rule.getAuditType is never NULL is
				// fine. This fact also allows us to only evaluate the
				// auditTypes for the rules we have rather than using all
				// auditTypes.
				allCandidateAuditTypes.add(rule.getAuditType());
			}
		}

		for (AuditType auditType : allCandidateAuditTypes) {
			auditTypes.put(auditType, new AuditTypeDetail());
			for (OperatorAccount operator : operators) {
				AuditTypeRule rule = getApplicable(rules, auditType, operator);
				if (rule.isInclude()) {
					auditTypes.get(auditType).operators.add(operator);
					auditTypes.get(auditType).rule = rule;
				}
			}
			if (auditTypes.get(auditType).operators.size() == 0)
				auditTypes.remove(auditType);
		}

		PicsLogger.stop();
		return auditTypes;
	}

	static public Map<AuditType, AuditTypeDetail> calculateRequiredAuditTypes(ContractorAccount contractor) {
		// This isn't super efficient, but it works
		AuditBuilder builder = new AuditBuilder();
		AuditTypeRuleCache auditTypeRuleCache = (AuditTypeRuleCache) SpringUtils.getBean("AuditTypeRuleCache");
		AuditBuilderController controller = (AuditBuilderController) SpringUtils.getBean("AuditBuilderController");
		controller.setup(contractor, new User(User.SYSTEM));
		List<AuditTypeRule> rules = auditTypeRuleCache.getApplicableAuditRules(contractor);
		List<AuditRule> prunedRules = controller.pruneRules(rules, null);
		List<AuditTypeRule> prunedAuditRules = new ArrayList<AuditTypeRule>();
		for (AuditRule ar : prunedRules)
			prunedAuditRules.add((AuditTypeRule) ar);
		return builder.calculateRequiredAuditTypes(prunedAuditRules, contractor.getOperatorAccounts());
	}

	/**
	 * Determine which categories should be on a given audit
	 * 
	 * @param rules
	 *            Make sure that these rules are filtered for the requested contractorAudit
	 */
	public AuditCategoriesDetail getDetail(AuditType auditType, List<AuditCategoryRule> rules,
			Collection<OperatorAccount> operators) {
		AuditCategoriesDetail detail = new AuditCategoriesDetail();
		sortRules(rules);
		detail.rules = rules;
		for (OperatorAccount operator : operators) {
			detail.operators.put(operator, null);
		}

		// Figure out which categories are required
		for (AuditCategory category : auditType.getCategories()) {
			includeCategory(detail, category, rules);
		}

		for (AuditCategoryRule rule : detail.operators.values()) {
			// AuditBuilderController.fillAuditOperators() will replace any null
			// with Operator (4)
			detail.governingBodies.add(rule == null ? null : rule.getOperatorAccount());
		}
		return detail;
	}

	/**
	 * Find the first rule that applies to this operator. Consider rules for any operator(*), this operator, or one its
	 * parent companies. Ignore rules for other operators.
	 * 
	 * @param rules
	 * @param auditType
	 * @param operator
	 * @return
	 */
	static private AuditTypeRule getApplicable(List<AuditTypeRule> rules, AuditType auditType, OperatorAccount operator) {
		for (AuditTypeRule rule : rules) {
			if (rule.getAuditType() == null || rule.getAuditType().equals(auditType)) {
				if (rule.isApplies(operator))
					// Only consider rules for this operator
					return rule;
			}
		}
		return null;
	}

	private void includeCategory(AuditCategoriesDetail detail, AuditCategory category,
			List<AuditCategoryRule> categoryRules) {
		for (AuditCategory cat : category.getChildren()) {
			for (OperatorAccount operator : detail.operators.keySet()) {
				AuditCategoryRule rule = getApplicable(categoryRules, cat, operator);
				if (rule != null && rule.isInclude()) {
					detail.categories.add(cat);
					if (rule.isMoreSpecific(detail.operators.get(operator)))
						detail.operators.put(operator, rule);
				}
			}
		}

		/*
		 * for (AuditCategory subCategory : category.getSubCategories()) { includeCategory(detail, subCategory,
		 * categoryRules); }
		 */
	}

	static public AuditCategoryRule getApplicable(List<AuditCategoryRule> rules, AuditCategory auditCategory,
			OperatorAccount operator) {
		for (AuditCategoryRule rule : rules) {
			boolean ruleApplies = false;
			if (rule.getAuditCategory() == null) {
				// We have a wildcard category, so let's figure out if it
				// matches on categories or subcategories or both
				if (rule.getRootCategory() == null) {
					// Any category or subcategory matches
					ruleApplies = true;
				} else {
					if (rule.getRootCategory()) {
						if (auditCategory.getParent() == null)
							// Only categories match
							ruleApplies = true;
					} else {
						if (auditCategory.getParent() != null)
							// Only subcategories match
							ruleApplies = true;
					}
				}
			} else if (auditCategory.equals(rule.getAuditCategory())) {
				// We have a direct category match
				ruleApplies = true;
			}
			if (ruleApplies && rule.isApplies(operator))
				return rule;
		}
		return null;
	}

	// TODO evaluate how long this takes or eliminate this once the converted
	// rule priorities are properly calculated
	static private void sortRules(List<? extends AuditRule> rules) {
		for (BaseDecisionTreeRule rule : rules) {
			rule.calculatePriority();
		}

		Collections.sort(rules);
		Collections.reverse(rules);
	}
}
