package com.picsauditing.model.group;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupManagerTest {
    private static final int TEST_USER_ID = 123;
    private static final String TEST_USER_NAME = "Test User";
    private static final String TEST_USERNAME = "testusername";
    private static final int TEST_ACCOUNT_ID = 456;

    private GroupManager groupManager;

    @Mock
    private UserDAO userDAO;
    @Mock
    private User group;
    @Mock
    private Account account;
    @Mock
    private Permissions permissions;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        groupManager = new GroupManager();

        PicsTestUtil.autowireDAOsFromDeclaredMocks(groupManager, this);

        when(group.getId()).thenReturn(TEST_USER_ID);
        when(group.getAccount()).thenReturn(account);
        when(group.getName()).thenReturn(TEST_USER_NAME);
        when(group.getUsername()).thenReturn(TEST_USERNAME);
        when(account.getId()).thenReturn(TEST_ACCOUNT_ID);
    }

    @Test
    public void testSaveWithAuditColumnsAndRefresh() throws Exception {
        when(userDAO.save(group)).thenReturn(group);

        groupManager.saveWithAuditColumnsAndRefresh(group, permissions);

        InOrder inOrder = inOrder(group, userDAO);

        inOrder.verify(group).setAuditColumns(permissions);
        inOrder.verify(userDAO).save(group);
        inOrder.verify(userDAO).refresh(group);
    }

    @Test
    public void testIsGroupnameAvailable_TestsForGeneratedGroupname() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        groupManager.isGroupnameAvailable(group);

        String generatedGroupname = Whitebox.invokeMethod(groupManager, "generatedGroupname", group);

        verify(userDAO).duplicateUsername(captor.capture(), eq(TEST_USER_ID));
        assertEquals(generatedGroupname, captor.getValue());
    }

    @Test
    public void testIsGroupnameAvailable_isDuplicate() throws Exception {
        when(userDAO.duplicateUsername(any(String.class), any(int.class))).thenReturn(true);

        assertFalse(groupManager.isGroupnameAvailable(group));
    }

    @Test
    public void testIsGroupnameAvailable_isNotDuplicate() throws Exception {
        when(userDAO.duplicateUsername(any(String.class), any(int.class))).thenReturn(false);

        assertTrue(groupManager.isGroupnameAvailable(group));
    }

    @Test
    public void testSetUsernameToGeneratedGroupname() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        String generatedGroupname = Whitebox.invokeMethod(groupManager, "generatedGroupname", group);

        groupManager.setUsernameToGeneratedGroupname(group);

        verify(group).setUsername(captor.capture());
        assertEquals(generatedGroupname, captor.getValue());
        assertTrue(generatedGroupname.startsWith("GROUP"));
    }

    @Test
    public void testResetGroup() throws Exception {
        groupManager.resetGroup(group);
        verify(userDAO).refresh(group);
    }

    @Test
    public void testInitializeNewGroup() throws Exception {
        User group = groupManager.initializeNewGroup(account);
        assertThat(account, is(equalTo(group.getAccount())));
        assertThat(YesNo.Yes, is(equalTo(group.getIsGroup())));
        assertTrue(group.isActiveB());
    }

    @Test
    public void testGroupIsDeactivatable_GroupsAreCurrentlyAlwaysDeactivatable() throws Exception {
        UserGroupManagementStatus status = groupManager.groupIsDeactivatable(group);
        assertTrue(status.isOk);
    }

    @Test
    public void testGroupIsMovable_GroupsAreNeverMovable() throws Exception {
        UserGroupManagementStatus status = groupManager.groupIsMovable(group);
        assertFalse(status.isOk);
    }

    @Test
    public void testDeactivate() throws Exception {
        groupManager.deactivate(group, permissions);

        InOrder inOrder = inOrder(userDAO, group);

        inOrder.verify(userDAO).refresh(group);
        inOrder.verify(group).setActive(false);
        inOrder.verify(userDAO).save(group);
    }

    @Test
    public void testDelete_SoftDeletesWithUserNameChangeAndSave() throws Exception {
        groupManager.delete(group, permissions);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(group).setUsername(captor.capture());
        assertTrue(captor.getValue().startsWith("DELETE-"));
        verify(userDAO).save(group);
    }

    @Test
    public void testGroupIsDeletable_GroupsAreAlwaysDeletable() throws Exception {
        UserGroupManagementStatus status = groupManager.groupIsDeletable(group);
        assertTrue(status.isOk);

    }
}
