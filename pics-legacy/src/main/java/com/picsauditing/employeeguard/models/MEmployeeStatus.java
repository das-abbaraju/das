package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Set;

public class MEmployeeStatus {
	@Expose
	@SerializedName("status")
	private String overallStatus;

	@Expose
	@SerializedName("skills")
	private Set<MEmployeeSkillStatus> employeeSkillStatus;

	public String getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(String overallStatus) {
		this.overallStatus = overallStatus;
	}

	public Set<MEmployeeSkillStatus> getEmployeeSkillStatus() {
		return employeeSkillStatus;
	}

	public void addToEmployeeSkillStatus(MEmployeeSkillStatus mEmployeeSkillStatus){
		this.employeeSkillStatus.add(mEmployeeSkillStatus);
	}

	public void setEmployeeSkillStatus(Set<MEmployeeSkillStatus> employeeSkillStatus) {
		this.employeeSkillStatus = employeeSkillStatus;
	}
}
