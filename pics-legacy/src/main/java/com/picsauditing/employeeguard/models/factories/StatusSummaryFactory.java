package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.models.StatusSummary;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusSummaryFactory {

	public <M extends StatusSummary, T> M addStatusSummary(final M model, final Map<T, SkillStatus> statusMap) {
		Map<SkillStatus, Integer> statusCountMap = getCount(statusMap);

		model.setCompleted(statusCountMap.get(SkillStatus.Complete));
		model.setPending(statusCountMap.get(SkillStatus.Pending));
		model.setExpiring(statusCountMap.get(SkillStatus.Expiring));
		model.setExpired(statusCountMap.get(SkillStatus.Expired));

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
			put(SkillStatus.Complete, 0);
			put(SkillStatus.Pending, 0);
			put(SkillStatus.Expiring, 0);
			put(SkillStatus.Expired, 0);
		}};
	}

}
