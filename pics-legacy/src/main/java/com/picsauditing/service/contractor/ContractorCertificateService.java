package com.picsauditing.service.contractor;

import com.picsauditing.dao.contractor.ContractorCertificateDao;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.contractor.CertificateType;
import com.picsauditing.model.contractor.ContractorCertificate;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractorCertificateService {

    @Autowired
    private ContractorCertificateDao contractorCertificateDao;

    public ContractorCertificate issueCertificate(ContractorCertificate contractorCertificate) {
        contractorCertificateDao.save(contractorCertificate);
        return contractorCertificate;
    }

    public ContractorCertificate getSsipCertificate(ContractorAccount contractor) {
        return contractorCertificateDao.findMostRecentByContractor(contractor, CertificateType.SSIP);
    }

}
