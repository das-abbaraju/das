package com.picsauditing.actions.cron;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.mail.NoUsersDefinedException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class EmailUpcomingImplementationAudits extends CronTask {
    private static String NAME = "EmailUpcomingImplementationAudits";
    private ContractorAuditDAO contractorAuditDAO;

    public EmailUpcomingImplementationAudits(ContractorAuditDAO contractorAuditDAO) {
        super(NAME);
        this.contractorAuditDAO = contractorAuditDAO;
    }

    protected void run() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);

        List<ContractorAudit> caList = contractorAuditDAO.findScheduledAuditsByAuditId(AuditType.IMPLEMENTATION_AUDIT,
                DateBean.setToStartOfDay(cal.getTime()), DateBean.setToEndOfDay(cal.getTime()));
        for (ContractorAudit ca : caList) {
            try {
                EventSubscriptionBuilder.notifyUpcomingImplementationAudit(ca);
            } catch (NoUsersDefinedException e) {
                logger.error("NoUsersDefinedException in EmailUpcomingImplementationAudits for ContractorAudit = " + ca.getId());
            } catch (IOException e) {
                logger.error("IOException in EmailUpcomingImplementationAudits for ContractorAudit = " + ca.getId());
            }
        }
    }
}
