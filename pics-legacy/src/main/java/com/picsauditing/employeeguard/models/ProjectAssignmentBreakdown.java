package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

public class ProjectAssignmentBreakdown {

	private Map<SkillStatus, Integer> statusRollup;

	public ProjectAssignmentBreakdown(Map<SkillStatus, Integer> statusRollup) {
		this.statusRollup = statusRollup;
	}

	public int getExpired() {
		return getCount(SkillStatus.Expired);
	}

	public int getExpiring() {
		return getCount(SkillStatus.Expiring);
	}

	public int getPending() {
		return getCount(SkillStatus.Pending);
	}

	public int getComplete() {
		return getCount(SkillStatus.Completed);
	}

	private int getCount(final SkillStatus skillStatus) {
		if (MapUtils.isEmpty(statusRollup)) {
			return 0;
		}

		return statusRollup.containsKey(skillStatus) ? statusRollup.get(skillStatus) : 0;
	}
}
