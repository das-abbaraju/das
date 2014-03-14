package com.picsauditing.service.registration;


import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class RegistrationRequestService {

    private final ContractorAccountDAO contractorAccountDAO;

    public RegistrationRequestService(
            ContractorAccountDAO dao
    ) {
        this.contractorAccountDAO = dao;
    }

    public ContractorAccount preRegistrationFromKey(final String registrationKey) {
        if (Strings.isEmpty(registrationKey))
            return new ContractorAccount();

        final List<ContractorAccount> potentialRegistrants = findByHash(registrationKey);
        if (potentialRegistrants.isEmpty())
            return new ContractorAccount();

        final ContractorAccount registrant = potentialRegistrants.get(0);
        //TODO: Write cron to scrub registration hashes no longer needed.
        if (registrant.getStatus().isRequested())
            return registrant;
        else
            return new ContractorAccount();
    }

    private List<ContractorAccount> findByHash(String key) {
        final List<ContractorAccount> results = contractorAccountDAO.findWhere("a.registrationHash = '" + key + "'");
        return (results == null)
                ? new ArrayList<ContractorAccount>(0)
                : results;
    }

}
