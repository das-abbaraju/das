package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorSiteAssignmentStatusFactory {

	public List<OperatorSiteAssignmentStatus> create(final int siteId,
	                                                 final String siteName,
	                                                 final Map<Employee, SkillStatus> employeeStatuses) {

		Map<SkillStatus, Integer> statusCounts = getCount(employeeStatuses);

		int completed = statusCounts.get(SkillStatus.Complete);
		int expiring = statusCounts.get(SkillStatus.Expiring);
		int expired = statusCounts.get(SkillStatus.Expired);

		OperatorSiteAssignmentStatus status = new OperatorSiteAssignmentStatus.Builder()
				.id(siteId)
				.name(siteName)
				.employees(completed + expiring + expired)
				.completed(completed)
				.expiring(expiring)
				.expired(expired)
				.build();

		return Arrays.asList(status);
	}

	private Map<SkillStatus, Integer> getCount(final Map<Employee, SkillStatus> employeeStatuses) {
		Map<SkillStatus, Integer> statusCount = buildMapPrepopulatedWithKeys();
		for (Employee employee : employeeStatuses.keySet()) {
			SkillStatus skillStatus = employeeStatuses.get(employee);
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
