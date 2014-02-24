package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;

import java.util.List;

public class RoleModelFactory {

	public RoleModel create(final Role role, final List<SkillModel> skills) {
		RoleModel roleModel = new RoleModel();
		roleModel.setId(role.getId());
		roleModel.setName(role.getName());
		roleModel.setSkills(skills);
		return roleModel;
	}

}
