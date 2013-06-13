package com.picsauditing.service.csr;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class RecommendedCsrService {

    @Autowired
    private ContractorAccountDAO contractorAccountDAO;

    public int acceptRecommendedCsrs(String contractorIds, int acceptedByUserId) {
        List<Integer> contractorIdsList = Strings.explodeCommaDelimitedStringOfIds(contractorIds);

        for (Integer conID : contractorIdsList) {
            ContractorAccount contractor = contractorAccountDAO.find(conID);
            contractorAccountDAO.expireCurrentCSRAssignment(conID);
            contractorAccountDAO.assignNewCSR(conID, contractor.getRecommendedCsr().getId());
        }

        return contractorIdsList.size();
    }

    public int rejectRecommendedCsrs(String contractorIds) {
        return contractorAccountDAO.rejectRecommendedAssignmentForList(contractorIds);
    }
}


