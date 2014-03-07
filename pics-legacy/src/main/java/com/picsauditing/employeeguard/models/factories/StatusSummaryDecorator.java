package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.StatusSummarizable;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class StatusSummaryDecorator {

	public static <M extends StatusSummarizable> M addStatusSummaryRollup(final M model, final Collection<SkillStatus> statuses) {
		Map<SkillStatus, Integer> statusCountMap = getCountOfRollup(statuses);

		return addStatusSummaryFromCounts(model, statusCountMap);
	}

	private static Map<SkillStatus, Integer> getCountOfRollup(final Collection<SkillStatus> statuses) {
		Map<SkillStatus, Integer> statusCount = buildMapPrepopulatedWithKeys();
		for (SkillStatus skillStatus : statuses) {
			int count = statusCount.get(skillStatus) + 1;
			statusCount.put(skillStatus, count);
		}

		return statusCount;
	}

	public static <M extends StatusSummarizable, T> M addStatusSummary(final M model, final Map<T, SkillStatus> statusMap) {
		Map<SkillStatus, Integer> statusCountMap = getCount(statusMap);

		return addStatusSummaryFromCounts(model, statusCountMap);
	}

	private static <M extends StatusSummarizable> M addStatusSummaryFromCounts(final M model, final Map<SkillStatus, Integer> statusCountMap) {
		model.setCompleted(statusCountMap.get(SkillStatus.Complete));
		model.setPending(statusCountMap.get(SkillStatus.Pending));
		model.setExpiring(statusCountMap.get(SkillStatus.Expiring));
		model.setExpired(statusCountMap.get(SkillStatus.Expired));

		return model;
	}

	private static <T> Map<SkillStatus, Integer> getCount(final Map<T, SkillStatus> statusMap) {
		Map<SkillStatus, Integer> statusCount = buildMapPrepopulatedWithKeys();
		for (T type : statusMap.keySet()) {
			SkillStatus skillStatus = statusMap.get(type);
			int count = statusCount.get(skillStatus) + 1;
			statusCount.put(skillStatus, count);
		}

		return statusCount;
	}

	private static Map<SkillStatus, Integer> buildMapPrepopulatedWithKeys() {
		return new HashMap<SkillStatus, Integer>() {{
			put(SkillStatus.Complete, 0);
			put(SkillStatus.Pending, 0);
			put(SkillStatus.Expiring, 0);
			put(SkillStatus.Expired, 0);
		}};
	}

}
