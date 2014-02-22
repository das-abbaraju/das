package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectModel;

import java.util.Collection;
import java.util.Map;

public class ProjectDetailModel extends ProjectModel {
	private final Collection<Integer> skills;
	private final Collection<Integer> roles;
	private final SkillStatus status;

	public ProjectDetailModel(Builder builder) {
		super(builder.id, builder.name);
		this.skills = Utilities.unmodifiableCollection(builder.skills);
		this.roles = Utilities.unmodifiableCollection(builder.roles);
		this.status = builder.status;
	}

	public Collection<Integer> getSkills() {
		return skills;
	}

	public Collection<Integer> getRoles() {
		return roles;
	}

	public SkillStatus getStatus() {
		return status;
	}

	public static class Builder {
		private int id;
		private String name;
		private Collection<Integer> skills;
		private Collection<Integer> roles;
		private SkillStatus status;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder skills(Collection<Integer> skills) {
			this.skills = skills;
			return this;
		}

		public Builder roles(Collection<Integer> roles) {
			this.roles = roles;
			return this;
		}

		public Builder status(SkillStatus status) {
			this.status = status;
			return this;
		}

		public ProjectDetailModel build() {
			return new ProjectDetailModel(this);
		}
	}
}
