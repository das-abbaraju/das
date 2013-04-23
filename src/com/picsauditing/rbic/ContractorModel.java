package com.picsauditing.rbic;

import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.InsuranceCriteriaContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.rbic.builders.ContractorModelBuilder;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContractorModel implements Serializable {

    @Autowired
    private OperatorAccountDAO operatorAccountDAO;
    @Autowired
    private FlagCriteriaDAO flagCriteriaDAO;
    @Autowired
    private InsuranceCriteriaContractorOperatorDAO insuranceCriteriaContractorOperatorDAO;

    private ContractorAccount contractor;

    public List<InsuranceCriteriaContractorOperator> insuranceCriteriaOperators = new ArrayList<>();
    public boolean hasTag(int tagID) {
        for (ContractorTag tag : contractor.getOperatorTags()) {
            if (tag.getTag().getId() == tagID) {
                return true;
            }
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