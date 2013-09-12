package com.picsauditing.actions.users;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserGroupSaveTest extends PicsActionTest {
    private UserGroupSave userGroupSave;
    private UserGroupManagementStatus status;

    @Mock
    private User user;
    @Mock
    private User group;
    @Mock
    private UserGroup userGroup;
    @Mock
    private UserManagementService userManagementService;
    @Mock
    private List<UserGroup> groupMembers;
    @Mock
    private List<UserGroup> usersUserGroups;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userGroupSave = new UserGroupSave();
        super.setUp(userGroupSave);

        status = new UserGroupManagementStatus();
        when(userManagementService.userIsAddableToGroup(user, group)).thenReturn(status);
        when(userManagementService.addUserToGroup(user, group, permissions)).thenReturn(userGroup);
        when(group.getMembers()).thenReturn(groupMembers);
        when(user.getGroups()).thenReturn(usersUserGroups);
        Whitebox.setInternalState(userGroupSave, "userManagementService", userManagementService);
    }

    @Test
    public void test_addUserToGroup_ValidationErrorIsRecorded() throws Exception {
        status.isOk = false;
        status.notOkErrorKey = "ERROR";

        Boolean result = Whitebox.invokeMethod(userGroupSave, "addUserToGroup", user, group);

        assertFalse(result);
        assertTrue(userGroupSave.getActionErrors().contains("ERROR"));
    }

    @Test
    public void test_addUserToGroup_IsOkToAddProxiesToService() throws Exception {
        status.isOk = true;

        Boolean result = Whitebox.invokeMethod(userGroupSave, "addUserToGroup", user, group);

        assertTrue(result);
        verify(userManagementService).addUserToGroup(user, group, permissions);
    }

    @Test
    public void test_addUserToGroup_IsOkAddedToGroupsForWebDisplayWidget() throws Exception {
        status.isOk = true;
        when(groupMembers.contains(userGroup)).thenReturn(false);
        when(usersUserGroups.contains(userGroup)).thenReturn(false);

        Whitebox.invokeMethod(userGroupSave, "addUserToGroup", user, group);

        verify(groupMembers).add(userGroup);
        verify(usersUserGroups).add(userGroup);
    }

}
