package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.beans.Transient;
import java.util.Comparator;

@SuppressWarnings("serial")
@Entity
@Table(name = "flag_criteria_contractor_operator")
public class InsuranceCriteriaContractorOperator extends BaseTable {

    private ContractorAccount contractorAccount;
    private OperatorAccount operatorAccount;
    private FlagCriteria flagCriteria;
    private int insuranceLimit;

    @ManyToOne
    @JoinColumn(name = "contractorID", nullable = false)
    public ContractorAccount getContractorAccount() {
        return contractorAccount;
    }

    public void setContractorAccount(ContractorAccount contractorAccount) {
        this.contractorAccount = contractorAccount;
    }

    @ManyToOne
    @JoinColumn(name = "criteriaID", nullable = false)
    public FlagCriteria getFlagCriteria() {
        return flagCriteria;
    }

    public void setFlagCriteria(FlagCriteria flagCriteria) {
        this.flagCriteria = flagCriteria;
    }

    @ManyToOne
    @JoinColumn(name = "operatorID", nullable = false)
    public OperatorAccount getOperatorAccount() {
        return operatorAccount;
    }

    public void setOperatorAccount(OperatorAccount operatorAccount) {
        this.operatorAccount = operatorAccount;
    }

    public int getInsuranceLimit() {
        return insuranceLimit;
    }

    public void setInsuranceLimit(int insuranceLimit) {
        this.insuranceLimit = insuranceLimit;
    }

    public boolean meetsCriteria(AuditData contractorsLimit) {
        return meetsCriteria(Integer.parseInt(contractorsLimit.getAnswer().replace(",", "")));
    }
   public boolean meetsCriteria(int contractorsLimit) {
        return contractorsLimit >= this.getInsuranceLimit();
    }
}
