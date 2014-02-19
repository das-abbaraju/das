package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.PICS.Utilities;

import java.util.List;

public class EmployeeModel {

	private final int id;
	private final String name;
	private final String title;
	private final List<String> companyNames;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getCompanyNames() {
		return companyNames;
	}

	private EmployeeModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.title = builder.title;
		this.companyNames = Utilities.unmodifiableList(builder.companyNames);
	}


	public static class Builder {
		private int id;
		private String name;
		private String title;
		private List<String> companyNames;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder companyNames(List<String> companyNames) {
			this.companyNames = companyNames;
			return this;
		}

		public EmployeeModel build() {
			return new EmployeeModel(this);
		}
	}
}
