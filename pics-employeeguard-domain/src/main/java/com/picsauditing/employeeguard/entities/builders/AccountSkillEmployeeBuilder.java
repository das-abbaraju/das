package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.Date;

public class AccountSkillEmployeeBuilder extends AbstractBaseEntityBuilder<AccountSkillEmployee, AccountSkillEmployeeBuilder> {

	public AccountSkillEmployeeBuilder() {
		this.entity = new AccountSkillEmployee();
		that = this;
	}

	public AccountSkillEmployeeBuilder id(int id) {
		entity.setId(id);
		return this;
	}

	public AccountSkillEmployeeBuilder accountSkill(AccountSkill accountSkill) {
		entity.setSkill(accountSkill);
		return this;
	}

	public AccountSkillEmployeeBuilder employee(Employee employee) {
		entity.setEmployee(employee);
		return this;
	}

	public AccountSkillEmployeeBuilder profileDocument(ProfileDocument profileDocument) {
		entity.setProfileDocument(profileDocument);
		return this;
	}

	public AccountSkillEmployeeBuilder startDate(Date startDate) {
		entity.setStartDate(startDate);
		return this;
	}

	public AccountSkillEmployeeBuilder endDate(Date endDate) {
		entity.setEndDate(endDate);
		return this;
	}

	public AccountSkillEmployee build() {
		return entity;
	}
}
