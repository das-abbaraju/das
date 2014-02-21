package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class OperatorEmployeeSkillModel {
	private final int id;
	private final String name;
	private final List<Integer> projects;
	private final List<Integer> roles;
	private final SkillStatus status;

	public OperatorEmployeeSkillModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.projects = builder.projects;
		this.roles = builder.roles;
		this.status = builder.status;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getProjects() {
		return projects;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public SkillStatus getStatus() {
		return status;
	}
	
	public static class Builder {
		private int id;
		private String name;
		private List<Integer> projects;
		private List<Integer> roles;
		private SkillStatus status;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder projects(List<Integer> projects) {
			this.projects = projects;
			return this;
		}

		public Builder roles(List<Integer> roles) {
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
