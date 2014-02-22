package com.picsauditing.employeeguard.models;

import java.util.List;

public class RoleModel extends AbstractJSONable implements Identifiable, Nameable {

	private int id;
	private String name;
	private List<SkillModel> skills;

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

	public List<SkillModel> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillModel> skills) {
		this.skills = skills;
	}
}
