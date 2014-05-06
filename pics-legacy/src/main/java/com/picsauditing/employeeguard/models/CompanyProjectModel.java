package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class CompanyProjectModel implements IdNameComposite, SkillStatusInfo {

	private int id;
	private String name;
	private SkillStatus status;
	private List<SkillStatusModel> skills;

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

	public List<SkillStatusModel> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillStatusModel> skills) {
		this.skills = skills;
	}
}
