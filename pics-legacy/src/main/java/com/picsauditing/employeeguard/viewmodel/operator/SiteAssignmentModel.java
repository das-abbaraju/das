package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;

import java.util.List;
import java.util.Map;

public class SiteAssignmentModel {
	private final List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels;
	private final Map<RoleInfo, Integer> roleEmployee;
	private final int totalEmployeesAssignedToSite;

	public SiteAssignmentModel(final Builder builder) {
		this.employeeSiteAssignmentModels = builder.employeeSiteAssignmentModels;
		this.roleEmployee = builder.roleEmployee;
		this.totalEmployeesAssignedToSite = builder.totalEmployeesAssignedToSite;
	}

	public List<EmployeeSiteAssignmentModel> getEmployeeSiteAssignmentModels() {
		return employeeSiteAssignmentModels;
	}

	public Map<RoleInfo, Integer> getRoleEmployee() {
		return roleEmployee;
	}

	public int getTotalEmployeesAssignedToSite() {
		return totalEmployeesAssignedToSite;
	}

	public static class Builder {
		private List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels;
		private Map<RoleInfo, Integer> roleEmployee;
		private int totalEmployeesAssignedToSite;

		public Builder employeeSiteAssignmentModels(List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels) {
			this.employeeSiteAssignmentModels = employeeSiteAssignmentModels;
			return this;
		}

		public Builder roleEmployeeCount(Map<RoleInfo, Integer> employeeRoleCount) {
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
