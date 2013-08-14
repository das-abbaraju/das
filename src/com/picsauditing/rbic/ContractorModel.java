package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.rbic.builders.ContractorModelBuilder;
import com.picsauditing.util.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContractorModel implements Serializable {

    private ContractorAccount contractor;

    public boolean hasTag(int tagID) {
        for (ContractorTag tag : contractor.getOperatorTags()) {
            if (tag.getTag().getId() == tagID) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTrade(int tradeID) {
        for (ContractorTrade conTrade : contractor.getTrades()) {
            if (hasTrade(tradeID, conTrade.getTrade())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTrade(int tradeID, Trade trade) {
        if (trade.getId() == tradeID) {
            return true;
        }

        if (trade.getParent() != null) {
            return hasTrade(tradeID, trade.getParent());
        }

        return false;
    }

    public boolean worksFor(int opID) {
        for (ContractorOperator operator : contractor.getOperators()) {
            if (operator.getOperatorAccount().getId() == opID) {
                return true;
            }
        }
        return false;
    }

    public String findPqfQuestionAnswer(int questionID) {
        List<ContractorAudit> pqf = getPqf(contractor);
        return  getQuestionById(questionID, pqf);
    }

    public String findQuestionAnswer(int questionId) {
        for (ContractorAudit audit : contractor.getAudits()) {
            for (AuditData pqfAnswer : audit.getData())
                if (pqfAnswer.getQuestion().getId() == questionId) {
                    return pqfAnswer.getAnswer();
                }
        }
        return null;
    }

    public double findQuestionAnswerAsNumber(int questionId) {
        String answer = findQuestionAnswer(questionId);
        return Strings.isEmpty(answer) ? 0 : Double.parseDouble(answer);
    }

    public int findQuestionAnswerAsInt(int questionId) {
        String answer = findQuestionAnswer(questionId);
        return Strings.isEmpty(answer) ? 0 : Integer.parseInt(answer);
    }

    public static  String getQuestionById(int questionId, List<ContractorAudit> pqfs) {
        for (ContractorAudit audit: pqfs) {
            for (AuditData pqfAnswer : audit.getData())
                if (pqfAnswer.getQuestion().getId() == questionId) {
                    return pqfAnswer.getAnswer();
                }
        }
        return null;
    }

    public static  List<ContractorAudit> getPqf(ContractorAccount contractor) {
        List<ContractorAudit> results = new ArrayList<>();
        for (ContractorAudit audit : contractor.getAudits()) {
            if (audit.getAuditType().isPqf()) {
                results.add(audit);
            }
        }
        return results;
    }

    public void updateCriteriaLimitForContractor(int flagCriteriaId, int opId, int limit) {
        InsuranceCriteriaContractorOperator insurance  = getContractor().getInsuranceCriteriaContractorOperators(flagCriteriaId, opId);

        if (insurance == null) {
            insurance = new InsuranceCriteriaContractorOperator();

            OperatorAccount operator = getContractor().getOperator(opId);
            FlagCriteria flagCriteria = operator.getFlagCriteria(flagCriteriaId);
            insurance.setOperatorAccount(operator);
            insurance.setFlagCriteria(flagCriteria);
            insurance.setContractorAccount(contractor);

            contractor.getInsuranceCriteriaContractorOperators().add(insurance);
        }

        insurance.setAuditColumns();
        insurance.setInsuranceLimit(limit);
    }

    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }

    public LowMedHigh getSafetyRisk() {
        return contractor.getSafetyRisk();
    }

    public LowMedHigh getProductRisk() {
        return contractor.getProductRisk();
    }

    public LowMedHigh getTransportationRisk() {
        return contractor.getTransportationRisk();
    }

    public static ContractorModelBuilder builder() {
        return new ContractorModelBuilder();
    }
}