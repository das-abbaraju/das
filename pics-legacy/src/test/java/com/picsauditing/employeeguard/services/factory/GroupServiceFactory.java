package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.builders.AccountGroupBuilder;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.services.GroupService;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class GroupServiceFactory {
	private static GroupService groupService = Mockito.mock(GroupService.class);

	public static GroupService getGroupService() {
		Mockito.reset(groupService);

		AccountGroup accountGroup = new AccountGroupBuilder().name("Group 1").build();
		List<AccountGroup> accountGroups = Arrays.asList(accountGroup, new AccountGroupBuilder().name("Group 2").build());
		when(groupService.getGroupsForAccount(anyInt())).thenReturn(accountGroups);
		when(groupService.getGroup(anyString(), anyInt())).thenReturn(accountGroup);
		when(groupService.search(anyString(), anyInt())).thenReturn(accountGroups);
		when(groupService.getGroupsForAccount(anyInt())).thenReturn(accountGroups);
		when(groupService.update(any(GroupNameSkillsForm.class), anyString(), anyInt(), anyInt())).thenReturn(accountGroup);
		when(groupService.update(any(GroupEmployeesForm.class), anyString(), anyInt(), anyInt())).thenReturn(accountGroup);

		return groupService;
	}
}
