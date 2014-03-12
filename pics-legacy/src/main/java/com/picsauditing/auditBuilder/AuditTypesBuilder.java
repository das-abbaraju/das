package com.picsauditing.auditBuilder;

import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditTypesBuilder extends AuditBuilderBase {
	private AuditTypeRuleCache ruleCache;
	private List<AuditTypeRule> rules;
	private AuditDataDAO auditDataDAO;

	public class AuditTypeDetail {
		/**
		 * The AuditTypeRule that is responsible for including this auditType for this contractor
		 */
		public AuditTypeRule rule;
		/**
		 * Operator Accounts that require this audit (CAOPs)
		 */
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	}

	public AuditTypesBuilder(AuditTypeRuleCache ruleCache, ContractorAccount contractor) {
		super(contractor);
		this.ruleCache = ruleCache;
	}

	public Set<AuditTypeDetail> calculate() {
		Set<AuditTypeDetail> types = new HashSet<AuditTypeDetail>();

		rules = ruleCache.getRules(contractor);

		// Prune Rules
		Map<Integer, OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, List<AuditData>> answers = buildQuestionAnswersMap(rules);
		rules = evaluateRulesAndFilterOutNegatives(rules, tags, answers);

		/**
		 * We will never have a rule that says to include all audit types. So assuming that rule.getAuditType is never
		 * NULL is fine. This fact also allows us to only evaluate the auditTypes for the rules we have rather than
		 * using all auditTypes.
		 */
		Set<AuditType> allCandidateAuditTypes = new HashSet<AuditType>();
		for (AuditTypeRule rule : rules) {
			if (rule.isInclude()) {
				allCandidateAuditTypes.add(rule.getAuditType());
			}
		}

		// Get the operator list once
		List<OperatorAccount> operatorAccounts = new ArrayList<OperatorAccount>();
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			if (operator.isOperator() && !operator.getStatus().isDeactivated() && !operator.getStatus().isDeleted()) {
				operatorAccounts.add(operator);
			}
		}

		for (AuditType auditType : allCandidateAuditTypes) {
			List<AuditTypeRule> rulesForThisAuditType = new ArrayList<AuditTypeRule>();
			for (AuditTypeRule rule : rules) {
				if (auditType.equals(rule.getAuditType())) {
					rulesForThisAuditType.add(rule);
				}
			}

			// Welcome Audits do not csre about trades or contractor types
			if (auditType.getId() == AuditType.WELCOME) {
				Trade blank = new Trade();
				blank.setId(-1);

				AuditTypeDetail detail = new AuditTypeDetail();
				for (OperatorAccount operator : operatorAccounts) {
					AuditTypeRule rule = getApplicable(rulesForThisAuditType, auditType, blank, null, operator);
					if (rule != null && rule.isInclude()) {
						// We need to add this category to the audit
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
							// We need to add this category to the audit
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
				if (rule.isApplies(trade))
					if (rule.isApplies(type))
						if (rule.isApplies(operator))
							return rule;
		}
		return null;
	}

    protected boolean isValidRuleForDependentAuditStatus(AuditRule rule) {
        AuditTypeRule auditTypeRule = (AuditTypeRule) rule;
        if (auditTypeRule.getDependentAuditType() != null && auditTypeRule.getDependentAuditStatus() != null) {
            boolean found = false;
            for (ContractorAudit audit : contractor.getAudits()) {
                if (!audit.isExpired()
                        && audit.getAuditType().equals(auditTypeRule.getDependentAuditType())
                        && (audit.hasCaoStatus(auditTypeRule.getDependentAuditStatus()) ||
                        audit.hasCaoStatusAfter(auditTypeRule.getDependentAuditStatus()))) {
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

	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers,
	                          Map<Integer, OperatorTag> opTags) {
		AuditTypeRule auditTypeRule = (AuditTypeRule) rule;

		// Based on PICS-2734, we're going to check for ManuallyAdded in AuditBuilder line 62
		//if (auditTypeRule.isManuallyAdded())
		//return false;

		if (auditTypeRule.getDependentAuditType() != null && auditTypeRule.getDependentAuditStatus() != null) {
			boolean found = false;
			for (ContractorAudit audit : contractor.getAudits()) {
				if (!audit.isExpired()
						&& audit.getAuditType().equals(auditTypeRule.getDependentAuditType())
						&& (audit.hasCaoStatus(auditTypeRule.getDependentAuditStatus()) ||
						audit.hasCaoStatusAfter(auditTypeRule.getDependentAuditStatus()))) {
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
                AuditData visibleQuestionAnswer = auditDataDAO.findAnswerToQuestion(data.getAudit().getId(), data.getQuestion().getVisibleQuestion().getId());
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
        for (AuditCatData acd:data.getAudit().getCategories()) {
            if (acd.getCategory().getId() == data.getQuestion().getCategory().getId())
                return acd.isApplies();
        }

        return true;
    }

    private List<AuditData> findAnswersByContractorAndQuestion(ContractorAccount contractor, AuditQuestion question) {
		if (auditDataDAO == null) {
			auditDataDAO = SpringUtils.getBean("AuditDataDAO");
		}
		return auditDataDAO.findAnswersByContractorAndQuestion(contractor, question);
	}

	public List<AuditTypeRule> getRules() {
		return rules;
	}
}
