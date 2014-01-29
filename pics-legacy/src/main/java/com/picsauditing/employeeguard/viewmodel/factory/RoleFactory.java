package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.viewmodel.model.Role;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoleFactory {

    public static Role create(final AccountGroup role) {
        return new Role(role.getId(), role.getName());
    }

    public static List<Role> createFromRoles(final List<AccountGroup> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return Collections.emptyList();
        }

        List<Role> roles = new ArrayList<>();
        for (AccountGroup accountGroup : roleList) {
            roles.add(create(accountGroup));
        }

        return roles;
    }

    public static Role create(final ProjectRole projectRole) {
        AccountGroup role = projectRole.getRole();
        return create(role);
    }

    public static List<Role> createFromProjectRoles(final List<ProjectRole> projectRoles) {
        if (CollectionUtils.isEmpty(projectRoles)) {
            return Collections.emptyList();
        }

        List<Role> roles = new ArrayList<>();
        for (ProjectRole projectRole : projectRoles) {
            roles.add(create(projectRole));
        }

        return roles;
    }
}
