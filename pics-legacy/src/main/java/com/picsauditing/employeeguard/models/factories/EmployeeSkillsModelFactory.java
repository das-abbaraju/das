package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.services.status.SkillStatus;

import java.util.Set;

public class EmployeeSkillsModelFactory {

	public EmployeeSkillsModel create(final SkillStatus overallStatus,
									  final Set<CompanyStatusModelFactory.CompanyStatusModel> companyStatusModels) {
		EmployeeSkillsModel employeeSkillsModel = new EmployeeSkillsModel();

		employeeSkillsModel.setStatus(overallStatus);
		employeeSkillsModel.setSites(companyStatusModels);

		return employeeSkillsModel;
	}

	public class EmployeeSkillsModel {

		private SkillStatus status;
		private Set<CompanyStatusModelFactory.CompanyStatusModel> sites;

		public SkillStatus getStatus() {
			return status;
		}

		public void setStatus(SkillStatus status) {
			this.status = status;
		}

		public Set<CompanyStatusModelFactory.CompanyStatusModel> getSites() {
			return sites;
		}

		public void setSites(Set<CompanyStatusModelFactory.CompanyStatusModel> sites) {
			this.sites = sites;
		}
	}
}
