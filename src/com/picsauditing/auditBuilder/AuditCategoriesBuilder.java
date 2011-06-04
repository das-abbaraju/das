package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.AnswerMap;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditCategoriesBuilder extends AuditBuilderBase {
	private AuditCategoryRuleCache ruleCache;
	/**
	 * Operators that require this audit and the include rule (with associated Governing Body) that required the
	 * category
	 */
	private Map<OperatorAccount, AuditCategoryRule> operators = new HashMap<OperatorAccount, AuditCategoryRule>();

	private Set<ContractorType> contractorTypes = new HashSet<ContractorType>();
	private Set<Trade> trades = new HashSet<Trade>();

	public AuditCategoriesBuilder(AuditCategoryRuleCache auditCategoryRuleCache, ContractorAccount contractor) {
		this.ruleCache = auditCategoryRuleCache;
		this.contractor = contractor;

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

	public Set<AuditCategory> calculate(ContractorAudit conAudit, Collection<OperatorAccount> auditOperators) {
		operators.clear();
		for (OperatorAccount operator : auditOperators) {
			operators.put(operator, null);
		}

		List<AuditCategoryRule> rules = ruleCache.getApplicableCategoryRules(contractor, conAudit.getAuditType());
		
		// Prune Rules
		Set<OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, AuditData> answers = getAuditAnswers(rules, conAudit);
		Iterator<AuditCategoryRule> iterator = rules.iterator();
		while (iterator.hasNext()) {
			AuditCategoryRule rule = iterator.next();
			if (!isValid(rule, answers, tags))
				iterator.remove();
		}
		reSortRules(rules);

		Set<AuditCategory> categories = new HashSet<AuditCategory>();
		for (AuditCategory category : conAudit.getAuditType().getCategories()) {
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : auditOperators) {
						AuditCategoryRule rule = getApplicable(rules, category, trade, type, operator);
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
	private AuditCategoryRule getApplicable(List<AuditCategoryRule> rules, AuditCategory auditCategory, Trade trade,
			ContractorType type, OperatorAccount operator) {
		for (AuditCategoryRule rule : rules) {
			if (rule.isApplies(auditCategory) && rule.isApplies(trade) && rule.isApplies(type)
					&& rule.isApplies(operator)) {
				return rule;
			}
		}
		return null;
	}

	/**
	 * Based on the set of operators for this audit and the category rules needed for each of those operators, generate
	 * a set of CAOs and corresponding CAOPs. CAOs are also known as Governing Bodies.
	 */
	public Map<OperatorAccount, Set<OperatorAccount>> getCaos() {
		Map<OperatorAccount, Set<OperatorAccount>> caos = new HashMap<OperatorAccount, Set<OperatorAccount>>();

		// We need a PICS Global record to represent the wildcard rules not associated with any operator or corporate
		// account
		OperatorAccount picsGlobal = new OperatorAccount("PICS Global");
		picsGlobal.setId(4);

		for (OperatorAccount operator : operators.keySet()) {
			AuditCategoryRule rule = operators.get(operator);
			if (rule == null) {
				// This operator doesn't require any categories, so I'm just going to ignore it for now
			} else {
				OperatorAccount governingBody = rule.getOperatorAccount();
				if (governingBody == null)
					governingBody = picsGlobal;
				if (!caos.containsKey(governingBody))
					caos.put(governingBody, new HashSet<OperatorAccount>());
				// Add the operator (caop) to one and only one governingBody (cao)
				caos.get(governingBody).add(operator);
			}
		}
		return caos;
	}

	private Map<Integer, AuditData> getAuditAnswers(List<? extends AuditRule> rules, ContractorAudit conAudit) {
		Set<Integer> auditAnswersNeeded = new HashSet<Integer>();
		for (AuditRule rule : rules) {
			if (rule.getQuestion() != null) {
				if (conAudit.getAuditType().equals(rule.getQuestion().getAuditType()))
					auditAnswersNeeded.add(rule.getQuestion().getId());
			}
		}

		Map<Integer, AuditData> answers = new HashMap<Integer, AuditData>();
		if (auditAnswersNeeded.size() > 0) {
			List<AuditData> requiredAnswers = new ArrayList<AuditData>();
			for (AuditData answer : conAudit.getData())
				if (auditAnswersNeeded.contains(answer.getQuestion().getId()))
					requiredAnswers.add(answer);
			AnswerMap answerMap = new AnswerMap(requiredAnswers);
			for (Integer questionID : auditAnswersNeeded) {
				answers.put(questionID, answerMap.get(questionID));
			}
		}
		return answers;
	}

}
