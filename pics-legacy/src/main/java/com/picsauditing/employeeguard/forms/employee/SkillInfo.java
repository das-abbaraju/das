package com.picsauditing.employeeguard.forms.employee;

import com.picsauditing.employeeguard.entities.SkillType;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class SkillInfo {

    private int id;
    private String endDate;
    private String name;
    private String description;
    private SkillType skillType;
    private SkillStatus skillStatus;
	private boolean doesNotExpire;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public SkillStatus getSkillStatus() {
        return skillStatus;
    }

    public void setSkillStatus(SkillStatus skillStatus) {
        this.skillStatus = skillStatus;
    }

	public boolean isDoesNotExpire() {
		return doesNotExpire;
	}

	public void setDoesNotExpire(boolean doesNotExpire) {
		this.doesNotExpire = doesNotExpire;
	}
}
