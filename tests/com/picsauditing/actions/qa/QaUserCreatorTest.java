package com.picsauditing.actions.qa;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.ApiRequired;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class QaUserCreatorTest extends PicsActionTest {
    private QaUserCreator qaUserCreator;
    private Set<Integer> groupId;

    @Mock
    private UserManagementService userManagementService;
    @Mock
    private Account account;
    @Mock
    private User user;
    @Mock
    protected UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        qaUserCreator = new QaUserCreator();
        super.setUp(qaUserCreator);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(qaUserCreator, this);

        groupId = new HashSet<>();
        qaUserCreator.setAccount(account);
        qaUserCreator.setGroupId(groupId);
        Whitebox.setInternalState(qaUserCreator, "userManagementService", userManagementService);
        when(userManagementService.initializeNewUser(account)).thenReturn(user);
    }

    @Test
    public void testExecute_RequiresCreateTestUserPermAndApiKey() throws Exception {
        Method execute = qaUserCreator.getClass().getMethod("execute");
        assertTrue(execute.isAnnotationPresent(RequiredPermission.class));
        assertTrue(execute.isAnnotationPresent(ApiRequired.class));
        RequiredPermission annotation = execute.getAnnotation(RequiredPermission.class);
        assertEquals(OpPerms.CreateTestUser, annotation.value());
    }

    @Test
    public void testExecute_NullAccountIsJsonError() throws Exception {
        qaUserCreator.setAccount(null);

        qaUserCreator.execute();
        JSONObject json = qaUserCreator.getJson();
        String status = (String)json.get("status");

        assertTrue(status.startsWith("ERROR"));
    }

    @Test
    public void testExecute_GoodAccountResultsInSavedUser() throws Exception {
        qaUserCreator.execute();
        JSONObject json = qaUserCreator.getJson();
        String status = (String)json.get("status");

        assertTrue(status.startsWith("SUCCESS"));
    }

    @Test
    public void testExecute_UsernameEndsInGuid() throws Exception {
        doCallRealMethod().when(user).setUsername(anyString());
        doCallRealMethod().when(user).getUsername();

        qaUserCreator.execute();

        JSONObject json = qaUserCreator.getJson();
        String username = (String)json.get("username");
        assertTrue(username.matches("Selenium(-[a-zA-Z0-9]*){5}"));
    }

    @Test
    public void testExecute_UserOnlyAddedToOkGroups() throws Exception {
        User okGroup = mock(User.class);
        User notOkGroup = mock(User.class);
        when(userDAO.find(1)).thenReturn(okGroup);
        when(userDAO.find(2)).thenReturn(notOkGroup);
        groupId.add(1);
        groupId.add(2);
        List<User> okGroups = new ArrayList<>();
        okGroups.add(okGroup);
        when(userManagementService.getAddableGroups(permissions, account, user)).thenReturn(okGroups);
        when(userManagementService.userIsAddableToGroup(user, okGroup)).thenReturn(new UserGroupManagementStatus());
        UserGroupManagementStatus badStatus = new UserGroupManagementStatus();
        badStatus.isOk = false;
        when(userManagementService.userIsAddableToGroup(user, notOkGroup)).thenReturn(badStatus);
        qaUserCreator.execute();

        verify(userManagementService).addUserToGroup(user, okGroup, permissions);
        verify(userManagementService, never()).addUserToGroup(user, notOkGroup, permissions);
    }
}
