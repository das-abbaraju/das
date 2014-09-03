package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.ContractorTagDAO;
import com.picsauditing.auditbuilder.dao.DocumentDataDAO;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.DocumentUtilityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AuditBuilderBase {
	protected ContractorAccount contractor;
	protected Set<ContractorType> contractorTypes = new HashSet<>();
	protected Set<Trade> trades = new HashSet<>();
    @Autowired
    private ContractorTagDAO contractorTagDAO;
    @Autowired
    private DocumentDataDAO auditDataDAO;

    public void setContractorTagDAO(ContractorTagDAO contractorTagDAO) {
        this.contractorTagDAO = contractorTagDAO;
    }

    public ContractorTagDAO getContractorTagDAO() {
        return contractorTagDAO;
    }

    public void setAuditDataDAO(DocumentDataDAO auditDataDAO) {
        this.auditDataDAO = auditDataDAO;
    }

    public DocumentDataDAO getAuditDataDAO() {
        return auditDataDAO;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
        initialize();
    }

    protected void initialize() {
        for (ContractorTrade ct : contractor.getTrades()) {
            this.trades.add(ct.getTrade());
        }
        if (this.trades.size() == 0) {
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
        if (contractor.isTransportationServices())
            contractorTypes.add(ContractorType.Transportation);
    }

    protected Map<Integer, OperatorTag> getRequiredTags(List<? extends DocumentRule> rules) {
		Map<Integer, OperatorTag> tagsNeeded = new HashMap<>();
		for (DocumentRule rule : rules) {
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

	protected boolean isValid(DocumentRule rule, Map<Integer, DocumentData> contractorAnswers, Map<Integer, OperatorTag> opTags) {
		if (rule.getQuestion() != null && !DocumentUtilityService.isMatchingAnswer(rule, contractorAnswers.get(rule.getQuestion().getId()))) {
			return false;
		}

		if (rule.getTag() != null && opTags.get(rule.getTag().getId()) == null) {
			return false;
		}

		return true;
	}

	public boolean evaluateRule(DocumentRule rule, Map<Integer, List<DocumentData>> allQuestionAnswers, Map<Integer, OperatorTag> opTags) {

		if (!evaluateRuleForQuestion(rule, allQuestionAnswers)) {
			return false;
		}

		if (rule.getTag() != null && opTags.get(rule.getTag().getId()) == null) {
			return false;
		}

		return true;
	}

	private boolean evaluateRuleForQuestion(DocumentRule rule, Map<Integer, List<DocumentData>> allQuestionAnswers) {
		if (rule.getQuestion() != null) {
			List<DocumentData> questionAnswers = allQuestionAnswers.get(rule.getQuestion().getId());

			DocumentData questionAnswer = chooseAnswerToEvaluate(rule, questionAnswers);
			if (!DocumentUtilityService.isMatchingAnswer(rule, questionAnswer)) {
				return false;
			}
		}
		return true;
	}

	DocumentData chooseAnswerToEvaluate(DocumentRule rule, List<DocumentData> questionAnswers) {
		DocumentData questionAnswer = null;

		if (rule.appliesToASpecificYear()) {
			questionAnswer = findAnswerForSpecificYear(rule, questionAnswers);
		} else if (questionAnswers.size() > 0) {
			questionAnswer = questionAnswers.get(0);
		}
		return questionAnswer;
	}

	private DocumentData findAnswerForSpecificYear(DocumentRule rule, List<DocumentData> questionAnswers) {
		for (DocumentData questionAnswer : questionAnswers) {
			if (answerYearMatchesRuleYear(questionAnswer, rule)) {
				return questionAnswer;
			}
		}
		return null;
	}

	private boolean answerYearMatchesRuleYear(DocumentData questionAnswer, DocumentRule rule) {
		int answerYear = DocumentUtilityService.getAuditYear(questionAnswer.getAudit());
		int ruleYear = rule.getYearToCheck().getYear();

		return answerYear == ruleYear;
	}
}