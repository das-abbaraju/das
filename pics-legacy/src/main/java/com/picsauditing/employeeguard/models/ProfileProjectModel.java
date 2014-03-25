package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class ProfileProjectModel implements Nameable, SkillStatusInfo {

	private String name;
	private SkillStatus status;
	private String site;

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

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

}
