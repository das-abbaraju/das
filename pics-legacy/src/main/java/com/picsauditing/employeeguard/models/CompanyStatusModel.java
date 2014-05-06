package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

// This could represent either a Contractor the Employee works for or a Site
public class CompanyStatusModel implements IdNameComposite, SkillStatusInfo {

	private int id;
	private String name;
	private SkillStatus status;
	private RequiredSkills required;
	private List<CompanyProjectModel> projects;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SkillStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(SkillStatus status) {
		this.status = status;
	}

	public RequiredSkills getRequired() {
		return required;
	}

	public void setRequired(RequiredSkills required) {
		this.required = required;
	}

	public List<CompanyProjectModel> getProjects() {
		return projects;
	}

	public void setProjects(List<CompanyProjectModel> projects) {
		this.projects = projects;
	}
}
