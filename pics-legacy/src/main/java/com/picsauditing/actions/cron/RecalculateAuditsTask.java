package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

import java.util.Date;
import java.util.List;

public class RecalculateAuditsTask extends CronTask {
    private static String NAME = "RecalculateAuditCategories";
    private ContractorAuditDAO contractorAuditDAO;
    private AuditPercentCalculator auditPercentCalculator;

    public RecalculateAuditsTask(ContractorAuditDAO contractorAuditDAO, AuditPercentCalculator auditPercentCalculator) {
        super(NAME);
        this.contractorAuditDAO = contractorAuditDAO;
        this.auditPercentCalculator = auditPercentCalculator;
    }

    protected void run() throws Throwable {
        // TODO we shouldn't recacluate audits, but only categories.
        // This shouldn't be needed at all anymore
        List<ContractorAudit> conList = contractorAuditDAO.findAuditsNeedingRecalculation();
        for (ContractorAudit cAudit : conList) {
            try {
                auditPercentCalculator.percentCalculateComplete(cAudit, true);
                cAudit.setLastRecalculation(new Date());
                cAudit.setAuditColumns(new User(User.SYSTEM));
                contractorAuditDAO.save(cAudit);
            } catch (Exception e) {
                logger.error("RecalculateAudits auditID = " + cAudit.getId());
            }
        }
    }
}
