package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class OperatorEmployeeProjectAssignment implements Comparable<OperatorEmployeeProjectAssignment> {

	private int employeeId;
	private String employeeName;
	private String title;
	private int companyId;
	private String companyName;
	private List<Integer> assignedRoleIds;
	private List<SkillStatus> skillStatuses;

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<SkillStatus> getSkillStatuses() {
		return skillStatuses;
	}

	public void setSkillStatuses(List<SkillStatus> skillStatuses) {
		this.skillStatuses = skillStatuses;
	}

	public List<Integer> getAssignedRoleIds() {
		return assignedRoleIds;
	}

	public void setAssignedRoleIds(List<Integer> assignedRoleIds) {
		this.assignedRoleIds = assignedRoleIds;
	}

	public boolean isHasRoles() {
		return CollectionUtils.isNotEmpty(assignedRoleIds);
	}

	public boolean hasRole(int roleId) {
		if (CollectionUtils.isEmpty(assignedRoleIds)) {
			return false;
		}

		return assignedRoleIds.contains(roleId);
	}

	@Override
	public int compareTo(OperatorEmployeeProjectAssignment that) {
		if (this.companyName.equalsIgnoreCase(that.companyName)) {
			return this.employeeName.compareToIgnoreCase(that.employeeName);
		}

		return this.companyName.compareToIgnoreCase(that.companyName);
	}
}
