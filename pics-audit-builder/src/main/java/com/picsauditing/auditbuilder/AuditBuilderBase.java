package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.ContractorTagDAO;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AuditService;

import java.util.*;

public abstract class AuditBuilderBase {
	protected ContractorAccount contractor;
	protected Set<ContractorType> contractorTypes = new HashSet<>();
	protected Set<Trade> trades = new HashSet<>();
    protected ContractorTagDAO contractorTagDAO;

    public void setContractorTagDAO(ContractorTagDAO contractorTagDAO) {
        this.contractorTagDAO = contractorTagDAO;
    }

	public AuditBuilderBase(ContractorAccount contractor) {
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
		// FIXME PICS-4324 follow-up: Replace the following code with ...
		// contractorTypes =  contractor.getAccountTypes();
		// ... and find other places to do it, too.
		if (contractor.isOnsiteServices())
			contractorTypes.add(ContractorType.Onsite);
		if (contractor.isOffsiteServices())
			contractorTypes.add(ContractorType.Offsite);
		if (contractor.isMaterialSupplier())
			contractorTypes.add(ContractorType.Supplier);
		if (contractor.isTransportationServices())
			contractorTypes.add(ContractorType.Transportation);
	}

	protected Map<Integer, OperatorTag> getRequiredTags(List<? extends AuditRule> rules) {
		Map<Integer, OperatorTag> tagsNeeded = new HashMap<>();
		for (AuditRule rule : rules) {
			if (rule.getTag() != null)
				tagsNeeded.put(rule.getTag().getId(), null);
		}
		if (tagsNeeded.size() > 0) {
			List<ContractorTag> contractorTags = contractorTagDAO.getContractorTags(contractor.getId(), tagsNeeded.keySet());
			for (ContractorTag contractorTag : contractorTags) {
				tagsNeeded.put(contractorTag.getTag().getId(), contractorTag.getTag());
			}
		}
		return tagsNeeded;
	}

	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers, Map<Integer, OperatorTag> opTags) {
		if (rule.getQuestion() != null && !AuditService.isMatchingAnswer(rule, contractorAnswers.get(rule.getQuestion().getId()))) {
			return false;
		}

		if (rule.getTag() != null && opTags.get(rule.getTag().getId()) == null) {
			return false;
		}

		return true;
	}

	public boolean evaluateRule(AuditRule rule, Map<Integer, List<AuditData>> allQuestionAnswers, Map<Integer, OperatorTag> opTags) {

		if (!evaluateRuleForQuestion(rule, allQuestionAnswers)) {
			return false;
		}

		if (rule.getTag() != null && opTags.get(rule.getTag().getId()) == null) {
			return false;
		}

		return true;
	}

	private boolean evaluateRuleForQuestion(AuditRule rule, Map<Integer, List<AuditData>> allQuestionAnswers) {
		if (rule.getQuestion() != null) {
			List<AuditData> questionAnswers = allQuestionAnswers.get(rule.getQuestion().getId());

			AuditData questionAnswer = chooseAnswerToEvaluate(rule, questionAnswers);
			if (!AuditService.isMatchingAnswer(rule, questionAnswer)) {
				return false;
			}
		}
		return true;
	}

	AuditData chooseAnswerToEvaluate(AuditRule rule, List<AuditData> questionAnswers) {
		AuditData questionAnswer = null;

		if (rule.appliesToASpecificYear()) {
			questionAnswer = findAnswerForSpecificYear(rule, questionAnswers);
		} else if (questionAnswers.size() > 0) {
			questionAnswer = questionAnswers.get(0);
		}
		return questionAnswer;
	}

	private AuditData findAnswerForSpecificYear(AuditRule rule, List<AuditData> questionAnswers) {
		for (AuditData questionAnswer : questionAnswers) {
			if (answerYearMatchesRuleYear(questionAnswer, rule)) {
				return questionAnswer;
			}
		}
		return null;
	}

	private boolean answerYearMatchesRuleYear(AuditData questionAnswer, AuditRule rule) {
		int answerYear = AuditService.getAuditYear(questionAnswer.getAudit());
		int ruleYear = rule.getYearToCheck().getYear();

		return answerYear == ruleYear;
	}
}
