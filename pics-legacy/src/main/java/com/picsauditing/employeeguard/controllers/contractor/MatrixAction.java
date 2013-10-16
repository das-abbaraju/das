package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;

@SuppressWarnings("serial")
public class MatrixAction extends PicsRestActionSupport {

	/* pages */

	public String rolesToEmployees() {
		return "roles-to-employees";
	}

	public String employeeToSkills() {
		return "employee-to-skills";
	}

	public String employeesToSkills() {
		return "employees-to-skills";
	}

	public String skillsToRoles() {
		return "skills-to-roles";
	}

	/* other methods */

	/* getters + setters */

}
