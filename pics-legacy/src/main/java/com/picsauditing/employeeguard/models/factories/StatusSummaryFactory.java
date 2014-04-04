package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.StatusSummary;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.HashMap;
import java.util.Map;

public class StatusSummaryFactory {

	public <T> StatusSummary create(final Map<T, SkillStatus> statusMap) {
		StatusSummary statusSummary = new StatusSummary();

		Map<SkillStatus, Integer> statusCountMap = getCount(statusMap);
		statusSummary.setCompleted(statusCountMap.get(SkillStatus.Complete));
		statusSummary.setPending(statusCountMap.get(SkillStatus.Pending));
		statusSummary.setExpiring(statusCountMap.get(SkillStatus.Expiring));
		statusSummary.setExpired(statusCountMap.get(SkillStatus.Expired));
		statusSummary.setEmployees(getTotal(statusCountMap));

		return statusSummary;
	}

	public <M extends StatusSummary, T> M addStatusSummary(final M model, final Map<T, SkillStatus> statusMap) {
		Map<SkillStatus, Integer> statusCountMap = getCount(statusMap);

		model.setCompleted(statusCountMap.get(SkillStatus.Completed));
		model.setPending(statusCountMap.get(SkillStatus.Pending));
		model.setExpiring(statusCountMap.get(SkillStatus.Expiring));
		model.setExpired(statusCountMap.get(SkillStatus.Expired));
		model.setEmployees(getTotal(statusCountMap));

		return model;
	}

	private <T> Map<SkillStatus, Integer> getCount(final Map<T, SkillStatus> statusMap) {
		Map<SkillStatus, Integer> statusCount = buildMapPrepopulatedWithKeys();
		for (T type : statusMap.keySet()) {
			SkillStatus skillStatus = statusMap.get(type);
			int count = statusCount.get(skillStatus) + 1;
			statusCount.put(skillStatus, count);
		}

		return statusCount;
	}

	private Map<SkillStatus, Integer> buildMapPrepopulatedWithKeys() {
		return new HashMap<SkillStatus, Integer>() {{
			put(SkillStatus.Completed, 0);
			put(SkillStatus.Pending, 0);
			put(SkillStatus.Expiring, 0);
			put(SkillStatus.Expired, 0);
		}};
	}

	private int getTotal(final Map<SkillStatus, Integer> statusCountMap) {
		int total = 0;

		for (SkillStatus status : statusCountMap.keySet()) {
			total += statusCountMap.get(status);
		}

		return total;
	}

}
