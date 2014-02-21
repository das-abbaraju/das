package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectModel;

import java.util.List;
import java.util.Map;

public class ProjectDetailModel extends ProjectModel {
	private final Map<String, String> companyInfoMap;
	private final List<Integer> skills;
	private final List<Integer> roles;
	private final SkillStatus skillStatus;

	public ProjectDetailModel(Builder builder) {
		super(builder.id, builder.name);
		this.companyInfoMap = builder.companyInfoMap;
		this.skills = builder.skills;
		this.roles = builder.roles;
		this.skillStatus = builder.skillStatus;
	}

	public Map<String, String> getCompanyInfoMap() {
		return companyInfoMap;
	}

	public List<Integer> getSkills() {
		return skills;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public SkillStatus getSkillStatus() {
		return skillStatus;
	}

	public static class Builder {
		private int id;
		private String name;
		private Map<String, String> companyInfoMap;
		private List<Integer> skills;
		private List<Integer> roles;
		private SkillStatus skillStatus;

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

		public Builder skills(List<Integer> skills) {
			this.skills = skills;
			return this;
		}

		public Builder roles(List<Integer> roles) {
			this.roles = roles;
			return this;
		}

		public Builder skillStatus(SkillStatus skillStatus) {
			this.skillStatus = skillStatus;
			return this;
		}

		public ProjectDetailModel build() {
			return new ProjectDetailModel(this);
		}
	}
}
