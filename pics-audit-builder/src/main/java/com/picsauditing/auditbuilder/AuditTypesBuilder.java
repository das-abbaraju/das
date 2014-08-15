package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AccountService;
import com.picsauditing.auditbuilder.service.AuditService;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class AuditTypesBuilder extends AuditBuilderBase implements DocumentTypesBuilder {
    private AuditTypeRuleCache2 ruleCache;
	private List<AuditTypeRule> rules;

    public class AuditTypeDetail {
		public AuditTypeRule rule;
		public Set<OperatorAccount> operators = new HashSet<>();
	}

    public AuditTypesBuilder() {
        super();
    }

    public AuditTypesBuilder(AuditTypeRuleCache2 ruleCache, ContractorAccount contractor) {
        super();
        this.ruleCache = ruleCache;
        this.setContractor(contractor);
    }

    public void setRuleCache(AuditTypeRuleCache2 ruleCache) {
        this.ruleCache = ruleCache;
    }

    public void setContractorId(int contractorId) {
        ContractorAccount contractor = getAuditDataDAO().find(ContractorAccount.class, contractorId);
        this.setContractor(contractor);
    }

	public Set<AuditTypeDetail> calculate() {
		Set<AuditTypeDetail> types = new HashSet<>();

		rules = ruleCache.getRules(contractor);

		Map<Integer, OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, List<AuditData>> answers = buildQuestionAnswersMap(rules);
		rules = evaluateRulesAndFilterOutNegatives(rules, tags, answers);

		Set<AuditType> allCandidateAuditTypes = new HashSet<>();
		for (AuditTypeRule rule : rules) {
			if (rule.isInclude()) {
				allCandidateAuditTypes.add(rule.getAuditType());
			}
		}

		List<OperatorAccount> operatorAccounts = new ArrayList<>();
		for (OperatorAccount operator : AccountService.getOperatorAccounts(contractor)) {
			if (AccountService.isOperator(operator) && !operator.getStatus().isDeactivated() && !operator.getStatus().isDeleted()) {
				operatorAccounts.add(operator);
			}
		}

		for (AuditType auditType : allCandidateAuditTypes) {
			List<AuditTypeRule> rulesForThisAuditType = new ArrayList<>();
			for (AuditTypeRule rule : rules) {
				if (auditType.equals(rule.getAuditType())) {
					rulesForThisAuditType.add(rule);
				}
			}

			if (auditType.getId() == AuditType.WELCOME) {
				Trade blank = new Trade();
				blank.setId(-1);

				AuditTypeDetail detail = new AuditTypeDetail();
				for (OperatorAccount operator : operatorAccounts) {
					AuditTypeRule rule = getApplicable(rulesForThisAuditType, auditType, blank, null, operator);
					if (rule != null && rule.isInclude()) {
						detail.operators.add(operator);
						if (rule.isMoreSpecific(detail.rule))
							detail.rule = rule;
						types.add(detail);
					}
				}

				continue;
			}

			AuditTypeDetail detail = new AuditTypeDetail();
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : operatorAccounts) {
						AuditTypeRule rule = getApplicable(rulesForThisAuditType, auditType, trade, type, operator);
						if (rule != null && rule.isInclude()) {
							detail.operators.add(operator);
							if (rule.isMoreSpecific(detail.rule))
								detail.rule = rule;
							types.add(detail);
						}
					}
				}
			}
		}

		return types;
	}

	private List<AuditTypeRule> evaluateRulesAndFilterOutNegatives(List<AuditTypeRule> rules, Map<Integer, OperatorTag> tags, Map<Integer, List<AuditData>> answers) {
		Iterator<AuditTypeRule> iterator = rules.iterator();
		while (iterator.hasNext()) {
			AuditTypeRule rule = iterator.next();
			if (!isValidRuleForDependentAuditStatus(rule) ||
                    !evaluateRule(rule, answers, tags)) {
				iterator.remove();
			}
		}
		return rules;
	}

	private AuditTypeRule getApplicable(List<AuditTypeRule> rules, AuditType auditType, Trade trade,
	                                    ContractorType type, OperatorAccount operator) {
		for (AuditTypeRule rule : rules) {
			if (auditType.equals(rule.getAuditType()))
				if (AuditService.isApplies(rule, trade))
					if (AuditService.isApplies(rule, type))
						if (AuditService.isApplies(rule, operator))
							return rule;
		}
		return null;
	}

    protected boolean isValidRuleForDependentAuditStatus(AuditRule rule) {
        AuditTypeRule auditTypeRule = (AuditTypeRule) rule;
        if (auditTypeRule.getDependentAuditType() != null && auditTypeRule.getDependentAuditStatus() != null) {
            boolean found = false;
            for (ContractorAudit audit : contractor.getAudits()) {
                if (!AuditService.isExpired(audit)
                        && audit.getAuditType().equals(auditTypeRule.getDependentAuditType())
                        && (AuditService.hasCaoStatus(audit, auditTypeRule.getDependentAuditStatus()) ||
                        AuditService.hasCaoStatusAfter(audit, auditTypeRule.getDependentAuditStatus()))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

	private Map<Integer, List<AuditData>> buildQuestionAnswersMap(List<? extends AuditRule> rules) {
		Map<Integer, List<AuditData>> questionAnswers = new HashMap<>();
		for (AuditRule rule : rules) {
			AuditQuestion auditQuestion = rule.getQuestion();
			if (auditQuestion != null) {
				List<AuditData> answers = findAnswersByContractorAndQuestion(contractor, auditQuestion);
                filterNonVisibleAnswers(answers);
                filterNonApplicableCategoryAnswers(answers);
				questionAnswers.put(auditQuestion.getId(), answers);
			}
		}

		return questionAnswers;
	}

    private void filterNonVisibleAnswers(List<AuditData> answers) {
        Iterator<AuditData> iterator = answers.iterator();
        while (iterator.hasNext()) {
            AuditData data = iterator.next();
            if (data.getQuestion().getVisibleQuestion() != null && data.getQuestion().getVisibleAnswer() != null) {
                AuditData visibleQuestionAnswer = getAuditDataDAO().findAnswerToQuestion(data.getAudit().getId(), data.getQuestion().getVisibleQuestion().getId());
                if (visibleQuestionAnswer != null && !StringUtils.equals(visibleQuestionAnswer.getAnswer(), data.getQuestion().getVisibleAnswer())) {
                    iterator.remove();
                }
            }
        }
    }

    private void filterNonApplicableCategoryAnswers(List<AuditData> answers) {
        Iterator<AuditData> iterator = answers.iterator();
        while (iterator.hasNext()) {
            AuditData data = iterator.next();

            if (!isCategoryApplicable(data)) {
                iterator.remove();
            }
        }

    }

    private boolean isCategoryApplicable(AuditData data) {
        for (AuditCatData acd : data.getAudit().getCategories()) {
            if (acd.getCategory().getId() == data.getQuestion().getCategory().getId())
                return acd.isApplies();
        }

        return true;
    }

    private List<AuditData> findAnswersByContractorAndQuestion(ContractorAccount contractor, AuditQuestion question) {
	    return getAuditDataDAO().findAnswersByContractorAndQuestion(contractor, question);
	}

    public List<AuditTypeRule> getRules() {
        return rules;
    }
}