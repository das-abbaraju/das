package com.picsauditing.employeeguard.services.calculator;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class SkillStatusCalculator {

	public static SkillStatus calculateStatusFromSkill(final AccountSkillProfile accountSkillProfile) {
		return calculateStatus(accountSkillProfile, DateBean.today());
	}

	private static SkillStatus calculateStatus(final AccountSkillProfile accountSkillProfile, final Date today) {
		if (accountSkillProfile == null) {
			return SkillStatus.Expired;
		}

		Date endDate = accountSkillProfile.getEndDate();

		if (endDate == null || endDate.before(today)) {
			return SkillStatus.Expired;
		} else if (endDate.before(DateBean.addMonths(today, 1))) {
			return SkillStatus.Expiring;
		} else {
			return SkillStatus.Completed;
		}
	}

	public static SkillStatus calculateStatusRollUp(final Collection<AccountSkillProfile> accountSkillProfiles) {
		if (CollectionUtils.isEmpty(accountSkillProfiles)) {
			throw new IllegalArgumentException("accountSkillEmployees Collection cannot be empty.");
		}

		Date today = DateBean.today();
		SkillStatus lowestStatus = SkillStatus.Completed;
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			SkillStatus calculatedStatus = calculateStatus(accountSkillProfile, today);

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
