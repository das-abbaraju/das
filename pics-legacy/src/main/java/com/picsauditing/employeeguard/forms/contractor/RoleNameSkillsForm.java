package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import com.picsauditing.employeeguard.validators.duplicate.DuplicateInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;

public class RoleNameSkillsForm implements DuplicateInfoProvider {

	protected String name;

	protected int[] skills;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getSkills() {
		return skills;
	}

	public void setSkills(int[] skills) {
		this.skills = skills;
	}

	@Override
	public UniqueIndexable getUniqueIndexable() {
		return new Role.RoleUniqueKey(SessionInfoProviderFactory.getSessionInfoProvider().getId(),
				SessionInfoProviderFactory.getSessionInfoProvider().getAccountId(), name);
	}

	@Override
	public Class<?> getType() {
		return Role.class;
	}
}
