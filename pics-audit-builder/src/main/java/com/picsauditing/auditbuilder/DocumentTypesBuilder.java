package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AccountService;
import com.picsauditing.auditbuilder.service.DocumentUtilityService;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class DocumentTypesBuilder extends DocumentBuilderBase {
    private DocumentTypeRuleCache ruleCache;
	private List<DocumentTypeRule> rules;

    public class AuditTypeDetail {
		public DocumentTypeRule rule;
		public Set<OperatorAccount> operators = new HashSet<>();
	}

    public DocumentTypesBuilder() {

    }

    public DocumentTypesBuilder(DocumentTypeRuleCache ruleCache, ContractorAccount contractor) {
        super();
        this.ruleCache = ruleCache;
        this.setContractor(contractor);
    }

    public void setRuleCache(DocumentTypeRuleCache ruleCache) {
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
		Map<Integer, List<DocumentData>> answers = buildQuestionAnswersMap(rules);
		rules = evaluateRulesAndFilterOutNegatives(rules, tags, answers);

		Set<AuditType> allCandidateAuditTypes = new HashSet<>();
		for (DocumentTypeRule rule : rules) {
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
			List<DocumentTypeRule> rulesForThisAuditType = new ArrayList<>();
			for (DocumentTypeRule rule : rules) {
				if (auditType.equals(rule.getAuditType())) {
					rulesForThisAuditType.add(rule);
				}
			}

			if (auditType.getId() == AuditType.WELCOME) {
				Trade blank = new Trade();
				blank.setId(-1);

				AuditTypeDetail detail = new AuditTypeDetail();
				for (OperatorAccount operator : operatorAccounts) {
					DocumentTypeRule rule = getApplicable(rulesForThisAuditType, auditType, blank, null, operator);
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
						DocumentTypeRule rule = getApplicable(rulesForThisAuditType, auditType, trade, type, operator);
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

	private List<DocumentTypeRule> evaluateRulesAndFilterOutNegatives(List<DocumentTypeRule> rules, Map<Integer, OperatorTag> tags, Map<Integer, List<DocumentData>> answers) {
		Iterator<DocumentTypeRule> iterator = rules.iterator();
		while (iterator.hasNext()) {
			DocumentTypeRule rule = iterator.next();
			if (!isValidRuleForDependentAuditStatus(rule) ||
                    !evaluateRule(rule, answers, tags)) {
				iterator.remove();
			}
		}
		return rules;
	}

	private DocumentTypeRule getApplicable(List<DocumentTypeRule> rules, AuditType auditType, Trade trade,
	                                    ContractorType type, OperatorAccount operator) {
		for (DocumentTypeRule rule : rules) {
			if (auditType.equals(rule.getAuditType()))
				if (DocumentUtilityService.isApplies(rule, trade))
					if (DocumentUtilityService.isApplies(rule, type))
						if (DocumentUtilityService.isApplies(rule, operator))
							return rule;
		}
		return null;
	}

    protected boolean isValidRuleForDependentAuditStatus(DocumentRule rule) {
        DocumentTypeRule documentTypeRule = (DocumentTypeRule) rule;
        if (documentTypeRule.getDependentAuditType() != null && documentTypeRule.getDependentDocumentStatus() != null) {
            boolean found = false;
            for (ContractorDocument audit : contractor.getAudits()) {
                if (!DocumentUtilityService.isExpired(audit)
                        && audit.getAuditType().equals(documentTypeRule.getDependentAuditType())
                        && (DocumentUtilityService.hasCaoStatus(audit, documentTypeRule.getDependentDocumentStatus()) ||
                        DocumentUtilityService.hasCaoStatusAfter(audit, documentTypeRule.getDependentDocumentStatus()))) {
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

	private Map<Integer, List<DocumentData>> buildQuestionAnswersMap(List<? extends DocumentRule> rules) {
		Map<Integer, List<DocumentData>> questionAnswers = new HashMap<>();
		for (DocumentRule rule : rules) {
			DocumentQuestion documentQuestion = rule.getQuestion();
			if (documentQuestion != null) {
				List<DocumentData> answers = findAnswersByContractorAndQuestion(contractor, documentQuestion);
                filterNonVisibleAnswers(answers);
                filterNonApplicableCategoryAnswers(answers);
				questionAnswers.put(documentQuestion.getId(), answers);
			}
		}

		return questionAnswers;
	}

    private void filterNonVisibleAnswers(List<DocumentData> answers) {
        Iterator<DocumentData> iterator = answers.iterator();
        while (iterator.hasNext()) {
            DocumentData data = iterator.next();
            if (data.getQuestion().getVisibleQuestion() != null && data.getQuestion().getVisibleAnswer() != null) {
                DocumentData visibleQuestionAnswer = getAuditDataDAO().findAnswerToQuestion(data.getAudit().getId(), data.getQuestion().getVisibleQuestion().getId());
                if (visibleQuestionAnswer != null && !StringUtils.equals(visibleQuestionAnswer.getAnswer(), data.getQuestion().getVisibleAnswer())) {
                    iterator.remove();
                }
            }
        }
    }

    private void filterNonApplicableCategoryAnswers(List<DocumentData> answers) {
        Iterator<DocumentData> iterator = answers.iterator();
        while (iterator.hasNext()) {
            DocumentData data = iterator.next();

            if (!isCategoryApplicable(data)) {
                iterator.remove();
            }
        }

    }

    private boolean isCategoryApplicable(DocumentData data) {
        for (DocumentCatData acd : data.getAudit().getCategories()) {
            if (acd.getCategory().getId() == data.getQuestion().getCategory().getId())
                return acd.isApplies();
        }

        return true;
    }

    private List<DocumentData> findAnswersByContractorAndQuestion(ContractorAccount contractor, DocumentQuestion question) {
	    return getAuditDataDAO().findAnswersByContractorAndQuestion(contractor, question);
	}

    public List<DocumentTypeRule> getRules() {
        return rules;
    }
}