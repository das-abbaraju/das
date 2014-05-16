package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class SkillStatusCalculator {

	public static SkillStatus calculateStatusFromSkill(final AccountSkillEmployee accountSkillEmployee) {
		return calculateStatus(accountSkillEmployee, DateBean.today());
	}

	public static SkillStatus calculateStatusRollUp(final Collection<AccountSkillEmployee> accountSkillEmployees) {
		if (CollectionUtils.isEmpty(accountSkillEmployees)) {
			throw new IllegalArgumentException("accountSkillEmployees Collection cannot be empty.");
		}

		Date today = DateBean.today();
		SkillStatus lowestStatus = SkillStatus.Completed;
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

	private static SkillStatus calculateStatus(final AccountSkillEmployee accountSkillEmployee, final Date today) {
		if (accountSkillEmployee == null) {
			return SkillStatus.Expired;
		}

		/* Figure out endDate in runtime, looking at the information in AccountSkill, instead of relying on
		 endDate stamped on AccountSkillEmployee, because the expiration criteria may have been updated for that skill
		  */
		Date endDate =ExpirationCalculator.calculateExpirationDate(accountSkillEmployee);

		if (endDate == null || endDate.before(today)) {
			return SkillStatus.Expired;
		} else if (endDate.before(DateBean.addMonths(today, 1))) {
			return SkillStatus.Expiring;
		} else {
			return SkillStatus.Completed;
		}
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
