package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.RoleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoleModelFactory {

	public List<RoleModel> create(final Map<Role, SkillStatus> roleStatusMap) {
		List<RoleModel> roles = new ArrayList<>();
		for (Role role : roleStatusMap.keySet()) {
			roles.add(create(role, roleStatusMap.get(role)));
		}

		return roles;
	}

	public RoleModel create(final Role role, final SkillStatus skillStatus) {
		Set<Integer> skillIds = Utilities.getIdsFromCollection(role.getSkills(), new Utilities.Identitifable<AccountSkillRole, Integer>() {
			@Override
			public Integer getId(AccountSkillRole element) {
				return element.getSkill().getId();
			}
		});

		return new RoleModel.Builder()
				.id(role.getId())
				.name(role.getName())
				.skills(skillIds)
				.status(skillStatus)
				.build();
	}

}
