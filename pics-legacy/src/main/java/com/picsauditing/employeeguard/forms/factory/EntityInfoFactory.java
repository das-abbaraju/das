package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.EntityInfo;

import java.util.ArrayList;
import java.util.List;

public class EntityInfoFactory {

	public List<EntityInfo> create(List<AccountSkill> skills) {
		List<EntityInfo> entityInfos = new ArrayList<>();

		for (AccountSkill skill : skills) {
			entityInfos.add(new EntityInfo(skill.getId(), skill.getName()));
		}

		return entityInfos;
	}
}
