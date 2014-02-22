package com.picsauditing.employeeguard.models;

import java.util.List;

public class CompanyEmployeeModel implements Identifiable {

	private int id;
	private String firstName;
	private String lastName;
	private String title;
	private List<CompanyModel> companies;
	private List<ProjectModel> projects;
	private List<RoleModel> roles;

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

	public List<CompanyModel> getCompanies() {
		return companies;
	}

	public void setCompanies(List<CompanyModel> companies) {
		this.companies = companies;
	}

	public List<ProjectModel> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectModel> projects) {
		this.projects = projects;
	}

	public List<RoleModel> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleModel> roles) {
		this.roles = roles;
	}
}
