package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AuditService;

import java.util.*;

public class AuditCategoriesBuilder extends AuditBuilderBase {
	private AuditCategoryRuleCache2 ruleCache;

	private Map<OperatorAccount, AuditCategoryRule> operators = new HashMap<>();
	private Map<OperatorAccount, Set<AuditCategory>> categoriesPerOperator = new HashMap<>();

	private AuditType auditType = null;
	private OperatorAccount auditFor = null;

	public AuditCategoriesBuilder(AuditCategoryRuleCache2 auditCategoryRuleCache, ContractorAccount contractor) {
		setContractor(contractor);
		this.ruleCache = auditCategoryRuleCache;
	}

    public void calculate(int auditId, Collection<Integer> operatorIds) {
        ContractorAudit audit = getAuditDataDAO().find(ContractorAudit.class, auditId);
        Collection<OperatorAccount> operators = new ArrayList<>();
        for (int id:operatorIds) {
            operators.add(getAuditDataDAO().find(OperatorAccount.class, id));
        }
        calculate(audit, operators);
    }

	public Set<AuditCategory> calculate(ContractorAudit conAudit) {
		Collection<OperatorAccount> operators = new HashSet<>();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
					operators.add(caop.getOperator());
				}
			}
		}
		return calculate(conAudit, operators);
	}

	public Set<AuditCategory> calculate(ContractorAudit conAudit, Collection<OperatorAccount> auditOperators) {
		Set<AuditCategory> categories = new HashSet<>();

		auditType = conAudit.getAuditType();
		if (auditType.getId() == AuditType.WELCOME) {
			categories.addAll(conAudit.getAuditType().getCategories());
			return categories;
		}

		operators.clear();
		if (auditOperators.size() == 0)
			return categories;

		if (conAudit.getAuditType().getId() == AuditType.FIELD) {
			operators.put(conAudit.getRequestingOpAccount(), null);
			auditFor = conAudit.getRequestingOpAccount();
		} else {
			for (OperatorAccount operator : auditOperators) {
				operators.put(operator, null);
			}
		}

		List<AuditCategoryRule> rules = ruleCache.getRules(contractor, conAudit.getAuditType());

		Map<Integer, OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, AuditData> answers = getAuditAnswers(rules, conAudit);
			Iterator<AuditCategoryRule> iterator = rules.iterator();
			while (iterator.hasNext()) {
				AuditCategoryRule rule = iterator.next();
				if (!isValid(rule, answers, tags))
					iterator.remove();
			}

		for (AuditCategory category : conAudit.getAuditType().getCategories()) {
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : auditOperators) {
						AuditCategoryRule rule = getApplicable(rules, category, trade, type, operator);
						if (rule != null && rule.isInclude()) {
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

	private AuditCategoryRule getApplicable(List<AuditCategoryRule> rules, AuditCategory auditCategory, Trade trade,
			ContractorType type, OperatorAccount operator) {
		for (AuditCategoryRule rule : rules) {
			if (AuditService.isApplies(rule, auditCategory))
                if (AuditService.isApplies(rule, trade))
                    if (AuditService.isApplies(rule, type))
                        if (AuditService.isApplies(rule, operator))
							return rule;
		}
		return null;
	}

	protected boolean isValid(AuditCategoryRule rule, Map<Integer, AuditData> contractorAnswers,
	                          Map<Integer, OperatorTag> opTags) {
		AuditCategoryRule auditCategoryRule = rule;
		if (auditCategoryRule.getDependentAuditType() != null && auditCategoryRule.getDependentAuditStatus() != null) {
			boolean found = false;
			for (ContractorAudit audit : contractor.getAudits()) {
				if (!AuditService.isExpired(audit)
						&& audit.getAuditType().equals(auditCategoryRule.getDependentAuditType())
						&& (AuditService.hasCaoStatus(audit, auditCategoryRule.getDependentAuditStatus()) ||
						AuditService.hasCaoStatusAfter(audit, auditCategoryRule.getDependentAuditStatus()))) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return super.isValid(rule, contractorAnswers, opTags);
	}

	public Map<OperatorAccount, Set<OperatorAccount>> getCaos() {
		Map<OperatorAccount, Set<OperatorAccount>> caos = new HashMap<>();

		OperatorAccount picsGlobal = new OperatorAccount("PICS Global");
		picsGlobal.setId(4);

		for (OperatorAccount operator : operators.keySet()) {
			AuditCategoryRule rule = operators.get(operator);
			if (rule == null) {

			} else {

				OperatorAccount governingBody = determineGoverningBody(rule.getOperatorAccount(), operator);
				if (!caos.containsKey(governingBody))
					caos.put(governingBody, new HashSet<OperatorAccount>());

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
						|| auditType.getId() == AuditType.MANUAL_AUDIT || auditType.getId() == AuditType.IMPLEMENTATION_AUDIT))) {
			return picsGlobal;
		}

		if (auditType != null
				&& auditType.getId() == AuditType.PQF
				&& (governingBody == null || Account.PICS_CORPORATE
						.contains(governingBody.getId()))) {
			return operator;
		}

		if (governingBody == null)
			return picsGlobal;

		return governingBody;
	}

	private Map<Integer, AuditData> getAuditAnswers(List<? extends AuditRule> rules, ContractorAudit conAudit) {
		Map<Integer, AuditData> answers = new HashMap<>();
		for (AuditRule rule : rules) {
			if (rule.getQuestion() != null) {
				int currentQuestionId = rule.getQuestion().getId();
				ContractorAudit auditContainingCurrentQuestion = conAudit;

				if (!conAudit.getAuditType().equals(AuditService.getAuditType(rule.getQuestion()))) {
					auditContainingCurrentQuestion = findMostRecentAudit(AuditService.getAuditType(rule.getQuestion()).getId());
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

    public boolean isCategoryApplicable(int categoryId, int caoId) {
        AuditCategory category = getAuditDataDAO().find(AuditCategory.class, categoryId);
        ContractorAuditOperator cao = getAuditDataDAO().find(ContractorAuditOperator.class, caoId);
        return isCategoryApplicable(category, cao);
    }

	public boolean isCategoryApplicable(AuditCategory category, ContractorAuditOperator cao) {
		for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
			Set<AuditCategory> operatorCategories = categoriesPerOperator.get(caop.getOperator());
			if (operatorCategories != null && operatorCategories.contains(category) && ContractorAuditCategories.isCategoryEffective(category, cao.getAudit().getEffectiveDate()))
				return true;
		}

		return false;
	}
}