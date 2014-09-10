package com.picsauditing.contractor.service;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.TriState;

public class ContractorAccountService {

    public TriState isSmallBusiness(ContractorAccount contractorAccount) {
        if (contractorAccount == null) {
            throw new IllegalArgumentException("contractorAccount can not be null");
        }

        if (contractorAccount.getNumberOfEmployees() == null) {
            return TriState.UNKNOWN;
        }

        //todo: Lookup from enum
        // return CountryBusinessSize.isSmallBusiness(contractorAccount.getBillingCountry().getIsoCode(), contractorAccount.getNumberOfEmployees())
        return TriState.FALSE;

    }
}
