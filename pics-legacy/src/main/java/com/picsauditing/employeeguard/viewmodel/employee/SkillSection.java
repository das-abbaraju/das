package com.picsauditing.employeeguard.viewmodel.employee;

public class SkillSection implements Comparable<SkillSection> {
	private final String name;
	private final int belongsToProject;
	private final int belongsToRole;

	private SkillSection(final Builder builder) {
		this.name = builder.name;
		this.belongsToProject = builder.belongsToProject;
		this.belongsToRole = builder.belongsToRole;
	}

	public String getName() {
		return name;
	}

	public int getBelongsToProject() {
		return belongsToProject;
	}

	public int getBelongsToRole() {
		return belongsToRole;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SkillSection that = (SkillSection) o;

		if (belongsToProject != that.belongsToProject) return false;
		if (belongsToRole != that.belongsToRole) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + belongsToProject;
		result = 31 * result + belongsToRole;
		return result;
	}

	@Override
	public int compareTo(SkillSection that) {
		return this.name.compareToIgnoreCase(that.name);
	}

	public static class Builder {
		private String name;
		private int belongsToProject;
		private int belongsToRole;

		public Builder name(String name) {
			this.name = name;
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

		public SkillSection build() {
			return new SkillSection(this);
		}
	}
}