package com.picsauditing.contractor;

import com.picsauditing.exception.PicsException;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AddContractorFacilityFailedException extends PicsException {

    private final ContractorAccount contractorAccount;
    private final OperatorAccount operatorAccount;

    public AddContractorFacilityFailedException(String message, Throwable cause, ContractorAccount contractorAccount, OperatorAccount operatorAccount) {
        super(message, cause);
        this.contractorAccount = contractorAccount;
        this.operatorAccount = operatorAccount;
    }

    public ContractorAccount getContractorAccount() {
        return contractorAccount;
    }

    public OperatorAccount getOperatorAccount() {
        return operatorAccount;
    }
}
