package com.picsauditing.actions.cron;

import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class RecalculateAuditsTask implements CronTask {
    @Autowired
    private ContractorAuditDAO contractorAuditDAO;
    @Autowired
    private AuditPercentCalculator auditPercentCalculator;

    public String getDescription() {
        return "RecalculateAuditsTask";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        // TODO we shouldn't recalculate audits, but only categories.
        // This shouldn't be needed at all anymore
        List<ContractorAudit> conList = contractorAuditDAO.findAuditsNeedingRecalculation();
        results.getLogger().append("Recalculating " + conList.size() + " audits: ");
        for (ContractorAudit cAudit : conList) {
            try {
                auditPercentCalculator.percentCalculateComplete(cAudit, true);
                cAudit.setLastRecalculation(new Date());
                cAudit.setAuditColumns(new User(User.SYSTEM));
                contractorAuditDAO.save(cAudit);
                results.getLogger().append(", " + cAudit.getId());
            } catch (Exception e) {
                results.setSuccess(false);
                results.getLogger().append("\n\n ERROR RecalculateAudits auditID = " + cAudit.getId() + "\n");
            }
        }
        return results;
    }
}
