package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.Date;

public class SkillStatusCalculator {

    public static SkillStatus calculateStatusFromSkill(final AccountSkillEmployee accountSkillEmployee) {
        return calculateStatus(accountSkillEmployee, DateBean.today());
    }

    private static SkillStatus calculateStatus(final AccountSkillEmployee accountSkillEmployee, final Date today) {
        if (accountSkillEmployee == null) {
            return SkillStatus.Expired;
        }

        Date endDate = accountSkillEmployee.getEndDate();

        if (endDate == null || endDate.before(today)) {
            return SkillStatus.Expired;
        } else if (endDate.before(DateBean.addMonths(today, 1))) {
            return SkillStatus.Expiring;
        } else {
            return SkillStatus.Complete;
        }
    }

    public static SkillStatus calculateStatusRollUp(final Collection<AccountSkillEmployee> accountSkillEmployees) {
        if (CollectionUtils.isEmpty(accountSkillEmployees)) {
            throw new IllegalArgumentException("accountSkillEmployees Collection cannot be empty.");
        }

        Date today = DateBean.today();
        SkillStatus lowestStatus = SkillStatus.Complete;
        for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
            SkillStatus calculatedStatus = calculateStatus(accountSkillEmployee, today);

            // exit early if any status is "Expired", because we have already hit the lowest status
            if (calculatedStatus.isExpired()) {
                return SkillStatus.Expired;
            }

            if (calculatedStatus.compareTo(lowestStatus) < 0) {
                lowestStatus = calculatedStatus;
            }
        }

        return lowestStatus;
    }
}
