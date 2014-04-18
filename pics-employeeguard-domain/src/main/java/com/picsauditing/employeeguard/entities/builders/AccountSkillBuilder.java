package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;
import org.apache.commons.lang.ArrayUtils;

public class AccountSkillBuilder {

	private AccountSkill accountSkill;

	public AccountSkillBuilder() {
		this.accountSkill = new AccountSkill();
	}

	public AccountSkillBuilder(int id, int accountId) {
		accountSkill = new AccountSkill(id, accountId);
	}

	public AccountSkillBuilder id(int id) {
		accountSkill.setId(id);
		return this;
	}

	public AccountSkillBuilder accountId(int accountId) {
		accountSkill.setAccountId(accountId);
		return this;
	}

	public AccountSkillBuilder name(String name) {
		accountSkill.setName(name);
		return this;
	}

	public AccountSkillBuilder description(String description) {
		accountSkill.setDescription(description);
		return this;
	}

	public AccountSkillBuilder skillType(SkillType skillType) {
		accountSkill.setSkillType(skillType);
		return this;
	}

	public AccountSkillBuilder required(boolean required) {
		accountSkill.setRuleType(required ? RuleType.REQUIRED : RuleType.OPTIONAL);
		return this;
	}

	public AccountSkillBuilder intervalType(IntervalType intervalType) {
		accountSkill.setIntervalType(intervalType);
		return this;
	}

	public AccountSkillBuilder intervalPeriod(int intervalPeriod) {
		accountSkill.setIntervalPeriod(intervalPeriod);
		return this;
	}

	public AccountSkillBuilder doesNotExpire(boolean doesNotExpire) {
		if (doesNotExpire) {
			accountSkill.setIntervalType(IntervalType.NO_EXPIRATION);
		}

		return this;
	}

	public AccountSkillBuilder groups(String[] groups) {
		if (!ArrayUtils.isEmpty(groups)) {
			accountSkill.getGroups().clear();

			for (String group : groups) {
				Group accountGroup = new Group();
				accountGroup.setName(group);
				accountGroup.setAccountId(accountSkill.getAccountId());
				AccountSkillGroup accountSkillGroup = new AccountSkillGroup(accountGroup, accountSkill);

				accountSkill.getGroups().add(accountSkillGroup);
			}
		}

		return this;
	}

	public AccountSkillBuilder roles(String[] roles) {
		if (!ArrayUtils.isEmpty(roles)) {
			accountSkill.getRoles().clear();

			for (String role : roles) {
				Role accountRole = new Role();
				accountRole.setName(role);
				accountRole.setAccountId(accountSkill.getAccountId());
				AccountSkillRole accountSkillRole = new AccountSkillRole(accountRole, accountSkill);

				accountSkill.getRoles().add(accountSkillRole);
			}
		}

		return this;
	}

	public AccountSkill build() {
		return accountSkill;
	}

}
