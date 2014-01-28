package com.picsauditing.employeeguard.viewmodel.contractor;

import java.util.List;
import java.util.Map;

public class SiteAssignmentModel {
	private final List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels;
	private final Map<String, Integer> roleEmployee;
	private final int totalEmployeesAssignedToSite;

	public SiteAssignmentModel(final Builder builder) {
		this.employeeSiteAssignmentModels = builder.employeeSiteAssignmentModels;
		this.roleEmployee = builder.roleEmployee;
		this.totalEmployeesAssignedToSite = builder.totalEmployeesAssignedToSite;
	}

	public List<EmployeeSiteAssignmentModel> getEmployeeSiteAssignmentModels() {
		return employeeSiteAssignmentModels;
	}

	public Map<String, Integer> getRoleEmployee() {
		return roleEmployee;
	}

	public int getTotalEmployeesAssignedToSite() {
		return totalEmployeesAssignedToSite;
	}

	public static class Builder {
		private List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels;
		private Map<String, Integer> roleEmployee;
		private int totalEmployeesAssignedToSite;

		public Builder employeeSiteAssignmentModels(List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels) {
			this.employeeSiteAssignmentModels = employeeSiteAssignmentModels;
			return this;
		}

		public Builder roleEmployeeCount(Map<String, Integer> employeeRoleCount) {
			this.roleEmployee = employeeRoleCount;
			return this;
		}

		public Builder totalEmployeesAssignedToSite(int totalEmployeesAssignedToSite) {
			this.totalEmployeesAssignedToSite = totalEmployeesAssignedToSite;
			return this;
		}

		public SiteAssignmentModel build() {
			return new SiteAssignmentModel(this);
		}
	}
}
