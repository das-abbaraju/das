package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class RoleStatusModelFactory extends RoleModelFactory {

	public RoleStatusModel create(final Role role, final List<SkillModel> skills, final SkillStatus status) {
		RoleStatusModel roleStatusModel = new RoleStatusModel(super.create(role, skills));
		roleStatusModel.setStatus(status);
		return roleStatusModel;
	}

}
