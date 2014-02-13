package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.lang.ArrayUtils;

import java.util.Date;
import java.util.List;

public class RoleBuilder {

	private Role role;

	public RoleBuilder() {
		role = new Role();
	}

	public RoleBuilder(int id, int accountId) {
		role = new Role(id, accountId);
	}

	public RoleBuilder name(String name) {
		role.setName(name);
		return this;
	}

	public RoleBuilder description(String description) {
		role.setDescription(description);
		return this;
	}

	public RoleBuilder skills(int[] skills) {
		if (!ArrayUtils.isEmpty(skills)) {
			role.getSkills().clear();

			for (int skill : skills) {
				AccountSkill accountSkill = new AccountSkill();
				accountSkill.setId(skill);

				AccountSkillRole accountSkillGroup = new AccountSkillRole(role, accountSkill);
				role.getSkills().add(accountSkillGroup);
			}
		}

		return this;
	}

	public RoleBuilder employees(int[] employees) {
		if (!ArrayUtils.isEmpty(employees)) {
			role.getEmployees().clear();

			for (int employeeId : employees) {
				Employee employee = new Employee();
				employee.setId(employeeId);

				RoleEmployee groupEmployee = new RoleEmployee(employee, role);
				role.getEmployees().add(groupEmployee);
			}
		}

		return this;
	}

	public RoleBuilder skills(List<AccountSkill> skills) {
		role.getSkills().clear();

		for (AccountSkill accountSkill : skills) {
			AccountSkillRole accountSkillRole = new AccountSkillRole(role, accountSkill);
			role.getSkills().add(accountSkillRole);
		}

		return this;
	}

	public RoleBuilder employees(List<Employee> employees) {
		role.getEmployees().clear();

		for (Employee employee : employees) {
			RoleEmployee groupEmployee = new RoleEmployee(employee, role);
			role.getEmployees().add(groupEmployee);
		}

		return this;
	}

	public RoleBuilder accountId(int accountId) {
		role.setAccountId(accountId);
		return this;
	}

	public RoleBuilder createdBy(int createdBy) {
		role.setCreatedBy(createdBy);
		return this;
	}

	public RoleBuilder createdDate(Date createdDate) {
		role.setCreatedDate(createdDate);
		return this;
	}

	public Role build() {
		return role;
	}
}
