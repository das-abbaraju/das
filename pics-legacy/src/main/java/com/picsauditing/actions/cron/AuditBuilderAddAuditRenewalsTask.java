package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AuditBuilderAddAuditRenewalsTask implements CronTask {

    @Autowired
    private ContractorAuditDAO contractorAuditDAO;
    @Autowired
    private AuditBuilder auditBuilder;

    public String getDescription() {
        return "AuditBuilder_addAuditRenewals";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() throws CronTaskException {
        CronTaskResult results = new CronTaskResult();
        List<ContractorAccount> contractors = contractorAuditDAO.findContractorsWithExpiringAudits();
        for (ContractorAccount contractor : contractors) {
            try {
                // TODO Testing the cron DON'T COMMIT!!
                // auditBuilder.buildAudits(contractor);
                // contractorAuditDAO.save(contractor);
                results.getLogger().append(", " + contractor.getId());
            } catch (Exception e) {
                results.getLogger().append("\nContractor ERROR id = " + contractor.getId() + " - " + e.getMessage());
            }
        }
        results.setSuccess(true);
        return results;
    }
}
