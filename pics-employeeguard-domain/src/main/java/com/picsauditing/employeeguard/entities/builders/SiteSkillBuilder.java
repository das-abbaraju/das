package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.SiteSkill;

public class SiteSkillBuilder extends AbstractBaseEntityBuilder<SiteSkill, SiteSkillBuilder> {

	public SiteSkillBuilder() {
		entity = new SiteSkill();
		that = this;
	}

	public SiteSkillBuilder siteId(int siteId) {
		entity.setSiteId(siteId);
		return this;
	}

	public SiteSkillBuilder skill(AccountSkill accountSkill) {
		entity.setSkill(accountSkill);
		return this;
	}

	@Override
	public SiteSkill build() {
		return entity;
	}
}
