package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;

public class AccountGroupBuilder {

	private AccountGroup accountGroup;

	public AccountGroupBuilder() {
		accountGroup = new AccountGroup();
	}

	public AccountGroupBuilder(int id, int accountId) {
		accountGroup = new AccountGroup(id, accountId);
	}

	public AccountGroupBuilder name(String name) {
		accountGroup.setName(name);
		return this;
	}

	public AccountGroupBuilder description(String description) {
		accountGroup.setDescription(description);
		return this;
	}

	public AccountGroupBuilder skills(int[] skills) {
		if (!ArrayUtils.isEmpty(skills)) {
			accountGroup.getSkills().clear();

			for (int skill : skills) {
				AccountSkill accountSkill = new AccountSkill();
				accountSkill.setId(skill);

				AccountSkillGroup accountSkillGroup = new AccountSkillGroup(accountGroup, accountSkill);
				accountGroup.getSkills().add(accountSkillGroup);
			}
		}

		return this;
	}

	public AccountGroupBuilder employees(int[] employees) {
		if (!ArrayUtils.isEmpty(employees)) {
			accountGroup.getEmployees().clear();

			for (int employeeId : employees) {
				Employee employee = new Employee();
				employee.setId(employeeId);

				AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee(employee, accountGroup);
				accountGroup.getEmployees().add(accountGroupEmployee);
			}
		}

		return this;
	}

	public AccountGroupBuilder skills(List<AccountSkill> skills) {
		accountGroup.getSkills().clear();

		for (AccountSkill accountSkill : skills) {
			AccountSkillGroup accountSkillGroup = new AccountSkillGroup(accountGroup, accountSkill);
			accountGroup.getSkills().add(accountSkillGroup);
		}

		return this;
	}

	public AccountGroupBuilder employees(List<Employee> employees) {
		accountGroup.getEmployees().clear();

		for (Employee employee : employees) {
			AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee(employee, accountGroup);
			accountGroup.getEmployees().add(accountGroupEmployee);
		}

		return this;
	}

	public AccountGroup build() {
		return accountGroup;
	}
}
