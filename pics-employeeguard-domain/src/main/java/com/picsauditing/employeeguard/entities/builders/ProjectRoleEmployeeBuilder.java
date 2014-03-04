package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;

public class ProjectRoleEmployeeBuilder extends AbstractBaseEntityBuilder<ProjectRoleEmployee, ProjectRoleEmployeeBuilder> {

	public ProjectRoleEmployeeBuilder() {
		super.entity = new ProjectRoleEmployee();
		super.that = this;
	}

	public ProjectRoleEmployeeBuilder projectRole(ProjectRole projectRole) {
		entity.setProjectRole(projectRole);
		return this;
	}

	public ProjectRoleEmployeeBuilder employee(Employee employee) {
		entity.setEmployee(employee);
		return this;
	}

	@Override
	public ProjectRoleEmployee build() {
		return entity;
	}
}
