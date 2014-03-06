package com.picsauditing.service.contractor;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.antlr.grammar.v3.ANTLRv3Parser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContractorOperatorService {
    @Autowired
    protected ContractorOperatorDAO contractorOperatorDAO;

    public void cascadeWorkStatusToParent(ContractorOperator conOp) {
        if (conOp.getOperatorAccount().getParent() != null) {
            ContractorOperator contractorCorporate = contractorOperatorDAO.find(conOp.getContractorAccount().getId(), conOp.getOperatorAccount().getParent().getId());
            if (contractorCorporate != null) {
                if (contractorCorporate.getWorkStatus().isForced() || conOp.getWorkStatus() == contractorCorporate.getWorkStatus())
                    return;

                else if (conOp.getWorkStatus().ordinal() > contractorCorporate.getWorkStatus().ordinal()) {
                    contractorCorporate.setWorkStatus(conOp.getWorkStatus());
                    contractorOperatorDAO.save(contractorCorporate);
                } else {
                    boolean isUniform = areAllContractorRelationshipsUniform(contractorCorporate.getOperatorAccount());
                    if (isUniform) {
                        contractorCorporate.setWorkStatus(conOp.getWorkStatus());
                        contractorOperatorDAO.save(contractorCorporate);
                    }
                }
            }
        }
    }

    public boolean areAllContractorRelationshipsUniform(OperatorAccount operator) {
        if (operator.isAutoApproveRelationships())
            return true;
        return contractorOperatorDAO.isUnifiedWorkStatus(operator.getId());
    }
}
