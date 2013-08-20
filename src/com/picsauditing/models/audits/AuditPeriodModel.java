package com.picsauditing.models.audits;

import com.picsauditing.PICS.DateBean;
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
                if (auditFor == null || (auditFor != null && auditFor.equals(audit.getAuditFor()))) {
                    return audit;
                }
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
                auditFors.add(0, String.format("%d-%02d", date.get(Calendar.YEAR), (date.get(Calendar.MONTH) +1)));
            }
        }

        if (auditType.getPeriod().isQuarterly()) {
            date.set(Calendar.DAY_OF_MONTH, 1);
            int month = date.get(Calendar.MONTH);
            if (month <=2) {
                month = 0;
            } else if (month <=5) {
                month = 3;
            } else if (month <=8) {
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
                auditFors.add(0, String.format("%d:%d", date.get(Calendar.YEAR), quarter));
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
                if (currentDate.before(date.getTime())) {
                    date.add(Calendar.YEAR, 1);
                }
            }
            date.getTime();
            for (int i=0; i<auditType.getMaximumActive(); i++) {
                date.add(Calendar.YEAR, -1);
                date.getTime();
                auditFors.add(0, String.format("%d", date.get(Calendar.YEAR)));
            }
        }

        return auditFors;
    }

    public Date getEffectiveDateForMonthlyQuarterlyYearly(AuditType auditType, String auditFor) {
        Calendar date = parseAuditFor(auditType, auditFor);
        return date.getTime();
    }

    public Date getExpirationDateForMonthlyQuarterlyYearly(AuditType auditType, String auditFor) {
        Calendar date = parseAuditFor(auditType, auditFor);

        int months = 0;
        if (auditType.getMonthsToExpire() == null) {
            months = 12;
        } else {
            months = auditType.getMonthsToExpire().intValue();
        }
        date.add(Calendar.MONTH, months);
        date.getTime();
        date.add(Calendar.DATE, -1);

        date.setTime(DateBean.setToEndOfDay(date.getTime()));

        return date.getTime();
    }

    private Calendar parseAuditFor(AuditType auditType, String auditFor) {
        Calendar date = Calendar.getInstance();
        int year = 1970;
        int month = 1;
        int day = 1;

        if (auditType.getPeriod().isMonthly()) {
            String[] values = auditFor.split("-");
            year = Integer.parseInt(values[0]);
            month = Integer.parseInt(values[1]) - 1;
        }

        if (auditType.getPeriod().isQuarterly()) {
            String[] values = auditFor.split(":");
            year = Integer.parseInt(values[0]);
            int quarter = Integer.parseInt(values[1]);
            switch (quarter) {
                case 1:
                    month = 0;
                    break;
                case 2:
                    month = 3;
                    break;
                case 3:
                    month = 6;
                    break;
                case 4:
                    month = 9;
                    break;
                default:
                    month = 0;
                    break;
            }
        }

        if (auditType.getPeriod().isYearly() || auditType.getPeriod().isCustomDate()) {
            year = Integer.parseInt(auditFor);

            if (auditType.getPeriod().isYearly())  {
                month = 0;
            } else {
                month = auditType.getAnchorMonth() - 1;
                day = auditType.getAnchorDay();
            }
        }

        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        date.setTime(date.getTime());

        return date;
    }
}
