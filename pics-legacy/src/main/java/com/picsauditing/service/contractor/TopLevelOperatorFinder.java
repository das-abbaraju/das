package com.picsauditing.service.contractor;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TopLevelOperatorFinder {
    @Autowired
    private OperatorAccountDAO operatorAccountDAO;

    public List<OperatorAccount> findAllTopLevelOperators(ContractorAccount contractor) {
        return operatorAccountDAO.findAllTopLevelOperators(contractor.getId());
    }
}
