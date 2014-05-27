package com.picsauditing.employeeguard.services.status;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class SkillStatusCalculator {

	public static SkillStatus calculateStatusFromSkill(final AccountSkillProfile accountSkillProfile) {
		return calculateStatus(accountSkillProfile, DateBean.today());
	}

	public static SkillStatus calculateStatusRollUp(final Collection<AccountSkillProfile> accountSkillProfiles) {
		if (CollectionUtils.isEmpty(accountSkillProfiles)) {
			throw new IllegalArgumentException("accountSkillProfiles Collection cannot be empty.");
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

	private static SkillStatus calculateStatus(final AccountSkillProfile accountSkillProfile, final Date today) {
		if (accountSkillProfile == null) {
			return SkillStatus.Expired;
		}

		/* Figure out endDate in runtime, looking at the information in AccountSkill, instead of relying on
		   endDate stamped on AccountSkillProfile, because the expiration criteria may have been updated for that skill
		  */
		Date endDate = ExpirationCalculator.calculateExpirationDate(accountSkillProfile);

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
			if (CollectionUtils.isEmpty(entitySkillStatusMap.get(entity))) {
				overallSkillStatusMap.put(entity, SkillStatus.Completed);
			} else {
				overallSkillStatusMap.put(entity, calculateOverallStatus(entitySkillStatusMap.get(entity)));
			}
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

	public static <E> Map<SkillStatus, Integer> statusCount(final Map<E, List<SkillStatus>> entityStatusMap) {
		if (MapUtils.isEmpty(entityStatusMap)) {
			return Collections.emptyMap();
		}

		Map<SkillStatus, Integer> statusCount = buildMapWithCountsToZero();
		for (E entity : entityStatusMap.keySet()) {
			for (SkillStatus skillStatus : entityStatusMap.get(entity)) {
				statusCount.put(skillStatus, statusCount.get(skillStatus) + 1);
			}
		}

		return statusCount;
	}

	private static Map<SkillStatus, Integer> buildMapWithCountsToZero() {
		Map<SkillStatus, Integer> statusCount = new HashMap<>();
		for (SkillStatus skillStatus : SkillStatus.values()) {
			statusCount.put(skillStatus, 0);
		}

		return statusCount;
	}
}
