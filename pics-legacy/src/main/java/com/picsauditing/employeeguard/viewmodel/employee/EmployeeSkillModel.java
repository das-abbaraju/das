package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class EmployeeSkillModel implements Comparable<EmployeeSkillModel> {
	private final String skillName;
	private final SkillStatus skillStatus;
	private final int belongsToProject;
	private final int belongsToRole;

	private EmployeeSkillModel(final Builder builder) {
		this.skillName = builder.skillName;
		this.skillStatus = builder.skillStatus;
		this.belongsToProject = builder.belongsToProject;
		this.belongsToRole = builder.belongsToRole;
	}

	public String getSkillName() {
		return skillName;
	}

	public SkillStatus getSkillStatus() {
		return skillStatus;
	}

	public int getBelongsToProject() {
		return belongsToProject;
	}

	public int getBelongsToRole() {
		return belongsToRole;
	}

	@Override
	public int compareTo(EmployeeSkillModel that) {
		if (this.skillStatus == that.skillStatus) {
			return this.skillName.compareToIgnoreCase(that.skillName);
		}

		// Skill statuses are listed in reverse order
		return (that.skillStatus.compareTo(this.skillStatus));
	}

	public static class Builder {
		private String skillName;
		private SkillStatus skillStatus;
		private int belongsToProject;
		private int belongsToRole;

		public Builder skillName(String skillName) {
			this.skillName = skillName;
			return this;
		}

		public Builder skillStatus(SkillStatus skillStatus) {
			this.skillStatus = skillStatus;
			return this;
		}

		public Builder belongsToProject(int belongsToProject) {
			this.belongsToProject = belongsToProject;
			return this;
		}

		public Builder belongsToRole(int belongsToRole) {
			this.belongsToRole = belongsToRole;
			return this;
		}

		public EmployeeSkillModel build() {
			return new EmployeeSkillModel(this);
		}
	}
}
