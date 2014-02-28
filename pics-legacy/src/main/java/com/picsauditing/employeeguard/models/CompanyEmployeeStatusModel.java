package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class CompanyEmployeeStatusModel implements SkillStatusInfo {

	private int id;
	private String firstName;
	private String lastName;
	private String title;
	private String image;
	private SkillStatus status;
	private List<EmploymentInfoModel> companies;
	private List<ProjectStatusModel> projects;
	private List<RoleStatusModel> roles;

	public CompanyEmployeeStatusModel() { }

	public int getId() {
		return id;
	}

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public SkillStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(SkillStatus skillStatus) {
		this.status = skillStatus;
	}

	public List<EmploymentInfoModel> getCompanies() {
		return companies;
	}

	public void setCompanies(List<EmploymentInfoModel> companies) {
		this.companies = companies;
	}

	public List<ProjectStatusModel> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectStatusModel> projects) {
		this.projects = projects;
	}

	public List<RoleStatusModel> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleStatusModel> roles) {
		this.roles = roles;
	}
}
