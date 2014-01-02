package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.Group;
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

		Group group = new AccountGroupBuilder().name("Group 1").build();
		List<Group> groups = Arrays.asList(group, new AccountGroupBuilder().name("Group 2").build());
		when(groupService.getGroupsForAccount(anyInt())).thenReturn(groups);
		when(groupService.getGroup(anyString(), anyInt())).thenReturn(group);
		when(groupService.search(anyString(), anyInt())).thenReturn(groups);
		when(groupService.getGroupsForAccount(anyInt())).thenReturn(groups);
		when(groupService.update(any(GroupNameSkillsForm.class), anyString(), anyInt(), anyInt())).thenReturn(group);
		when(groupService.update(any(GroupEmployeesForm.class), anyString(), anyInt(), anyInt())).thenReturn(group);

		return groupService;
	}
}
