package com.picsauditing.employeeguard.viewmodel;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Collection;

public class RoleModel {
	private final int id;
	private final Collection<Integer> skills;
	private final String name;
	private final SkillStatus status;

	public RoleModel(Builder builder) {
		this.id = builder.id;
		this.skills = Utilities.unmodifiableCollection(builder.skills);
		this.name = builder.name;
		this.status = builder.status;
	}

	public int getId() {
		return id;
	}

	public Collection<Integer> getSkills() {
		return skills;
	}

	public String getName() {
		return name;
	}

	public SkillStatus getStatus() {
		return status;
	}

	public static class Builder {
		private int id;
		private Collection<Integer> skills;
		private String name;
		private SkillStatus status;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder skills(Collection<Integer> skills) {
			this.skills = skills;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder status(SkillStatus status) {
			this.status = status;
			return this;
		}

		public RoleModel build() {
			return new RoleModel(this);
		}
	}
}
