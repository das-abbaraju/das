package com.picsauditing.service.csr;

import com.picsauditing.dao.ContractorAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

public class RecommendedCsrService {

    @Autowired
    private ContractorAccountDAO contractorAccountDAO;

    public void acceptRecommendedCsrs(String contractorIds, int acceptedByUserId) throws SQLException {
        contractorAccountDAO.expireCurrentCsrForContractors(contractorIds);
        contractorAccountDAO.acceptRecommendedCsrForList(contractorIds);
    }

    public int rejectRecommendedCsrs(String contractorIds) {
        return contractorAccountDAO.rejectRecommendedAssignmentForList(contractorIds);
    }
}


