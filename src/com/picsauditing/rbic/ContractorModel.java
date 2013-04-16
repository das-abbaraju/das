package com.picsauditing.rbic;

import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.InsuranceCriteriaContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
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
        String result = null;
        ContractorAudit pqf = null;
        for (ContractorAudit audit : contractor.getAudits()) {
            if (audit.getAuditType().isPqf()) {
                pqf = audit;
                break;
            }
        }

        if (pqf != null) {
            for (AuditData pqfAnswer : pqf.getData())
                if (pqfAnswer.getQuestion().getId() == questionID) {
                    result = pqfAnswer.getAnswer();
                    break;
                }
        }

        return result;
    }

    public void updateCriteriaLimitForContractor(int flagCriteriaId, int opId, int limit) {
        InsuranceCriteriaContractorOperator insuranceCriteriaContractorOperator = null;

        try {
            insuranceCriteriaContractorOperator =
                    insuranceCriteriaContractorOperatorDAO.findBy(flagCriteriaId, contractor.getId(), opId);
        } catch (NoResultException whyDoesntThisJustReturnNullInsteadOfThrowingAnExceptionUgggghhhhhh) {};

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

    public void setSafetyRisk(LowMedHigh safetyRisk) {
        contractor.setSafetyRisk(safetyRisk);
    }

}