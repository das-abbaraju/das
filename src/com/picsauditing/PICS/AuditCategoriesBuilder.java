package com.picsauditing.PICS;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditCategoriesBuilder {
	private AuditCategoryRuleCache ruleCache;
	private List<AuditCategoryRule> rules = null;

	private Set<OperatorAccount> governingBodies = null;
	private Map<OperatorAccount, AuditCategoryRule> operators = new HashMap<OperatorAccount, AuditCategoryRule>();

	private ContractorAccount contractor;
	private Set<ContractorType> contractorTypes = new HashSet<ContractorType>();
	private Set<Trade> trades = new HashSet<Trade>();

	public AuditCategoriesBuilder(AuditCategoryRuleCache auditCategoryRuleCache, ContractorAccount contractor) {
		this.ruleCache = auditCategoryRuleCache;
		this.contractor = contractor;
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			this.operators.put(operator, null);
		}
		for (ContractorTrade ct : contractor.getTrades()) {
			this.trades.add(ct.getTrade());
		}
		if (this.trades.size() == 0) {
			// We have to add a blank trade in case the contractor is missing trades.
			// If we don't then no rules will be found, even wildcard trade rules.
			Trade blank = new Trade();
			blank.setId(-1);
			this.trades.add(blank);
		}
		if (contractor.isOnsiteServices())
			contractorTypes.add(ContractorType.Onsite);
		if (contractor.isOffsiteServices())
			contractorTypes.add(ContractorType.Offsite);
		if (contractor.isMaterialSupplier())
			contractorTypes.add(ContractorType.Supplier);
	}

	public Set<AuditCategory> calculate(AuditType auditType, Collection<OperatorAccount> auditOperators) {
		governingBodies = null;
		operators.clear();
		for (OperatorAccount operator : auditOperators) {
			operators.put(operator, null);
		}

		rules = ruleCache.getApplicableCategoryRules(contractor, auditType);
		// TODO prune rules
		// TODO recalc rules
		for (BaseDecisionTreeRule rule : rules) {
			rule.calculatePriority();
		}
		Collections.sort(rules);
		Collections.reverse(rules);

		Set<AuditCategory> categories = new HashSet<AuditCategory>();
		for (AuditCategory category : auditType.getCategories()) {
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : auditOperators) {
						AuditCategoryRule rule = getApplicable(category, trade, type, operator);
						if (rule != null && rule.isInclude()) {
							// We need to add this category to the audit
							categories.add(category);
							if (rule.isMoreSpecific(operators.get(operator)))
								operators.put(operator, rule);
						}
					}
				}
			}
		}

		return categories;
	}

	/**
	 * Find the first matching rule matching this signature
	 * 
	 * @param auditCategory
	 * @param trade
	 * @param type
	 * @param operator
	 * @return
	 */
	private AuditCategoryRule getApplicable(AuditCategory auditCategory, Trade trade, ContractorType type,
			OperatorAccount operator) {
		for (AuditCategoryRule rule : rules) {
			if (rule.isApplies(auditCategory) && rule.isApplies(trade) && rule.isApplies(type)
					&& rule.isApplies(operator)) {
				return rule;
			}
		}
		return null;
	}

	public List<AuditCategoryRule> getRules() {
		return rules;
	}

	public Set<OperatorAccount> getGoverningBodies() {
		if (governingBodies == null) {
			governingBodies = new HashSet<OperatorAccount>();

			// PICS Consortium
			OperatorAccount picsGlobal = new OperatorAccount("PICS Global");
			picsGlobal.setId(4);

			for (AuditCategoryRule rule : operators.values()) {
				if (rule == null)
					governingBodies.add(picsGlobal);
				else
					governingBodies.add(rule.getOperatorAccount());
			}
		}
		return governingBodies;
	}
}
