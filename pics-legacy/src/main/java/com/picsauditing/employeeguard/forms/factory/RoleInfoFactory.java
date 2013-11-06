package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.entities.AccountGroup;

import java.util.ArrayList;
import java.util.List;

public class RoleInfoFactory {

    public List<RoleInfo> build(List<AccountGroup> groups) {
        List<RoleInfo> roles = new ArrayList<>();
        for (AccountGroup group : groups) {
            roles.add(build(group));
        }

        return roles;
    }

    public RoleInfo build(AccountGroup groups) {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setId(groups.getId());
        roleInfo.setName(groups.getName());
        return roleInfo;
    }
}
