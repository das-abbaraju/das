package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.entities.builders.AccountGroupBuilder;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class GroupForm extends GroupNameSkillsForm implements AddAnotherForm {

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

	public AccountGroup buildAccountGroup() {
		return new AccountGroupBuilder().name(name).description(description).skills(skills).employees(employees).build();
	}

	public AccountGroup buildAccountGroup(int id, int accountId) {
		AccountSkillDAO accountSkillDAO = SpringUtils.getBean("AccountSkillDAO");
		EmployeeDAO employeeDAO = SpringUtils.getBean("EmployeeDAO");

		List<AccountSkill> accountSkills = accountSkillDAO.findByIds(Utilities.primitiveArrayToList(skills));
		List<Employee> accountEmployees = employeeDAO.findByIds(Utilities.primitiveArrayToList(employees));

		return new AccountGroupBuilder(id, accountId).name(name).description(description).skills(accountSkills).employees(accountEmployees).build();
	}

	public static class Builder {

		private AccountGroup accountGroup;

		public Builder accountGroup(AccountGroup accountGroup) {
			this.accountGroup = accountGroup;
			return this;
		}

		public GroupForm build() {
			GroupForm form = new GroupForm();
			form.setName(accountGroup.getName());
			form.setDescription(accountGroup.getDescription());

			int counter = 0;
			if (!CollectionUtils.isEmpty(accountGroup.getSkills())) {
				int[] skills = new int[accountGroup.getSkills().size()];

				for (AccountSkillGroup accountSkillGroup : accountGroup.getSkills()) {
					skills[counter++] = accountSkillGroup.getSkill().getId();
				}

				form.setSkills(skills);
			}

			if (!CollectionUtils.isEmpty(accountGroup.getEmployees())) {
				int[] employees = new int[accountGroup.getEmployees().size()];
				counter = 0;
				for (AccountGroupEmployee accountGroupEmployee : accountGroup.getEmployees()) {
					employees[counter++] = accountGroupEmployee.getEmployee().getId();
				}

				form.setEmployees(employees);
			}

			return form;
		}

	}

}
