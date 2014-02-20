package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.PICS.Utilities;

import java.util.List;
import java.util.Map;

public class EmployeeSkillsModel {

	private final Map<SkillSection, List<EmployeeSkillModel>> employeeSkills;

	private EmployeeSkillsModel(final Builder builder) {
		this.employeeSkills = Utilities.unmodifiableMap(builder.employeeSkills);
	}

	public Map<SkillSection, List<EmployeeSkillModel>> getEmployeeSkills() {
		return employeeSkills;
	}

	public static class Builder {
		private Map<SkillSection, List<EmployeeSkillModel>> employeeSkills;

		public Builder employeeSkills(final Map<SkillSection, List<EmployeeSkillModel>> employeeSkills) {
			this.employeeSkills = employeeSkills;
			return this;
		}

		public EmployeeSkillsModel build() {
			return new EmployeeSkillsModel(this);
		}
	}

	public class SkillSection {
		private String name;
		private int belongsToProject;
	}
}
