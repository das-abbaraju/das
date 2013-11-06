package com.picsauditing.employeeguard.forms.operator;

public abstract class ProjectNameLocationForm {
	protected String name;
	protected String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
