package com.picsauditing.actions.cron;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.mail.NoUsersDefinedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class EmailUpcomingImplementationAudits implements CronTask {
    @Autowired
    private ContractorAuditDAO contractorAuditDAO;

    public String getDescription() {
        return "TODO";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);

        List<ContractorAudit> caList = contractorAuditDAO.findScheduledAuditsByAuditId(AuditType.IMPLEMENTATION_AUDIT,
                DateBean.setToStartOfDay(cal.getTime()), DateBean.setToEndOfDay(cal.getTime()));
        for (ContractorAudit ca : caList) {
            try {
                EventSubscriptionBuilder.notifyUpcomingImplementationAudit(ca);
                results.getLogger().append(", ").append(ca.getId());
            } catch (NoUsersDefinedException e) {
                results.getLogger().append("NoUsersDefinedException in EmailUpcomingImplementationAudits for ContractorAudit = " + ca.getId());
            } catch (IOException e) {
                results.getLogger().append("IOException in EmailUpcomingImplementationAudits for ContractorAudit = " + ca.getId());
            }
        }
        return results;
    }
}
