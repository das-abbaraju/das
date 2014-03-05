package com.picsauditing.service.contractor;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.contractor.ContractorCertificateDao;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.contractor.CertificateType;
import com.picsauditing.model.contractor.ContractorCertificate;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractorCertificateService {

    // todo: This is temporary. Create an apiUser specifically for internal calls like pdf generation
    public static final int AETest_ID = 91532;

    @Autowired
    private ContractorCertificateDao contractorCertificateDao;

    @Autowired
    private UserDAO userDao;

    public ContractorCertificate issueCertificate(ContractorCertificate contractorCertificate) {
        contractorCertificateDao.save(contractorCertificate);
        return contractorCertificate;
    }

    public ContractorCertificate getSsipCertificate(ContractorAccount contractor) {
        return contractorCertificateDao.findMostRecentByContractor(contractor, CertificateType.SSIP);
    }

    public String getApiKeyForPdfGeneration() throws Exception {
        User apiUser = userDao.find(AETest_ID);
        if (apiUser == null) {
            throw new Exception("Null api user for pdf generation");
        }
        if (Strings.isEmpty(apiUser.getApiKey())) {
            throw new Exception("Invalid apiKey for pdf generation");
        }
        return apiUser.getApiKey();
    }
}
