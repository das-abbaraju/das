package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class EmployeeSkillsModel {

	private SkillStatus status;
	private List<CompanyStatusModel> sites;

	public SkillStatus getStatus() {
		return status;
	}

	public void setStatus(SkillStatus status) {
		this.status = status;
	}

	public List<CompanyStatusModel> getSites() {
		return sites;
	}

	public void setSites(List<CompanyStatusModel> sites) {
		this.sites = sites;
	}
}
