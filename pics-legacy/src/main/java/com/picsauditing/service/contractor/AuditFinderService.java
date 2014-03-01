package com.picsauditing.service.contractor;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AuditFinderService {

    @Autowired
    private ContractorAuditDAO contractorAuditDAO;

    public ContractorAudit findManualAudit(ContractorAccount contractor) {
        List<ContractorAudit> manualAudits = contractorAuditDAO.findByContractorAndAuditType(contractor, AuditType.MANUAL_AUDIT);

        ContractorAudit currentManualAudit = null;

        for (ContractorAudit manualAudit: manualAudits) {
            if (!manualAudit.isExpired()) {
                currentManualAudit = manualAudit;
                break;
            }
        }

        return currentManualAudit;
    }
}
