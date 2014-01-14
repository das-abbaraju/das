package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountGroupBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

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
		return new RoleBuilder().name(name).description(description).skills(skills).employees(employees).build();
	}

	public Group buildAccountGroup(int id, int accountId) {
		AccountSkillDAO accountSkillDAO = SpringUtils.getBean("AccountSkillDAO");
		EmployeeDAO employeeDAO = SpringUtils.getBean("EmployeeDAO");

		List<AccountSkill> accountSkills = accountSkillDAO.findByIds(Utilities.primitiveArrayToList(skills));
		List<Employee> accountEmployees = employeeDAO.findByIds(Utilities.primitiveArrayToList(employees));

		return new AccountGroupBuilder(id, accountId).name(name).description(description).skills(accountSkills).employees(accountEmployees).build();
	}

	public static class Builder {

		private Group group;

		public Builder accountGroup(Group group) {
			this.group = group;
			return this;
		}

		public OperatorJobRoleForm build() {
			OperatorJobRoleForm form = new OperatorJobRoleForm();
			form.setName(group.getName());
			form.setDescription(group.getDescription());

			int counter = 0;
			if (!CollectionUtils.isEmpty(group.getSkills())) {
				int[] skills = new int[group.getSkills().size()];

				for (AccountSkillGroup accountSkillGroup : group.getSkills()) {
					skills[counter++] = accountSkillGroup.getSkill().getId();
				}

				form.setSkills(skills);
			}

			if (!CollectionUtils.isEmpty(group.getEmployees())) {
				int[] employees = new int[group.getEmployees().size()];
				counter = 0;
				for (GroupEmployee groupEmployee : group.getEmployees()) {
					employees[counter++] = groupEmployee.getEmployee().getId();
				}

				form.setEmployees(employees);
			}

			return form;
		}

	}

}
