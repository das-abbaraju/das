package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class ProfileSiteModel implements Nameable, SkillStatusInfo {

	private String name;
	private SkillStatus status;
	private int roles;

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

	public int getRoles() {
		return roles;
	}

	public void setRoles(int roles) {
		this.roles = roles;
	}

}
