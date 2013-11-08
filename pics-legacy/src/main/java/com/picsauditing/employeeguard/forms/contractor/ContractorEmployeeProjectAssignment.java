package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class ContractorEmployeeProjectAssignment implements Comparable<ContractorEmployeeProjectAssignment> {

	private boolean assigned;
	private int employeeId;
	private String name;
	private String title;
	private List<SkillStatus> skillStatuses;
	// May not be needed anymore
	private List<Integer> assignedGroupIds;

	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<SkillStatus> getSkillStatuses() {
		return skillStatuses;
	}

	public void setSkillStatuses(List<SkillStatus> skillStatuses) {
		this.skillStatuses = skillStatuses;
	}

	public List<Integer> getAssignedGroupIds() {
		return assignedGroupIds;
	}

	public void setAssignedGroupIds(List<Integer> assignedGroupIds) {
		this.assignedGroupIds = assignedGroupIds;
	}

	public boolean hasRoles() {
		return CollectionUtils.isNotEmpty(assignedGroupIds);
	}

	public boolean hasRole(int roleId) {
		if (CollectionUtils.isEmpty(assignedGroupIds)) {
			return false;
		}

		return assignedGroupIds.contains(roleId);
	}

	@Override
	public int compareTo(ContractorEmployeeProjectAssignment that) {
		if (this.name.equalsIgnoreCase(that.name)) {
			return this.title.compareToIgnoreCase(that.title);
		}

		return this.name.compareToIgnoreCase(that.name);
	}
}
