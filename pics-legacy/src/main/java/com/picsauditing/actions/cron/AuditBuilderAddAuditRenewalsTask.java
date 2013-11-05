package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

import java.util.List;

public class AuditBuilderAddAuditRenewalsTask extends CronTask {

    public static String RUN_AuditBuilder_addAuditRenewals = "Cron.RunStep.AuditBuilder_addAuditRenewals";
    private ContractorAuditDAO contractorAuditDAO;
    private AuditBuilder auditBuilder;

    public AuditBuilderAddAuditRenewalsTask(ContractorAuditDAO contractorAuditDAO, AuditBuilder auditBuilder) {
        super(RUN_AuditBuilder_addAuditRenewals);
        this.contractorAuditDAO = contractorAuditDAO;
        this.auditBuilder = auditBuilder;
    }

    protected void run() throws CronTaskException {
        List<ContractorAccount> contractors = contractorAuditDAO.findContractorsWithExpiringAudits();
        for (ContractorAccount contractor : contractors) {
            try {
                auditBuilder.buildAudits(contractor);
                contractorAuditDAO.save(contractor);
            } catch (Exception e) {
                logger.error("AuditBuiler.addAuditRenewals() {}", e.getMessage());
            }
        }
    }
}
