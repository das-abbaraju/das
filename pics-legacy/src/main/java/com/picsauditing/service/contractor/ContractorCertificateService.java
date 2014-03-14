package com.picsauditing.service.contractor;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.contractor.ContractorCertificateDao;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.contractor.CdmScopeItem;
import com.picsauditing.model.contractor.CertificateType;
import com.picsauditing.model.contractor.ContractorCertificate;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

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
        ContractorCertificate certificate = contractorCertificateDao.findMostRecentByContractor(contractor, CertificateType.SSIP);
        String formattedCdmString = formatCdmString(certificate.getCdmScope());
        certificate.setFormattedCdmScope(formattedCdmString);
        return certificate;
    }

    private String formatCdmString(String cdmScope) {
        ArrayList<CdmScopeItem> cdmScopeItems = convertDbValuesToEnumList(cdmScope);
        ArrayList<String> displayNameArray = convertEnumsToDisplayNameArray(cdmScopeItems);
        String formattedCdmString = Strings.implode(displayNameArray, "/");
        return formattedCdmString;
    }

    private ArrayList<String> convertEnumsToDisplayNameArray(ArrayList<CdmScopeItem> cdmScopeItems) {
        ArrayList<String> cdmDisplayNames = new ArrayList<>();
        for (CdmScopeItem cdmScopeItem : cdmScopeItems) {
            cdmDisplayNames.add(cdmScopeItem.getDisplayValue());
        }
        return cdmDisplayNames;
    }

    private ArrayList<CdmScopeItem> convertDbValuesToEnumList(String cdmScope) {
        ArrayList<CdmScopeItem> cdmScopeItems = new ArrayList<>();
        if (Strings.isNotEmpty(cdmScope)) {
            String[] cdmScopeItemDbValues = cdmScope.split(",");
            for (String cdmScopeItemDbValue : cdmScopeItemDbValues) {
                CdmScopeItem cdmScopeItem = CdmScopeItem.fromDbValue(cdmScopeItemDbValue);
                cdmScopeItems.add(cdmScopeItem);
            }
        }
        return cdmScopeItems;
    }

}
