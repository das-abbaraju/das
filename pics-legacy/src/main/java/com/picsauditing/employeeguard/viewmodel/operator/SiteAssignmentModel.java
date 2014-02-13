package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.forms.EntityInfo;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;

import java.util.List;
import java.util.Map;

public class SiteAssignmentModel {
	private final List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels;
	private final Map<RoleInfo, Integer> roleEmployee;
	private final int totalEmployeesAssignedToSite;
	private final List<EntityInfo> skills;

	public SiteAssignmentModel(final Builder builder) {
		this.employeeSiteAssignmentModels = Utilities.unmodifiableList(builder.employeeSiteAssignmentModels);
		this.roleEmployee = Utilities.unmodifiableMap(builder.roleEmployee);
		this.totalEmployeesAssignedToSite = builder.totalEmployeesAssignedToSite;
		this.skills = Utilities.unmodifiableList(builder.skills);
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

	public List<EntityInfo> getSkills() {
		return skills;
	}

	public static class Builder {
		private List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels;
		private Map<RoleInfo, Integer> roleEmployee;
		private int totalEmployeesAssignedToSite;
		private List<EntityInfo> skills;

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

		public Builder skills(List<EntityInfo> skills) {
			this.skills = skills;
			return this;
		}

		public SiteAssignmentModel build() {
			return new SiteAssignmentModel(this);
		}
	}
}
