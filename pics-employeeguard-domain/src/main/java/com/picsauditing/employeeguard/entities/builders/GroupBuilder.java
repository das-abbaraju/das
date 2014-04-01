package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;

public class GroupBuilder {

	private Group group;

	public GroupBuilder() {
		group = new Group();
	}

	public GroupBuilder id(int id) {
		group.setId(id);
		return this;
	}

	public GroupBuilder accountId(int accountId) {
		group.setAccountId(accountId);
		return this;
	}

    @Deprecated
	public GroupBuilder(int id, int accountId) {
		group = new Group(id, accountId);
	}

	public GroupBuilder name(String name) {
		group.setName(name);
		return this;
	}

	public GroupBuilder description(String description) {
		group.setDescription(description);
		return this;
	}

	public GroupBuilder skills(int[] skills) {
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

	public GroupBuilder employees(int[] employees) {
		if (!ArrayUtils.isEmpty(employees)) {
			group.getEmployees().clear();

			for (int employeeId : employees) {
				Employee employee = new Employee();
				employee.setId(employeeId);

				GroupEmployee groupEmployee = new GroupEmployee(employee, group);
				group.getEmployees().add(groupEmployee);
			}
		}

		return this;
	}

	public GroupBuilder skills(List<AccountSkill> skills) {
		group.getSkills().clear();

		for (AccountSkill accountSkill : skills) {
			AccountSkillGroup accountSkillGroup = new AccountSkillGroup(group, accountSkill);
			group.getSkills().add(accountSkillGroup);
		}

		return this;
	}

	public GroupBuilder employees(List<Employee> employees) {
		group.getEmployees().clear();

		for (Employee employee : employees) {
			GroupEmployee groupEmployee = new GroupEmployee(employee, group);
			group.getEmployees().add(groupEmployee);
		}

		return this;
	}

	public Group build() {
		return group;
	}
}
