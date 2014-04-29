package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.List;

public class ProfileBuilder extends AbstractBaseEntityBuilder<Profile, ProfileBuilder> {

	public ProfileBuilder() {
		this.entity = new Profile();
		that = this;
	}

	public ProfileBuilder id(final int id) {
		entity.setId(id);
		return this;
	}

	public ProfileBuilder employees(List<Employee> employees) {
    entity.getEmployees().clear();
    entity.getEmployees().addAll(employees);
		return this;
	}

	public ProfileBuilder documents(final List<ProfileDocument> documents) {
    entity.getDocuments().clear();
    entity.getDocuments().addAll(documents);
		return this;
	}

	public Profile build() {
		return entity;
	}
}
