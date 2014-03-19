package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.*;

import java.util.ArrayList;

public class ContractorAccountBuilder {
    private ContractorAccount contractor = new ContractorAccount();

    public ContractorAccount build() {
        return contractor;
    }

    public ContractorAccountBuilder audit(ContractorAudit audit) {
        contractor.getAudits().add(audit);
        audit.setContractorAccount(contractor);
        return this;
    }

    public ContractorAccountBuilder operator(OperatorAccount operator) {
        return operator(operator, ApprovalStatus.Y);
    }

    public ContractorAccountBuilder operator(OperatorAccount operator, ApprovalStatus workStatus) {
        ContractorOperator joinTable = new ContractorOperator();
        joinTable.setOperatorAccount(operator);
        joinTable.setContractorAccount(contractor);
        joinTable.setWorkStatus(workStatus);
        contractor.getOperators().add(joinTable);

        return this;
    }

    public ContractorAccountBuilder id(int i) {
        contractor.setId(i);
        return this;
    }

    public ContractorAccountBuilder insuranceCriteriaOperator(FlagCriteria flagCriteria, OperatorAccount operatorAccount, int limit) {
        if (contractor.getInsuranceCriteriaContractorOperators() == null) {
            contractor.setInsuranceCriteriaContractorOperators(new ArrayList<InsuranceCriteriaContractorOperator>());
        }
        InsuranceCriteriaContractorOperator criteria = new InsuranceCriteriaContractorOperator();
        criteria.setFlagCriteria(flagCriteria);
        criteria.setOperatorAccount(operatorAccount);
        criteria.setInsuranceLimit(limit);
        contractor.getInsuranceCriteriaContractorOperators().add(criteria);
        return this;
    }

    public ContractorAccountBuilder transportationServices() {
        contractor.setTransportationServices(true);
        return this;
    }

    public ContractorAccountBuilder materialSupplier() {
        contractor.setMaterialSupplier(true);
        return this;
    }

    public ContractorAccountBuilder onSiteServices() {
        contractor.setOnsiteServices(true);
        return this;
    }

    public ContractorAccountBuilder primaryContact(User user) {
        contractor.setPrimaryContact(user);
        return this;
    }

    public ContractorAccountBuilder doesNotPerformOnSiteServices() {
        contractor.setOnsiteServices(false);
        return this;
    }

    public ContractorAccountBuilder hasEmployeeGuard(boolean hasEmployeeGuard) {
        contractor.setHasEmployeeGuard(hasEmployeeGuard);
        return this;
    }

    public ContractorAccountBuilder country(Country country) {
        contractor.setCountry(country);
        return this;
    }
}
