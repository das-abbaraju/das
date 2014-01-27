package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.ArrayList;
import java.util.List;

public class RoleInfoFactory {

	public List<RoleInfo> build(List<Group> groups) {
		List<RoleInfo> roles = new ArrayList<>();
		for (Group group : groups) {
			roles.add(build(group));
		}

		return roles;
	}

	public RoleInfo build(Group groups) {
		RoleInfo roleInfo = new RoleInfo();
		roleInfo.setId(groups.getId());
		roleInfo.setName(groups.getName());
		return roleInfo;
	}
}
