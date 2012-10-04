package com.picsauditing.actions.users;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;

public class UsersManageTest extends PicsActionTest {
	private static int NOT_GROUP_CSR = User.GROUP_CSR++;
	private UsersManage usersManage;
	private List<UserGroup> userGroups;
	private List<UserGroup> members;

	@Mock
	private User user;
	@Mock
	private User group;
	@Mock
	private UserGroup userGroup;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		usersManage = new UsersManage();
		super.setUp(usersManage);

	}

	@Test
	public void testGetCsrs_Users_UserGroup_WillNotBeReturned() throws Exception {
		usersManage.setUser(user);
		userGroups = new ArrayList<UserGroup>();
		userGroups.add(userGroup);
		when(user.getGroups()).thenReturn(userGroups);

		members = new ArrayList<UserGroup>();
		UserGroup firstMemberOfGroup = mock(UserGroup.class);
		User firstMemberOfGroupUser = mock(User.class);
		when(firstMemberOfGroup.getUser()).thenReturn(firstMemberOfGroupUser);
		when(firstMemberOfGroupUser.isGroup()).thenReturn(false);
		UserGroup secondMemberOfGroup = mock(UserGroup.class);
		User secondMemberOfGroupUser = mock(User.class);
		when(secondMemberOfGroup.getUser()).thenReturn(secondMemberOfGroupUser);
		when(secondMemberOfGroupUser.isGroup()).thenReturn(false);

		members.add(userGroup);
		members.add(firstMemberOfGroup);
		members.add(secondMemberOfGroup);

		when(group.getId()).thenReturn(User.GROUP_CSR);
		when(group.getMembers()).thenReturn(members);
		when(userGroup.getGroup()).thenReturn(group);
		when(userGroup.getUser()).thenReturn(group);
		when(group.getName()).thenReturn("TestyMcTest");
		when(firstMemberOfGroupUser.getName()).thenReturn("aTestyMcTest");
		when(secondMemberOfGroupUser.getName()).thenReturn("bTestyMcTest");
		when(user.getId()).thenReturn(NOT_GROUP_CSR);
		when(user.isGroup()).thenReturn(false);

		List<UserGroup> csrsNotIncludingCurrent = usersManage.getCsrs();

		assertThat(csrsNotIncludingCurrent, not(hasItem(userGroup)));
	}

	// test correct removal
}
