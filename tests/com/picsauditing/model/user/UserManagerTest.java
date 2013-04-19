package com.picsauditing.model.user;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserAccessDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserGroupDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserManagerTest {
    private static final int PERM_USER_ID = 123;

    private UserManager userManager;
    private Set<OpPerms> ownedOpPerms;
    private List<UserGroup> userGroups;

    @Mock
    private UserDAO userDAO;
    @Mock
    private Account account;
    @Mock
    protected UserAccessDAO userAccessDAO;
    @Mock
    protected UserGroupDAO userGroupDAO;
    @Mock
    private AccountDAO accountDAO;
    @Mock
    private User user;
    @Mock
    private User group;
    @Mock
    private Permissions permissions;
    @Mock
    private List mockList;
    @Mock
    private List<UserAccess> ownedPermissions;
    @Mock
    private UserGroup userGroup;
    @Mock
    private List<User> usersByRole;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userManager = new UserManager();

        ownedOpPerms = new HashSet<>();
        userGroups = new ArrayList<>();

        PicsTestUtil.autowireDAOsFromDeclaredMocks(userManager, this);

        when(user.getAccount()).thenReturn(account);
        when(user.getOwnedOpPerms()).thenReturn(ownedOpPerms);
        when(user.getOwnedPermissions()).thenReturn(ownedPermissions);
        when(group.isGroup()).thenReturn(true);
        when(account.getUsersByRole(any(OpPerms.class))).thenReturn(usersByRole);
    }

    @Test
    public void testMoveUserToNewAccount_SetsAccountAndSaves() throws Exception {
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        userManager.moveUserToNewAccount(user, account);

        verify(userDAO).save(user);
        verify(user).setAccount(captor.capture());
        assertEquals(account, captor.getValue());
    }

    @Test
    public void testMoveUserToNewAccount_RemovesAllGroupPerms() throws Exception {
        List<UserGroup> userGroupList = new ArrayList<UserGroup>();
        UserGroup userGroup1 = mock(UserGroup.class);
        UserGroup userGroup2 = mock(UserGroup.class);
        userGroupList.add(userGroup1);
        userGroupList.add(userGroup2);
        when(user.getId()).thenReturn(123);
        when(userGroupDAO.findByUser(123)).thenReturn(userGroupList);

        userManager.moveUserToNewAccount(user, account);

        verify(userAccessDAO).remove(userGroup1);
        verify(userAccessDAO).remove(userGroup2);
    }

    @Test
    public void testMoveUserToNewAccount_RemovesAllUserOpPerms() throws Exception {
        List<UserAccess> userAccessList = new ArrayList<UserAccess>();
        UserAccess userAccess1 = mock(UserAccess.class);
        UserAccess userAccess2 = mock(UserAccess.class);
        userAccessList.add(userAccess1);
        userAccessList.add(userAccess2);
        when(user.getId()).thenReturn(123);
        when(userAccessDAO.findByUser(123)).thenReturn(userAccessList);

        userManager.moveUserToNewAccount(user, account);

        verify(userAccessDAO).remove(userAccess1);
        verify(userAccessDAO).remove(userAccess2);
    }

    @Test
    public void testMoveUserToNewAccount_ByAccountIdLooksUpAccount() throws Exception {
        userManager.moveUserToNewAccount(user, 1);
        verify(accountDAO).find(1);
    }

    @Test
    public void testInitializeNewUser_SetsIsGroupToNo() throws Exception {
        User user = userManager.initializeNewUser(account);
        assertThat(YesNo.No, is(equalTo(user.getIsGroup())));
    }

    @Test
    public void testDelete_PrefixesUsernameForSoftDelete() throws Exception {
        when(user.getUsername()).thenReturn("testusername");

        userManager.delete(user);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(user).setUsername(captor.capture());
        assertTrue(captor.getValue().startsWith("DELETE-"));
        verify(userDAO).save(user);
    }

    @Test
    public void testDeactivateUser_ResetsSetsActiveAndSavesInOrder() throws Exception {
        userManager.deactivate(user);

        InOrder inOrder = inOrder(userDAO,  user);

        inOrder.verify(userDAO).refresh(user);
        inOrder.verify(user).setActive(false);
        inOrder.verify(userDAO).save(user);
    }

    @Test
    public void testActivateUser_ResetsSetsActiveAndSavesInOrder() throws Exception {
        userManager.activateUser(user);

        InOrder inOrder = inOrder(userDAO,  user);

        inOrder.verify(userDAO).refresh(user);
        inOrder.verify(user).setActive(true);
        inOrder.verify(userDAO).save(user);
    }

    @Test
    public void testUnlock_HappyPath_ResetsUserInputSetsLockSaves() throws Exception {
        userManager.unlock(user);

        InOrder inOrder = inOrder(userDAO,  user);

        inOrder.verify(userDAO).refresh(user);
        inOrder.verify(user).setLockUntil(null);
        inOrder.verify(userDAO).save(user);
    }

    @Test
    public void testSaveWithAuditColumnsAndRefresh_HappyPath_SetsAuditColumnsSavesAndRefreshes() throws Exception {
        when(userDAO.save(user)).thenReturn(user);

        userManager.saveWithAuditColumnsAndRefresh(user, permissions);

        InOrder inOrder = inOrder(user, userDAO);

        inOrder.verify(user).setAuditColumns(permissions);
        inOrder.verify(userDAO).save(user);
        inOrder.verify(userDAO).refresh(user);
    }

    @Test
    public void testSaveWithAuditColumnsAndRefresh_NullUser() throws Exception {
        try {
            userManager.saveWithAuditColumnsAndRefresh(null, permissions);
        } catch (RuntimeException e) {
            fail("null user should not produce an exception");
        }

        verify(user, never()).setAuditColumns(permissions);
        verify(userDAO, never()).save(user);
        verify(userDAO, never()).refresh(user);
    }

    @Test
    public void testUserIsDeactivatable_CannotDeactivatePrimaryContact() throws Exception {
        when(account.getPrimaryContact()).thenReturn(user);

        UserGroupManagementStatus status = userManager.userIsDeactivatable(user, account);

        assertFalse(status.isOk);
        assertEquals(UserManagementService.CANNOT_DEACTIVATE_ERROR_KEY, status.notOkErrorKey);
    }

    @Test
    public void testUserIsDeactivatable_ContractorAccountMustHaveAtLeastOneContractorAdminUser() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorAdmin);

        UserGroupManagementStatus status = commonForUserIsDeactivatable();

        assertEquals(OpPerms.ContractorAdmin.getDescription(), status.errorDetail);
    }

    @Test
    public void testUserIsDeactivatable_ContractorAccountMustHaveAtLeastOneContractorBillingUser() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorBilling);

        UserGroupManagementStatus status = commonForUserIsDeactivatable();

        assertEquals(OpPerms.ContractorBilling.getDescription(), status.errorDetail);
    }

    @Test
    public void testUserIsDeactivatable_ContractorAccountMustHaveAtLeastOneContractorSafetyUser() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorSafety);

        UserGroupManagementStatus status = commonForUserIsDeactivatable();

        assertEquals(OpPerms.ContractorSafety.getDescription(), status.errorDetail);
    }

    @Test
    public void testUserIsDeactivatable_ContractorAccountMustHaveAtLeastOneContractorInsuranceUser() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorInsurance);

        UserGroupManagementStatus status = commonForUserIsDeactivatable();

        assertEquals(OpPerms.ContractorInsurance.getDescription(), status.errorDetail);
    }

    @Test
    public void testUserIsDeactivatable_isOk() throws Exception {
        // return different user for primary
        when(account.getPrimaryContact()).thenReturn(mock(User.class));
        UserGroupManagementStatus status = userManager.userIsDeactivatable(user, account);
        assertTrue(status.isOk);
    }

    private UserGroupManagementStatus commonForUserIsDeactivatable() {
        when(account.getUsersByRole(Matchers.any(OpPerms.class))).thenReturn(mockList);
        when(mockList.size()).thenReturn(1);
        when(account.isContractor()).thenReturn(true);
        // return different user for primary
        when(account.getPrimaryContact()).thenReturn(mock(User.class));

        UserGroupManagementStatus status = userManager.userIsDeactivatable(user, account);

        assertFalse(status.isOk);
        assertEquals(UserManagementService.CANNOT_DEACTIVATE_NEED_ONE_USER_WITH_PERM, status.notOkErrorKey);
        return status;
    }

    @Test
    public void testUserIsDeletable_NullUserIsNot() throws Exception {
        UserGroupManagementStatus status = userManager.userIsDeletable(null);

        assertFalse(status.isOk);
        assertEquals(status.notOkErrorKey, UserManagementService.NO_USER_FOUND);
    }

    @Test
    public void testUserIsDeletable_CannotDeletePrimaryUser() throws Exception {
        when(account.getPrimaryContact()).thenReturn(user);

        UserGroupManagementStatus status = userManager.userIsDeletable(user);

        assertFalse(status.isOk);
        assertEquals(status.notOkErrorKey, UserManagementService.CANNOT_DELETE_PRIMARY_USER);
    }

    @Test
    public void testUserIsDeletable_CanDeleteNotPrimaryUser() throws Exception {
        when(account.getPrimaryContact()).thenReturn(mock(User.class));

        UserGroupManagementStatus status = userManager.userIsDeletable(user);

        assertTrue(status.isOk);
    }

    @Test
    public void testContractorUserIsSavable_ActiveContractorUserNotAddingAdminHasAPermission_isOk() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(user.isActiveB()).thenReturn(true);
        List<OpPerms> permissionsBeingAdded = new ArrayList<OpPerms>();
        permissionsBeingAdded.add(OpPerms.ContractorInsurance);

        UserGroupManagementStatus status = userManager.contractorUserIsSavable(user, account, permissionsBeingAdded);

        assertTrue(status.isOk);
    }

    @Test
    public void testContractorUserIsSavable_ActiveContractorUserAddingAdmin_FewerThanThree_isOk() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(user.isActiveB()).thenReturn(true);
        when(mockList.size()).thenReturn(1);
        when(account.getUsersByRole(OpPerms.ContractorAdmin)).thenReturn(mockList);
        List<OpPerms> permissionsBeingAdded = new ArrayList<OpPerms>();
        permissionsBeingAdded.add(OpPerms.ContractorAdmin);

        UserGroupManagementStatus status = userManager.contractorUserIsSavable(user, account, permissionsBeingAdded);

        assertTrue(status.isOk);
    }

    @Test
    public void testContractorUserIsSavable_ActiveContractorUserAddingAdmin_GreaterThanThree_isNotOk() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(user.isActiveB()).thenReturn(true);
        when(mockList.size()).thenReturn(4);
        when(account.getUsersByRole(OpPerms.ContractorAdmin)).thenReturn(mockList);
        List<OpPerms> permissionsBeingAdded = new ArrayList<OpPerms>();
        permissionsBeingAdded.add(OpPerms.ContractorAdmin);

        UserGroupManagementStatus status = userManager.contractorUserIsSavable(user, account, permissionsBeingAdded);

        assertFalse(status.isOk);
    }

    @Test
    public void testContractorUserIsSavable_ActiveContractorUserNotAddingAdmin_NoPerms_isNotOk() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(user.isActiveB()).thenReturn(true);
        when(mockList.size()).thenReturn(3);
        when(account.getUsersByRole(OpPerms.ContractorAdmin)).thenReturn(mockList);
        List<OpPerms> permissionsBeingAdded = new ArrayList<OpPerms>();

        UserGroupManagementStatus status = userManager.contractorUserIsSavable(user, account, permissionsBeingAdded);

        assertFalse(status.isOk);
    }

    @Test
    public void testContractorUserIsSavable_IfNotContractorUserIsSavable() throws Exception {
        when(account.isContractor()).thenReturn(false);
        List<OpPerms> permissionsBeingAdded = new ArrayList<OpPerms>();

        UserGroupManagementStatus status = userManager.contractorUserIsSavable(user, account, permissionsBeingAdded);

        assertTrue(status.isOk);
    }

    @Test
    public void testUserIsMovable_NullUserIsStatusError() throws Exception {
        UserGroupManagementStatus status = userManager.userIsMovable(null);

        assertFalse(status.isOk);
        assertEquals(status.notOkErrorKey, UserManagementService.NO_USER_FOUND);
    }

    @Test
    public void testUserIsMovable_CannotMoveLastUserOnAccount() throws Exception {
        when(mockList.size()).thenReturn(1);
        when(account.getUsers()).thenReturn(mockList);

        UserGroupManagementStatus status = userManager.userIsMovable(user);

        assertFalse(status.isOk);
        assertEquals(status.notOkErrorKey, UserManagementService.CANNOT_MOVE_LAST_USER);
    }

    @Test
    public void testUserIsMovable_CannotMoveIfUsersOnAccountIsNull() throws Exception {
        when(account.getUsers()).thenReturn(null);

        UserGroupManagementStatus status = userManager.userIsMovable(user);

        assertFalse(status.isOk);
        assertEquals(status.notOkErrorKey, UserManagementService.CANNOT_MOVE_LAST_USER);
    }

    @Test
    public void testUserIsMovable_NullAccountCannotMove() throws Exception {
        when(user.getAccount()).thenReturn(null);

        UserGroupManagementStatus status = userManager.userIsMovable(user);

        assertFalse(status.isOk);
        assertEquals(status.notOkErrorKey, UserManagementService.CANNOT_MOVE_LAST_USER);
    }

    @Test
    public void testInitializeNewUser() throws Exception {
        User user = userManager.initializeNewUser(account);
        assertThat(account, is(equalTo(user.getAccount())));
        assertThat(YesNo.No, is(equalTo(user.getIsGroup())));
        assertTrue(user.isActiveB());
    }

    @Test
    public void test_getAddableGroups_NoEditUserPermissionReturnsEmptyList() throws Exception {
        when(permissions.hasPermission(OpPerms.EditUsers, OpType.Edit)).thenReturn(false);

        List<User> result = userManager.getAddableGroups(permissions, account, user);

        assertTrue(result.isEmpty());
    }

    @Test
    public void test_addUserToGroup_SetsAndSaves() throws Exception {
        UserGroup userGroup = userManager.addUserToGroup(user, group, permissions);
        assertEquals(userGroup.getUser(), user);
        assertEquals(userGroup.getGroup(), group);
        verify(userGroupDAO).save(userGroup);
    }

    @Test
    public void test_userIsAddableToGroup_CannotAddUserToUser() throws Exception {
        when(group.isGroup()).thenReturn(false);

        UserGroupManagementStatus status = userManager.userIsAddableToGroup(user, group);

        assertFalse(status.isOk);
        assertEquals(UserManagementService.CANNOT_ADD_USER_TO_USER, status.notOkErrorKey);
    }

    @Test
    public void test_userIsAddableToGroup_CannotAddGroupToItself() throws Exception {
        UserGroupManagementStatus status = userManager.userIsAddableToGroup(group, group);

        assertFalse(status.isOk);
        assertEquals(UserManagementService.CANNOT_ADD_GROUP_TO_ITSELF, status.notOkErrorKey);
    }

    @Test
    public void test_userIsAddableToGroup_CannotAddSameGroupTwice() throws Exception {
        when(userGroup.getGroup()).thenReturn(group);
        userGroups.add(userGroup);
        when(user.getGroups()).thenReturn(userGroups);

        UserGroupManagementStatus status = userManager.userIsAddableToGroup(user, group);

        assertFalse(status.isOk);
        assertEquals(UserManagementService.USER_ALREADY_MEMBER_OF_GROUP, status.notOkErrorKey);
    }

    @Test
    public void test_userIsAddableToGroup_AddingGroupToGroupIsNotOkIfParentIsDecendant() throws Exception {
        List<UserGroup> userMembers = new ArrayList<>();
        userMembers.add(userGroup);
        when(userGroup.getUser()).thenReturn(group);
        when(user.getMembers()).thenReturn(userMembers);
        when(user.isGroup()).thenReturn(true);

        UserGroupManagementStatus status = userManager.userIsAddableToGroup(user, group);

        assertFalse(status.isOk);
    }

    @Test
    public void test_userIsAddableToGroup_HappyPath() throws Exception {
        when(userGroup.getGroup()).thenReturn(mock(User.class));
        userGroups.add(userGroup);
        when(user.getGroups()).thenReturn(userGroups);

        UserGroupManagementStatus status = userManager.userIsAddableToGroup(user, group);

        assertTrue(status.isOk);
    }

    @Test
    public void testUpdateUserPermissions_DoesNotHaveContractorAdminWantsToAdd() throws Exception {
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(true, false, false, false)
        );

        verify(user).addOwnedPermissions(OpPerms.ContractorAdmin, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorBilling, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorSafety, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorInsurance, PERM_USER_ID);
    }

    @Test
    public void testUpdateUserPermissions_DoesNotHaveContractorSafetyWantsToAdd() throws Exception {
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(false, false, true, false)
        );

        verify(user, never()).addOwnedPermissions(OpPerms.ContractorAdmin, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorBilling, PERM_USER_ID);
        verify(user).addOwnedPermissions(OpPerms.ContractorSafety, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorInsurance, PERM_USER_ID);
    }

    @Test
    public void testUpdateUserPermissions_DoesNotHaveAnyPermsWantsToAddAll() throws Exception {
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(true, true, true, true)
        );

        verify(user).addOwnedPermissions(OpPerms.ContractorAdmin, PERM_USER_ID);
        verify(user).addOwnedPermissions(OpPerms.ContractorBilling, PERM_USER_ID);
        verify(user).addOwnedPermissions(OpPerms.ContractorSafety, PERM_USER_ID);
        verify(user).addOwnedPermissions(OpPerms.ContractorInsurance, PERM_USER_ID);
    }

    @Test
    public void testUpdateUserPermissions_HasAllPermsWantsToAddAll() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorAdmin);
        ownedOpPerms.add(OpPerms.ContractorBilling);
        ownedOpPerms.add(OpPerms.ContractorSafety);
        ownedOpPerms.add(OpPerms.ContractorInsurance);
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(true, true, true, true)
        );

        verify(user, never()).addOwnedPermissions(OpPerms.ContractorAdmin, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorBilling, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorSafety, PERM_USER_ID);
        verify(user, never()).addOwnedPermissions(OpPerms.ContractorInsurance, PERM_USER_ID);
    }

    @Test
    public void testUpdateUserPermissions_HasContractorAdminPermWantsToRemove_NotOnlyOne() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorAdmin);
        List<UserAccess> ownedPermissions = new ArrayList<>();
        UserAccess ua = new UserAccess();
        ua.setOpPerm(OpPerms.ContractorAdmin);
        ownedPermissions.add(ua);

        when(user.getOwnedPermissions()).thenReturn(ownedPermissions);
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);
        when(usersByRole.size()).thenReturn(3);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(false, false, false, false)
        );

        verify(userAccessDAO).remove(ua);
        assertTrue(statuses.isEmpty());
    }

    @Test
    public void testUpdateUserPermissions_HasContractorAdminPermWantsToRemove_IsOnlyOne() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorAdmin);
        List<UserAccess> ownedPermissions = new ArrayList<>();
        UserAccess ua = new UserAccess();
        ua.setOpPerm(OpPerms.ContractorAdmin);
        ownedPermissions.add(ua);

        when(user.getOwnedPermissions()).thenReturn(ownedPermissions);
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);
        when(usersByRole.size()).thenReturn(1);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(false, false, false, false)
        );

        verify(userAccessDAO, never()).remove(ua);
        assertFalse(statuses.isEmpty());
    }


    @Test
    public void testUpdateUserPermissions_HasContractorInsurancePermWantsToRemove_NotOnlyOne() throws Exception {
        ownedOpPerms.add(OpPerms.ContractorInsurance);
        List<UserAccess> ownedPermissions = new ArrayList<>();
        UserAccess ua = new UserAccess();
        ua.setOpPerm(OpPerms.ContractorInsurance);
        ownedPermissions.add(ua);

        when(user.getOwnedPermissions()).thenReturn(ownedPermissions);
        when(permissions.getUserId()).thenReturn(PERM_USER_ID);
        when(usersByRole.size()).thenReturn(3);

        List<UserGroupManagementStatus> statuses = userManager.updateUserPermissions(
                user, account, permissions, requestedPermState(false, false, false, false)
        );

        verify(userAccessDAO).remove(ua);
        assertTrue(statuses.isEmpty());
    }

    private Map<OpPerms, Boolean> requestedPermState(boolean conAdmin, boolean conBilling, boolean conSafety, boolean conInsurance) {
        Map<OpPerms, Boolean> requestedPermState = new HashMap<>();
        requestedPermState.put(OpPerms.ContractorAdmin, conAdmin);
        requestedPermState.put(OpPerms.ContractorBilling, conBilling);
        requestedPermState.put(OpPerms.ContractorSafety, conSafety);
        requestedPermState.put(OpPerms.ContractorInsurance, conInsurance);
        return requestedPermState;
    }

}