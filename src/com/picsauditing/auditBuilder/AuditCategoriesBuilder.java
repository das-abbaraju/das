package com.picsauditing.auditBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;

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
	// TODO: the operator in both these maps are the same. Maybe we should consider creating an OperatorDetail object to
	// contain the rule and the Set
	private Map<OperatorAccount, Set<AuditCategory>> categoriesPerOperator = new HashMap<OperatorAccount, Set<AuditCategory>>();
	
	private AuditType auditType = null;
	private OperatorAccount auditFor = null;

	public AuditCategoriesBuilder(AuditCategoryRuleCache auditCategoryRuleCache, ContractorAccount contractor) {
		super(contractor);
		this.ruleCache = auditCategoryRuleCache;
	}

	public Set<AuditCategory> calculate(ContractorAudit conAudit) {
		Collection<OperatorAccount> operators = new HashSet<OperatorAccount>();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
					operators.add(caop.getOperator());
				}
			}
		}
		return calculate(conAudit, operators);

	}

	/**
	 * 
	 * @param conAudit
	 * @param auditOperators
	 *            Pass the Operators (CAOP), NOT the Governing Bodies (CAO)
	 * @return
	 */
	public Set<AuditCategory> calculate(ContractorAudit conAudit, Collection<OperatorAccount> auditOperators) {
		Set<AuditCategory> categories = new HashSet<AuditCategory>();

		auditType = conAudit.getAuditType(); 
		if (auditType.getId() == AuditType.WELCOME) {
			categories.addAll(conAudit.getAuditType().getCategories());
			return categories;
		}

		operators.clear();
		if (auditOperators.size() == 0)
			return categories;
		
		if (conAudit.getAuditType().getId() == AuditType.FIELD) { 
			// field audits will only have caos that are manually specified (not by rules)
			operators.put(conAudit.getRequestingOpAccount(), null);
			auditFor = conAudit.getRequestingOpAccount();
		} else {
			for (OperatorAccount operator : auditOperators) {
				operators.put(operator, null);
			}
		}

		List<AuditCategoryRule> rules = ruleCache.getRules(contractor, conAudit.getAuditType());

		// Prune Rules
		Map<Integer, OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, AuditData> answers = getAuditAnswers(rules, conAudit);
		if (tags.size() > 0 || answers.size() > 0) {
			Iterator<AuditCategoryRule> iterator = rules.iterator();
			while (iterator.hasNext()) {
				AuditCategoryRule rule = iterator.next();
				if (!isValid(rule, answers, tags))
					iterator.remove();
			}
		}

		for (AuditCategory category : conAudit.getAuditType().getCategories()) {
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : auditOperators) {
						AuditCategoryRule rule = getApplicable(rules, category, trade, type, operator);
						if (rule != null && rule.isInclude()) {
							// We need to add this category to the audit
							categories.add(category);

							if (!categoriesPerOperator.containsKey(operator))
								categoriesPerOperator.put(operator, new HashSet<AuditCategory>());
							categoriesPerOperator.get(operator).add(category);

							if (rule.isMoreSpecific(operators.get(operator))) {
								operators.put(operator, rule);
							}
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
			if (rule.isApplies(auditCategory))
				if (rule.isApplies(trade))
					if (rule.isApplies(type))
						if (rule.isApplies(operator))
							return rule;
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
				
				OperatorAccount governingBody = determineGoverningBody(rule.getOperatorAccount(), operator);
				if (!caos.containsKey(governingBody))
					caos.put(governingBody, new HashSet<OperatorAccount>());
				// Add the operator (caop) to one and only one governingBody
				// (cao)
				if (auditType != null && auditType.getId() == AuditType.FIELD
						&& auditFor != null) {
					caos.get(governingBody).add(auditFor);
					break;
				} else {
					caos.get(governingBody).add(operator);
				}
			}
		}
		
		if (auditType != null && auditType.getId() == AuditType.WELCOME && caos.isEmpty()) {
			caos.put(picsGlobal, new HashSet<OperatorAccount>());
		}

		return caos;
	}

	private OperatorAccount determineGoverningBody(OperatorAccount governingBody, OperatorAccount operator) {
		OperatorAccount picsGlobal = new OperatorAccount("PICS Global");
		picsGlobal.setId(4);
		
		if ((auditType != null && (auditType.getId() == AuditType.WELCOME
						|| auditType.isDesktop() || auditType
						.isImplementation()))) {
			return picsGlobal;
		}

		if (auditType != null
				&& auditType.isPqf()
				&& (governingBody == null || Account.PICS_CORPORATE
						.contains(governingBody.getId()))) {
			return operator;
		}

		if (governingBody == null)
			return picsGlobal;
		
		return governingBody;
	}

	private Map<Integer, AuditData> getAuditAnswers(List<? extends AuditRule> rules, ContractorAudit conAudit) {
		Map<Integer, AuditData> answers = new HashMap<Integer, AuditData>();
		for (AuditRule rule : rules) {
			if (rule.getQuestion() != null) {
				int currentQuestionId = rule.getQuestion().getId();
				ContractorAudit auditContainingCurrentQuestion = conAudit;
			
				if (!conAudit.getAuditType().equals(rule.getQuestion().getAuditType())) {
					auditContainingCurrentQuestion = findMostRecentAudit(rule.getQuestion().getAuditType().getId());
				}
				
				AuditData answer = findAnswer(auditContainingCurrentQuestion, currentQuestionId);
				answers.put(currentQuestionId, answer);
			}
		}
		
		return answers;
	}
	
	private ContractorAudit findMostRecentAudit(int auditTypeId) {
		ContractorAudit mostRecentAudit = null;
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == auditTypeId) {
				if (mostRecentAudit == null 
						|| mostRecentAudit.getCreationDate().before(audit.getCreationDate())) {
					mostRecentAudit = audit;
				}
			}
		}
		
		return mostRecentAudit;
	}

	private AuditData findAnswer(ContractorAudit auditContainingCurrentQuestion, int currentQuestionId) {
		if (auditContainingCurrentQuestion != null) {
			for (AuditData answer : auditContainingCurrentQuestion.getData()) {
				if (answer.getQuestion().getId() == currentQuestionId) {
					return answer;
				}
			}
		}
		
		return null;
	}

	public boolean isCategoryApplicable(AuditCategory category, ContractorAuditOperator cao) {
		for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
			Set<AuditCategory> operatorCategories = categoriesPerOperator.get(caop.getOperator());
			if (operatorCategories != null && operatorCategories.contains(category))
				return true;
		}
		
		return false;
	}
}
