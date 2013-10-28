package com.picsauditing.auditBuilder;

import java.util.*;

import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.SpringUtils;

public abstract class AuditBuilderBase {
	protected ContractorAccount contractor;
	protected Set<ContractorType> contractorTypes = new HashSet<ContractorType>();
	protected Set<Trade> trades = new HashSet<Trade>();
	
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
		Map<Integer, OperatorTag> tagsNeeded = new HashMap<Integer, OperatorTag>();
		for (AuditRule rule : rules) {
			if (rule.getTag() != null)
				tagsNeeded.put(rule.getTag().getId(), null);
		}
		if (tagsNeeded.size() > 0) {
			ContractorTagDAO dao = (ContractorTagDAO) SpringUtils.getBean("ContractorTagDAO");
			List<ContractorTag> contractorTags = dao.getContractorTags(contractor.getId(), tagsNeeded.keySet());
			for (ContractorTag contractorTag : contractorTags) {
				tagsNeeded.put(contractorTag.getTag().getId(), contractorTag.getTag());
			}
		}
		return tagsNeeded;
	}

	// Warning: This assumes that there is a single answer per question, which is NOT guaranteed. Consider evaluateRule() instead.
	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers, Map<Integer, OperatorTag> opTags) {
		if (rule.getQuestion() != null && !rule.isMatchingAnswer(contractorAnswers.get(rule.getQuestion().getId()))) {
			return false;
		}

		if (rule.getTag() != null && opTags.get(rule.getTag().getId()) == null) {
			return false;
		}

		return true;
	}

	boolean evaluateRule(AuditRule rule, Map<Integer, List<AuditData>> allQuestionAnswers, Map<Integer, OperatorTag> opTags) {

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
			if (!rule.isMatchingAnswer(questionAnswer)) {
				return false;
			}
		}
		return true;
	}

	AuditData chooseAnswerToEvaluate(AuditRule rule, List<AuditData> questionAnswers) {
		AuditData questionAnswer;

		if (rule.appliesToASpecificYear()) {
			questionAnswer = findAnswerForSpecificYear(rule, questionAnswers);
		} else {
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
		int answerYear = questionAnswer.getAudit().getAuditYear();
		int ruleYear = rule.getYearToCheck().getYear();

		return answerYear == ruleYear;
	}
}
