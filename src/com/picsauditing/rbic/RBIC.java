package com.picsauditing.rbic;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.InsuranceCriteriaContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InsuranceCriteriaContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RBIC extends PicsActionSupport {

    private ContractorAccount contractor;
    @Autowired
    private RulesRunner rulesRunner;
    @Autowired
    private InsuranceCriteriaContractorOperatorDAO insuranceCriteriaContractorOperatorDAO;

    public String execute() {
        rulesRunner.setContractor(contractor);

        for (ContractorOperator contractorOperator : contractor.getOperators()) {
            OperatorAccount operatorAccount = contractorOperator.getOperatorAccount();
            rulesRunner.runInsuranceCriteriaRulesForOperator(operatorAccount);
        }

        return SUCCESS;
    }

    public List<InsuranceCriteriaContractorOperator> getInsuranceLimits() {
        return insuranceCriteriaContractorOperatorDAO.findByContractorId(contractor.getId());
    }

    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }
}
