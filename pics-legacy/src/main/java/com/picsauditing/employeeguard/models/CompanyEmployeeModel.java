package com.picsauditing.employeeguard.models;

import java.util.List;

public class CompanyEmployeeModel implements Identifiable {

	private int id;
	private String firstName;
	private String lastName;
	private String title;
	private List<EmploymentInfoModel> companies;
	private List<? extends ProjectModel> projects;
	private List<? extends RoleModel> roles;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<EmploymentInfoModel> getCompanies() {
		return companies;
	}

	public void setCompanies(List<EmploymentInfoModel> companies) {
		this.companies = companies;
	}

	public List<? extends ProjectModel> getProjects() {
		return projects;
	}

	public void setProjects(List<? extends ProjectModel> projects) {
		this.projects = projects;
	}

	public List<? extends RoleModel> getRoles() {
		return roles;
	}

	public void setRoles(List<? extends RoleModel> roles) {
		this.roles = roles;
	}
}
