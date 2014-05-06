package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

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
            return SkillStatus.Completed;
        }
    }

    public static SkillStatus calculateStatusRollUp(final Collection<AccountSkillEmployee> accountSkillEmployees) {
        if (CollectionUtils.isEmpty(accountSkillEmployees)) {
            throw new IllegalArgumentException("accountSkillEmployees Collection cannot be empty.");
        }

        Date today = DateBean.today();
        //-- Default to the highest severity (Expired) since we are calculating the lowest status.
        SkillStatus lowestStatus = SkillStatus.Expired;
        for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
            lowestStatus = SkillStatus.Completed;
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

	public static <E> Map<E, SkillStatus> getOverallStatusPerEntity(final Map<E, ? extends Collection<SkillStatus>> entitySkillStatusMap) {
		if (MapUtils.isEmpty(entitySkillStatusMap)) {
			return Collections.emptyMap();
		}

		Map<E, SkillStatus> overallSkillStatusMap = new HashMap<>();
		for (E entity : entitySkillStatusMap.keySet()) {
			overallSkillStatusMap.put(entity, calculateOverallStatus(entitySkillStatusMap.get(entity)));
		}

		return overallSkillStatusMap;
	}

	public static SkillStatus calculateOverallStatus(final Collection<SkillStatus> skillStatuses) {
		if (CollectionUtils.isEmpty(skillStatuses)) {
			throw new IllegalArgumentException("skillStatuses should not be empty or null.");
		}


		SkillStatus worstStatus = SkillStatus.Completed;
		for (SkillStatus skillStatus : skillStatuses) {

			// exit early if any status is "Expired", because we have already hit the lowest status
			if (skillStatus.isExpired()) {
				return SkillStatus.Expired;
			}

			if (skillStatus.compareTo(worstStatus) < 0) {
				worstStatus = skillStatus;
			}
		}

		return worstStatus;
	}
}
