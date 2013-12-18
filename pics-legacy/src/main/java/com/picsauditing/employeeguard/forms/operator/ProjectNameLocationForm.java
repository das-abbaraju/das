package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;

public abstract class ProjectNameLocationForm implements DuplicateInfoProvider {
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
