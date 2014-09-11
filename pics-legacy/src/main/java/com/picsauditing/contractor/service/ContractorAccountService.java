package com.picsauditing.contractor.service;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.CountryBusinessSize;
import com.picsauditing.util.TriState;

public class ContractorAccountService {

    public TriState isSmallBusiness(ContractorAccount contractorAccount) {
        if (contractorAccount == null) {
            throw new IllegalArgumentException("contractorAccount can not be null");
        }

        if (contractorAccount.getCountry() == null || contractorAccount.getNumberOfEmployees() == null ) {
            return TriState.UNKNOWN;
        }

        boolean smallBusiness = CountryBusinessSize.isSmallBusiness(contractorAccount.getCountry().getIsoCode(), contractorAccount.getNumberOfEmployees());
        return TriState.fromBoolean(smallBusiness);
    }
}
