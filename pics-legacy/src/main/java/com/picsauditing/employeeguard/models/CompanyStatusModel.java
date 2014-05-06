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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CompanyStatusModel that = (CompanyStatusModel) o;

		if (id != that.id) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (status != that.status) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		return result;
	}
}
