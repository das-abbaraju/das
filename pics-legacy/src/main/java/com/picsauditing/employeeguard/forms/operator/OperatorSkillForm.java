package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;

public class OperatorSkillForm implements AddAnotherForm {

	private String name;
	private String description;
	private SkillType skillType;
	boolean required;
	// Defaults?
	private IntervalType intervalType = IntervalType.YEAR;
	private int intervalPeriod = 1;
	boolean doesNotExpire;
	boolean addAnother;

	/* Collections */
	private String[] groups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SkillType getSkillType() {
		return skillType;
	}

	public void setSkillType(SkillType skillType) {
		this.skillType = skillType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public IntervalType getIntervalType() {
		return intervalType;
	}

	public void setIntervalType(IntervalType intervalType) {
		this.intervalType = intervalType;
	}

	public int getIntervalPeriod() {
		return intervalPeriod;
	}

	public void setIntervalPeriod(int intervalPeriod) {
		this.intervalPeriod = intervalPeriod;
	}

	public boolean isDoesNotExpire() {
		return doesNotExpire;
	}

	public void setDoesNotExpire(boolean doesNotExpire) {
		this.doesNotExpire = doesNotExpire;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	@Override
	public boolean isAddAnother() {
		return addAnother;
	}

	@Override
	public void setAddAnother(boolean addAnother) {
		this.addAnother = addAnother;
	}

	public AccountSkill buildAccountSkill() {
		return new AccountSkillBuilder().name(name).description(description).skillType(skillType).required(required)
				.intervalType(intervalType).intervalPeriod(intervalPeriod).groups(groups).doesNotExpire(doesNotExpire).build();
	}

	public AccountSkill buildAccountSkill(int id, int accountId) {
		return new AccountSkillBuilder(id, accountId).name(name).description(description).skillType(skillType).required(required)
				.intervalType(intervalType).intervalPeriod(intervalPeriod).groups(groups).doesNotExpire(doesNotExpire).build();
	}

	public static class Builder {

		private AccountSkill accountSkill;

		public Builder accountSkill(AccountSkill accountSkill) {
			this.accountSkill = accountSkill;
			return this;
		}

		public OperatorSkillForm build() {
			OperatorSkillForm form = new OperatorSkillForm();
			form.setName(accountSkill.getName());
			form.setDescription(accountSkill.getDescription());
			form.setSkillType(accountSkill.getSkillType());
			form.setRequired(accountSkill.getRuleType() == RuleType.REQUIRED);
			form.setIntervalType(accountSkill.getIntervalType());
			form.setIntervalPeriod(accountSkill.getIntervalPeriod());
			form.setDoesNotExpire(!accountSkill.getIntervalType().isApplicableExpiration() && accountSkill.getIntervalPeriod() == 0);

			String[] groups = new String[accountSkill.getGroups().size()];
			int counter = 0;
			for (AccountSkillGroup accountSkillGroup : accountSkill.getGroups()) {
				groups[counter++] = accountSkillGroup.getGroup().getName();
			}

			form.setGroups(groups);

			return form;
		}

	}
}
