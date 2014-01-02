package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;

public class AccountGroupBuilder {

	private Group group;

	public AccountGroupBuilder() {
		group = new Group();
	}

	public AccountGroupBuilder(int id, int accountId) {
		group = new Group(id, accountId);
	}

	public AccountGroupBuilder name(String name) {
		group.setName(name);
		return this;
	}

	public AccountGroupBuilder description(String description) {
		group.setDescription(description);
		return this;
	}

	public AccountGroupBuilder skills(int[] skills) {
		if (!ArrayUtils.isEmpty(skills)) {
			group.getSkills().clear();

			for (int skill : skills) {
				AccountSkill accountSkill = new AccountSkill();
				accountSkill.setId(skill);

				AccountSkillGroup accountSkillGroup = new AccountSkillGroup(group, accountSkill);
				group.getSkills().add(accountSkillGroup);
			}
		}

		return this;
	}

	public AccountGroupBuilder employees(int[] employees) {
		if (!ArrayUtils.isEmpty(employees)) {
			group.getEmployees().clear();

			for (int employeeId : employees) {
				Employee employee = new Employee();
				employee.setId(employeeId);

				AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee(employee, group);
				group.getEmployees().add(accountGroupEmployee);
			}
		}

		return this;
	}

	public AccountGroupBuilder skills(List<AccountSkill> skills) {
		group.getSkills().clear();

		for (AccountSkill accountSkill : skills) {
			AccountSkillGroup accountSkillGroup = new AccountSkillGroup(group, accountSkill);
			group.getSkills().add(accountSkillGroup);
		}

		return this;
	}

	public AccountGroupBuilder employees(List<Employee> employees) {
		group.getEmployees().clear();

		for (Employee employee : employees) {
			AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee(employee, group);
			group.getEmployees().add(accountGroupEmployee);
		}

		return this;
	}

	public Group build() {
		return group;
	}
}
