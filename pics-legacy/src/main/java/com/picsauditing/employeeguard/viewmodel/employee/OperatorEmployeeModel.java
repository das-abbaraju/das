package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.RoleModel;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorEmployeeSkillModel;

import java.util.List;
import java.util.Map;

public class OperatorEmployeeModel {
	private final int id;
	private final String name;
	private final Map<String, String> companyInfoMap;
	private final SkillStatus overallStatus;
	private final List<ProjectDetailModel> projects;
	private final List<RoleModel> roles;
	private final List<OperatorEmployeeSkillModel> skills;

	public OperatorEmployeeModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.companyInfoMap = builder.companyInfoMap;
		this.overallStatus = builder.overallStatus;
		this.projects = builder.projects;
		this.roles = builder.roles;
		this.skills = builder.skills;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getCompanyInfoMap() {
		return companyInfoMap;
	}

	public SkillStatus getOverallStatus() {
		return overallStatus;
	}

	public List<ProjectDetailModel> getProjects() {
		return projects;
	}

	public List<RoleModel> getRoles() {
		return roles;
	}

	public List<OperatorEmployeeSkillModel> getSkills() {
		return skills;
	}

	public static class Builder {
		private int id;
		private String name;
		private Map<String, String> companyInfoMap;
		private SkillStatus overallStatus;
		private List<ProjectDetailModel> projects;
		private List<RoleModel> roles;
		private List<OperatorEmployeeSkillModel> skills;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder companyInfoMap(Map<String, String> companyInfoMap) {
			this.companyInfoMap = companyInfoMap;
			return this;
		}

		public Builder overallStatus(SkillStatus overallStatus) {
			this.overallStatus = overallStatus;
			return this;
		}

		public Builder projects(List<ProjectDetailModel> projects) {
			this.projects = projects;
			return this;
		}

		public Builder roles(List<RoleModel> roles) {
			this.roles = roles;
			return this;
		}

		public Builder skills(List<OperatorEmployeeSkillModel> skills) {
			this.skills = skills;
			return this;
		}

		public OperatorEmployeeModel build() {
			return new OperatorEmployeeModel(this);
		}
	}
}
