package com.picsauditing.actions.cron;

import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecalculateAuditsTask implements CronTask {
    @Autowired
    private ContractorAuditDAO contractorAuditDAO;
    @Autowired
    private AuditBuilderFactory auditBuilderFactory;

    public String getDescription() {
        return "Recalculate Audit Information";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<ContractorAudit> conList = contractorAuditDAO.findAuditsNeedingRecalculation();
        for (ContractorAudit cAudit : conList) {
            steps.add("Will recalculate " + cAudit.getContractorAccount().getName() + "'s " + cAudit.getAuditType().getName() + " " + cAudit.getAuditFor());
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        // TODO we shouldn't recalculate audits, but only categories.
        // This shouldn't be needed at all anymore
        List<ContractorAudit> conList = contractorAuditDAO.findAuditsNeedingRecalculation();
        results.getLogger().append("Recalculating " + conList.size() + " audits: ");
        for (ContractorAudit cAudit : conList) {
            try {
                auditBuilderFactory.percentCalculateComplete(cAudit);
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
