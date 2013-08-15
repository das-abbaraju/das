package com.picsauditing.models.audits;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AuditPeriodModel {
    public ContractorAudit findAudit(List<ContractorAudit> audits, AuditType auditType, String auditFor) {
        for (ContractorAudit audit:audits) {
            if (audit.getAuditType().equals(auditType)) {
                if (auditFor != null && !auditFor.equals(audit.getAuditFor())) {
                    return null;
                }

                return audit;
            }
        }

        return null;
    }

    public List<String> getAuditForByDate(AuditType auditType, Date currentDate) {
        List<String> auditFors = new ArrayList<>();

        if (!auditType.getPeriod().isMonthlyQuarterlyYearly()) {
            return auditFors;
        }

        if (currentDate == null) {
            currentDate = new Date();
        }

        Calendar date = Calendar.getInstance();
        date.setTime(currentDate);

        if (auditType.getPeriod().isMonthly()) {
            date.set(Calendar.DAY_OF_MONTH, 1);
            for (int i=0; i<auditType.getMaximumActive(); i++) {
                date.add(Calendar.MONTH, -1);
                auditFors.add(String.format("%d-%02d", date.get(Calendar.YEAR), (date.get(Calendar.MONTH) +1)));
            }
        }

        if (auditType.getPeriod().isQuarterly()) {
            date.set(Calendar.DAY_OF_MONTH, 1);
            int month = date.get(Calendar.MONTH);
            if (month <=2) {
                month = 0;
            } else if (month <=3) {
                month = 3;
            } else if (month <=6) {
                month = 6;
            } else {
                month = 9;
            }
            date.set(Calendar.MONTH, month);
            date.getTime();

            for (int i=0; i<auditType.getMaximumActive(); i++) {
                date.add(Calendar.MONTH, -3);
                date.getTime();
                int quarter = 0;
                switch (date.get(Calendar.MONTH)) {
                    case 0:
                        quarter = 1;
                        break;
                    case 3:
                        quarter = 2;
                        break;
                    case 6:
                        quarter = 3;
                        break;
                    case 9:
                        quarter = 4;
                        break;
                };
                auditFors.add(String.format("%d:%d", date.get(Calendar.YEAR), quarter));
            }
        }

        if (auditType.getPeriod().isYearly() || auditType.getPeriod().isCustomDate()) {
            if (auditType.getPeriod().isYearly()) {
                date.set(Calendar.DAY_OF_MONTH, 1);
                date.set(Calendar.MONTH, Calendar.JANUARY);
            } else {
                date.set(Calendar.DAY_OF_MONTH, auditType.getAnchorDay());
                date.set(Calendar.MONTH, auditType.getAnchorMonth() - 1);
                date.getTime();
                System.out.println("Current :" + currentDate);
                System.out.println("Target  :" + date.getTime());
                if (currentDate.before(date.getTime())) {
                    date.add(Calendar.YEAR, 1);
                    System.out.println("Modified  :" + date.getTime());
                }
            }
            date.getTime();
            for (int i=0; i<auditType.getMaximumActive(); i++) {
                date.add(Calendar.YEAR, -1);
                date.getTime();
                auditFors.add(String.format("%d", date.get(Calendar.YEAR)));
            }
        }

        return auditFors;
    }
}
