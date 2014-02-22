package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class CompanyEmployeeStatusModel extends CompanyEmployeeModel {

	private SkillStatus overallStatus;

	public SkillStatus getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(SkillStatus skillStatus) {
		this.overallStatus = skillStatus;
	}
}
