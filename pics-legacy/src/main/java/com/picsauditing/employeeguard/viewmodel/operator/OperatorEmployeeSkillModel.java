package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Collection;

public class OperatorEmployeeSkillModel {
	private final int id;
	private final String name;
	private final Collection<Integer> projects;
	private final Collection<Integer> roles;
	private final SkillStatus status;

	public OperatorEmployeeSkillModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.projects = Utilities.unmodifiableCollection(builder.projects);
		this.roles = Utilities.unmodifiableCollection(builder.roles);
		this.status = builder.status;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Collection<Integer> getProjects() {
		return projects;
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
		private Collection<Integer> projects;
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

		public Builder projects(Collection<Integer> projects) {
			this.projects = projects;
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

		public OperatorEmployeeSkillModel build() {
			return new OperatorEmployeeSkillModel(this);
		}
	}
}
