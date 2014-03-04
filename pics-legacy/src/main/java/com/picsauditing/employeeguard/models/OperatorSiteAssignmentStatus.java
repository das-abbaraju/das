package com.picsauditing.employeeguard.models;

import com.picsauditing.PICS.Utilities;

import java.util.Collections;
import java.util.List;

public class OperatorSiteAssignmentStatus {

	private final int id;
	private final String name;
	private final int employees;
	private final int completed;
	private final int pending;
	private final int expiring;
	private final int expired;
	private List<ProjectModel> projects;

	public OperatorSiteAssignmentStatus(final Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.employees = builder.employees;
		this.completed = builder.completed;
		this.pending = builder.pending;
		this.expiring = builder.expiring;
		this.expired = builder.expired;
		this.projects = Utilities.unmodifiableList(builder.projects);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getEmployees() {
		return employees;
	}

	public int getCompleted() {
		return completed;
	}

	public int getPending() {
		return pending;
	}

	public int getExpiring() {
		return expiring;
	}

	public int getExpired() {
		return expired;
	}

	public List<ProjectModel> getProjects() {
		return projects;
	}

	public static class Builder {
		private int id;
		private String name;
		private int employees;
		private int completed;
		private int pending;
		private int expiring;
		private int expired;
		private List<ProjectModel> projects = Collections.emptyList();

		public Builder id(final int id) {
			this.id = id;
			return this;
		}

		public Builder name(final String name) {
			this.name = name;
			return this;
		}

		public Builder employees(final int employees) {
			this.employees = employees;
			return this;
		}

		public Builder completed(final int completed) {
			this.completed = completed;
			return this;
		}

		public Builder pending(final int pending) {
			this.pending = pending;
			return this;
		}

		public Builder expiring(final int expiring) {
			this.expiring = expiring;
			return this;
		}

		public Builder expired(final int expired) {
			this.expired = expired;
			return this;
		}

		public Builder projects(final List<ProjectModel> projects) {
			this.projects = projects;
			return this;
		}

		public OperatorSiteAssignmentStatus build() {
			return new OperatorSiteAssignmentStatus(this);
		}
	}

}
