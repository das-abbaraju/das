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

    public int acceptRecommendedCsrs(String contractorIds) {
        List<Integer> contractorIdsList = parseContractorIdsString(contractorIds);

        List<ContractorAccount> contractorList = contractorAccountDAO.findByIDs(ContractorAccount.class, contractorIdsList);

        for (ContractorAccount contractor: contractorList) {
            contractor.setCurrentCsr(contractor.getRecommendedCsr());
            contractorAccountDAO.save(contractor);
        }

        return contractorList.size();
    }


    public int rejectRecommendedCsrs(String contractorIds) {
        List<Integer> contractorIdsList = parseContractorIdsString(contractorIds);

        List<ContractorAccount> contractorList = contractorAccountDAO.findByIDs(ContractorAccount.class, contractorIdsList);

        for (ContractorAccount contractor: contractorList) {
            contractor.setRecommendedCsr(contractor.getCurrentCsr());
            contractorAccountDAO.save(contractor);
        }

        return contractorList.size();
    }


    private List<Integer> parseContractorIdsString(String contractorIds) {
        String[] contractorsIdsStringArray = contractorIds.split(",");
        List<Integer> contractorIdsList = new ArrayList<Integer>();

        for (String contractorId: contractorsIdsStringArray) {
            contractorIdsList.add(Integer.valueOf(contractorId.trim()));
        }
        return contractorIdsList;
    }
}
