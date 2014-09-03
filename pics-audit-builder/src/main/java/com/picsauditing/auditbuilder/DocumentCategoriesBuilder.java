package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.DocumentUtilityService;

import java.util.*;

public class DocumentCategoriesBuilder extends DocumentBuilderBase {
	private DocumentCategoryRuleCache ruleCache;

	private Map<OperatorAccount, DocumentCategoryRule> operators = new HashMap<>();
	private Map<OperatorAccount, Set<DocumentCategory>> categoriesPerOperator = new HashMap<>();

	private AuditType auditType = null;
	private OperatorAccount auditFor = null;

    public DocumentCategoriesBuilder() {

    }

	public DocumentCategoriesBuilder(DocumentCategoryRuleCache auditCategoryRuleCache, ContractorAccount contractor) {
		setContractor(contractor);
		this.ruleCache = auditCategoryRuleCache;
	}

    public void calculate(int auditId, Collection<Integer> operatorIds) {
        ContractorDocument audit = getAuditDataDAO().find(ContractorDocument.class, auditId);
        Collection<OperatorAccount> operators = new ArrayList<>();
        for (int id:operatorIds) {
            operators.add(getAuditDataDAO().find(OperatorAccount.class, id));
        }
        calculate(audit, operators);
    }

	public Set<DocumentCategory> calculate(ContractorDocument conAudit) {
		Collection<OperatorAccount> operators = new HashSet<>();
		for (ContractorDocumentOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				for (ContractorDocumentOperatorPermission caop : cao.getCaoPermissions()) {
					operators.add(caop.getOperator());
				}
			}
		}
		return calculate(conAudit, operators);
	}

	public Set<DocumentCategory> calculate(ContractorDocument conAudit, Collection<OperatorAccount> auditOperators) {
		Set<DocumentCategory> categories = new HashSet<>();

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

		List<DocumentCategoryRule> rules = ruleCache.getRules(contractor, conAudit.getAuditType());

		Map<Integer, OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, DocumentData> answers = getAuditAnswers(rules, conAudit);
			Iterator<DocumentCategoryRule> iterator = rules.iterator();
			while (iterator.hasNext()) {
				DocumentCategoryRule rule = iterator.next();
				if (!isValid(rule, answers, tags))
					iterator.remove();
			}

		for (DocumentCategory category : conAudit.getAuditType().getCategories()) {
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : auditOperators) {
						DocumentCategoryRule rule = getApplicable(rules, category, trade, type, operator);
						if (rule != null && rule.isInclude()) {
							categories.add(category);

							if (!categoriesPerOperator.containsKey(operator))
								categoriesPerOperator.put(operator, new HashSet<DocumentCategory>());
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

	private DocumentCategoryRule getApplicable(List<DocumentCategoryRule> rules, DocumentCategory documentCategory, Trade trade,
			ContractorType type, OperatorAccount operator) {
		for (DocumentCategoryRule rule : rules) {
			if (DocumentUtilityService.isApplies(rule, documentCategory))
                if (DocumentUtilityService.isApplies(rule, trade))
                    if (DocumentUtilityService.isApplies(rule, type))
                        if (DocumentUtilityService.isApplies(rule, operator))
							return rule;
		}
		return null;
	}

	protected boolean isValid(DocumentCategoryRule rule, Map<Integer, DocumentData> contractorAnswers,
	                          Map<Integer, OperatorTag> opTags) {
		DocumentCategoryRule documentCategoryRule = rule;
		if (documentCategoryRule.getDependentAuditType() != null && documentCategoryRule.getDependentDocumentStatus() != null) {
			boolean found = false;
			for (ContractorDocument audit : contractor.getAudits()) {
				if (!DocumentUtilityService.isExpired(audit)
						&& audit.getAuditType().equals(documentCategoryRule.getDependentAuditType())
						&& (DocumentUtilityService.hasCaoStatus(audit, documentCategoryRule.getDependentDocumentStatus()) ||
						DocumentUtilityService.hasCaoStatusAfter(audit, documentCategoryRule.getDependentDocumentStatus()))) {
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
			DocumentCategoryRule rule = operators.get(operator);
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

	private Map<Integer, DocumentData> getAuditAnswers(List<? extends DocumentRule> rules, ContractorDocument conAudit) {
		Map<Integer, DocumentData> answers = new HashMap<>();
		for (DocumentRule rule : rules) {
			if (rule.getQuestion() != null) {
				int currentQuestionId = rule.getQuestion().getId();
				ContractorDocument auditContainingCurrentQuestion = conAudit;

				if (!conAudit.getAuditType().equals(DocumentUtilityService.getAuditType(rule.getQuestion()))) {
					auditContainingCurrentQuestion = findMostRecentAudit(DocumentUtilityService.getAuditType(rule.getQuestion()).getId());
				}

				DocumentData answer = findAnswer(auditContainingCurrentQuestion, currentQuestionId);
				answers.put(currentQuestionId, answer);
			}
		}

		return answers;
	}

	private ContractorDocument findMostRecentAudit(int auditTypeId) {
		ContractorDocument mostRecentAudit = null;
		for (ContractorDocument audit : contractor.getAudits()) {
			if (audit.getAuditType().getId() == auditTypeId) {
				if (mostRecentAudit == null
						|| mostRecentAudit.getCreationDate().before(audit.getCreationDate())) {
					mostRecentAudit = audit;
				}
			}
		}

		return mostRecentAudit;
	}

	private DocumentData findAnswer(ContractorDocument auditContainingCurrentQuestion, int currentQuestionId) {
		if (auditContainingCurrentQuestion != null) {
			for (DocumentData answer : auditContainingCurrentQuestion.getData()) {
				if (answer.getQuestion().getId() == currentQuestionId) {
					return answer;
				}
			}
		}

		return null;
	}

    public boolean isCategoryApplicable(int categoryId, int caoId) {
        DocumentCategory category = getAuditDataDAO().find(DocumentCategory.class, categoryId);
        ContractorDocumentOperator cao = getAuditDataDAO().find(ContractorDocumentOperator.class, caoId);
        return isCategoryApplicable(category, cao);
    }

	public boolean isCategoryApplicable(DocumentCategory category, ContractorDocumentOperator cao) {
		for (ContractorDocumentOperatorPermission caop : cao.getCaoPermissions()) {
			Set<DocumentCategory> operatorCategories = categoriesPerOperator.get(caop.getOperator());
			if (operatorCategories != null && operatorCategories.contains(category) && ContractorDocumentCategories.isCategoryEffective(category, cao.getAudit().getEffectiveDate()))
				return true;
		}

		return false;
	}

    public DocumentCategoryRuleCache getRuleCache() {
        return ruleCache;
    }

    public void setRuleCache(DocumentCategoryRuleCache ruleCache) {
        this.ruleCache = ruleCache;
    }
}