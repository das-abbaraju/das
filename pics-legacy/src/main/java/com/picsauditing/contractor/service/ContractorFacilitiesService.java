package com.picsauditing.contractor.service;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.contractor.AddContractorFacilityFailedException;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractorFacilitiesService {

    @Autowired
    private TopLevelOperatorFinder topLevelOperatorFinder;

    public void addContractorFacilityForQualifiedContractor(ContractorAccount contractorAccount, OperatorAccount operatorAccount, FacilityChanger facilityChanger) throws AddContractorFacilityFailedException {

        if (contractorAccount.getLogoForSingleOperatorContractor() == null) {
            OperatorAccount logoForSingleOperatorContractor = topLevelOperatorFinder.findLogoForSingleOperatorContractor(contractorAccount);
            contractorAccount.setLogoForSingleOperatorContractor(logoForSingleOperatorContractor);
        }

        contractorAccount.setRenew(true);

        try {
            facilityChanger.add();
        } catch (Exception e) {
            throw new AddContractorFacilityFailedException("Failed to add operator: " + operatorAccount.getId() + " for contractor: " + contractorAccount.getId(),
                    e, contractorAccount, operatorAccount);
        }

    }

}
