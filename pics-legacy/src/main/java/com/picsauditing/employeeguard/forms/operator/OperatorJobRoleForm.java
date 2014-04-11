package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;

public class OperatorJobRoleForm extends GroupNameSkillsForm implements AddAnotherForm {

	private String description;

	private int[] employees;

	private boolean addAnother;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int[] getEmployees() {
		return employees;
	}

	public void setEmployees(int[] employees) {
		this.employees = employees;
	}

	@Override
	public boolean isAddAnother() {
		return this.addAnother;
	}

	@Override
	public void setAddAnother(boolean addAnother) {
		this.addAnother = addAnother;
	}

	public Role buildAccountGroup() {
		return new RoleBuilder().name(name).description(description).skills(skills).build();
	}

}
