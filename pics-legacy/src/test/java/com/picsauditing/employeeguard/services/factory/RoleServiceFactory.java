package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.services.RoleService;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class RoleServiceFactory {
    private static RoleService roleService = Mockito.mock(RoleService.class);

    public static RoleService getRoleService() {
        Mockito.reset(roleService);

        Role role = new Role();
        role.setName("Role 1");

        Role role2 = new Role();
        role2.setName("Role 2");

        List<Role> Roles = Arrays.asList(role, role2);
        when(roleService.getRolesForAccount(anyInt())).thenReturn(Roles);
        when(roleService.getRole(anyString(), anyInt())).thenReturn(role);
        when(roleService.search(anyString(), anyInt())).thenReturn(Roles);
        when(roleService.getRolesForAccount(anyInt())).thenReturn(Roles);
        when(roleService.update(any(GroupNameSkillsForm.class), anyString(), anyInt(), anyInt())).thenReturn(role);
        when(roleService.update(any(GroupEmployeesForm.class), anyString(), anyInt(), anyInt())).thenReturn(role);

        return roleService;
    }
}
