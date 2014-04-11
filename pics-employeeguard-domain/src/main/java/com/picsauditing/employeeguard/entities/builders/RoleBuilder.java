package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.lang.ArrayUtils;

import java.util.List;

public class RoleBuilder extends AbstractBaseEntityBuilder<Role, RoleBuilder> {

	public RoleBuilder() {
		entity = new Role();
		that = this;
	}

	public RoleBuilder name(String name) {
		entity.setName(name);
		return this;
	}

	public RoleBuilder description(String description) {
		entity.setDescription(description);
		return this;
	}

	public RoleBuilder skills(int[] skills) {
		if (!ArrayUtils.isEmpty(skills)) {
			entity.getSkills().clear();

			for (int skill : skills) {
				AccountSkill accountSkill = new AccountSkill();
				accountSkill.setId(skill);

				AccountSkillRole accountSkillGroup = new AccountSkillRole(entity, accountSkill);
				entity.getSkills().add(accountSkillGroup);
			}
		}

		return this;
	}

	public RoleBuilder skills(List<AccountSkill> skills) {
		entity.getSkills().clear();

		for (AccountSkill accountSkill : skills) {
			AccountSkillRole accountSkillRole = new AccountSkillRole(entity, accountSkill);
			entity.getSkills().add(accountSkillRole);
		}

		return this;
	}

	public RoleBuilder accountId(int accountId) {
		entity.setAccountId(accountId);
		return this;
	}

	public Role build() {
		return entity;
	}
}
