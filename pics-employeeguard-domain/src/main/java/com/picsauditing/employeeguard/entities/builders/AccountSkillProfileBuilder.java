package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.Date;

public class AccountSkillProfileBuilder extends AbstractBaseEntityBuilder<AccountSkillProfile, AccountSkillProfileBuilder> {

	public AccountSkillProfileBuilder() {
		this.entity = new AccountSkillProfile();
		that = this;
	}

	public AccountSkillProfileBuilder id(int id) {
		entity.setId(id);
		return this;
	}

	public AccountSkillProfileBuilder accountSkill(AccountSkill accountSkill) {
		entity.setSkill(accountSkill);
		return this;
	}

	public AccountSkillProfileBuilder profile(Profile profile) {
		entity.setProfile(profile);
		return this;
	}

	public AccountSkillProfileBuilder profileDocument(ProfileDocument profileDocument) {
		entity.setProfileDocument(profileDocument);
		return this;
	}

	public AccountSkillProfileBuilder startDate(Date startDate) {
		entity.setStartDate(startDate);
		return this;
	}

	public AccountSkillProfileBuilder endDate(Date endDate) {
		entity.setEndDate(endDate);
		return this;
	}

	public AccountSkillProfile build() {
		return entity;
	}
}
