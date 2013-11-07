package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Invoice;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class AuditBuilderAddAuditRenewalsTask implements CronTask {

    @Autowired
    ContractorAuditDAO contractorAuditDAO;
    @Autowired
    AuditBuilder auditBuilder;

    public String getDescription() {
        return "Generate renewal audits to replace expiring audits";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<ContractorAccount> contractors = contractorAuditDAO.findContractorsWithExpiringAudits();
        for (ContractorAccount contractor : contractors) {
            steps.add("Will Build new audits for contractor: " + contractor.getName() + " (" + contractor.getId() + ")");
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult();
        List<ContractorAccount> contractors = contractorAuditDAO.findContractorsWithExpiringAudits();
        for (ContractorAccount contractor : contractors) {
            try {
                auditBuilder.buildAudits(contractor);
                contractorAuditDAO.save(contractor);
                results.getLogger().append(", " + contractor.getId());
            } catch (Exception e) {
                results.getLogger().append("\nContractor ERROR id = " + contractor.getId() + " - " + e.getMessage());
            }
        }
        results.setSuccess(true);
        return results;
    }
}
