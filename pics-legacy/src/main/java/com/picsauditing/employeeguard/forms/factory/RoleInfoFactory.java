package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.ArrayList;
import java.util.List;

public class RoleInfoFactory {

	public List<RoleInfo> build(final List<Role> roles) {
		List<RoleInfo> roleInfos = new ArrayList<>();
		for (Role role : roles) {
            roleInfos.add(build(role));
		}

		return roleInfos;
	}

	public RoleInfo build(Role role) {
		RoleInfo roleInfo = new RoleInfo();
		roleInfo.setId(role.getId());
		roleInfo.setName(role.getName());
		return roleInfo;
	}
}
