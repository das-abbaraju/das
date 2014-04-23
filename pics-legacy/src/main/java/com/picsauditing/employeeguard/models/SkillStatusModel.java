package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class SkillStatusModel implements IdNameComposite, SkillStatusInfo {

	private int id;
	private String name;
	private SkillStatus status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SkillStatus getStatus() {
		return status;
	}

	public void setStatus(SkillStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SkillStatusModel that = (SkillStatusModel) o;

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
