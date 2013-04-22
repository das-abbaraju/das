package com.picsauditing.rbic;

import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.InsuranceCriteriaContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
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
        InsuranceCriteriaContractorOperator insuranceCriteriaContractorOperator  =
                    insuranceCriteriaContractorOperatorDAO.findBy(flagCriteriaId, contractor.getId(), opId);

        if (insuranceCriteriaContractorOperator == null) {
            insuranceCriteriaContractorOperator = new InsuranceCriteriaContractorOperator();
            FlagCriteria flagCriteria = flagCriteriaDAO.find(flagCriteriaId);
            OperatorAccount operatorAccount = operatorAccountDAO.find(opId);
            insuranceCriteriaContractorOperator.setOperatorAccount(operatorAccount);
            insuranceCriteriaContractorOperator.setFlagCriteria(flagCriteria);
            insuranceCriteriaContractorOperator.setContractorAccount(contractor);
        }

        insuranceCriteriaContractorOperator.setAuditColumns();
        insuranceCriteriaContractorOperator.setInsuranceLimit(limit);
        insuranceCriteriaContractorOperatorDAO.save(insuranceCriteriaContractorOperator);
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

}