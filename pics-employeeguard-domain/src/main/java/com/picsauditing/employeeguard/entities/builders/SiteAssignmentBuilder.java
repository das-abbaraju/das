package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.SiteAssignment;

public class SiteAssignmentBuilder extends AbstractBaseEntityBuilder<SiteAssignment, SiteAssignmentBuilder> {

	public SiteAssignmentBuilder() {
		entity = new SiteAssignment();
		that = this;
	}

	public SiteAssignmentBuilder siteId(final int siteId) {
		entity.setSiteId(siteId);
		return this;
	}

	public SiteAssignmentBuilder role(final Role role) {
		entity.setRole(role);
		return this;
	}

	public SiteAssignmentBuilder employee(final Employee employee) {
		entity.setEmployee(employee);
		return this;
	}

	@Override
	public SiteAssignment build() {
		return entity;
	}
}
