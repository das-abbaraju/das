package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class CompanyEmployeeStatusModel extends CompanyEmployeeModel implements SkillStatusInfo {

	private SkillStatus status;

	public CompanyEmployeeStatusModel() { }

	public CompanyEmployeeStatusModel(final CompanyEmployeeModel companyEmployeeModel) {
		this.setCompanies(companyEmployeeModel.getCompanies());
		this.setFirstName(companyEmployeeModel.getFirstName());
		this.setLastName(companyEmployeeModel.getLastName());
		this.setId(companyEmployeeModel.getId());
		this.setRoles(companyEmployeeModel.getRoles());
		this.setProjects(companyEmployeeModel.getProjects());
		this.setTitle(companyEmployeeModel.getTitle());
	}

	@Override
	public SkillStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(SkillStatus skillStatus) {
		this.status = skillStatus;
	}
}
