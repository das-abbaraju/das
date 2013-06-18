package com.picsauditing.actions.users;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.model.group.GroupManagementService;
import com.picsauditing.model.usergroup.UserGroupManagementStatus;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.model.user.UserManagementService;
import com.picsauditing.model.user.UserManager;
import com.picsauditing.validator.InputValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UsersManageTest extends PicsActionTest {

	private static int NOT_GROUP_CSR = User.GROUP_CSR + 1;
	private static int INVALID_USER_ID = 0;
	private UsersManage usersManage;
	private List<UserGroup> userGroups;
	private List<UserGroup> members;
	@Mock
	private User user;
	@Mock
	private User group;
	@Mock
	private UserGroup userGroup;
	@Mock
	private UserDAO userDAO;
	@Mock
	private InputValidator inputValidator;
	@Mock
	private FeatureToggle featureToggle;
    @Mock
    private Account account;
    @Mock
    private UserManagementService userManagementService;
    @Mock
    private GroupManagementService groupManagementService;
    @Mock
    private AccountDAO accountDAO;
    @Mock
    private EmailQueueDAO emailQueueDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		usersManage = new UsersManage();
		super.setUp(usersManage);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(usersManage, this);
		Whitebox.setInternalState(usersManage, "inputValidator", inputValidator);
		Whitebox.setInternalState(usersManage, "featureToggle", featureToggle);
        Whitebox.setInternalState(usersManage, "userManagementService", userManagementService);
        Whitebox.setInternalState(usersManage, "groupManagementService", groupManagementService);

        when(permissions.getOperatorChildren()).thenReturn(new HashSet<Integer>());
        when(i18nCache.hasKey(anyString(), eq(Locale.ENGLISH))).thenReturn(true);
	}

    @Test
    public void testAdd_GroupProxiesToGroupManagementService() throws Exception {
        usersManage.setUserIsGroup(YesNo.Yes);
        usersManage.setAccount(account);

        String strutsResult = usersManage.add();

        verify(groupManagementService).initializeNewGroup(account);
        assertEquals(Action.SUCCESS, strutsResult);
    }

    @Test
    public void testAdd_UserProxiesToUserManagementService() throws Exception {
        usersManage.setUserIsGroup(YesNo.No);
        usersManage.setAccount(account);

        String strutsResult = usersManage.add();

        verify(userManagementService).initializeNewUser(account);
        assertEquals(Action.SUCCESS, strutsResult);
    }

    @Test
    public void testSave_NewGroup_NameIsAvailable() throws Exception {
        saveGroupCommon();

        when(i18nCache.getText(eq("UsersManage.GroupSavedSuccessfully"), eq(Locale.ENGLISH), any())).thenReturn("GroupSavedSuccessfully");
        when(groupManagementService.isGroupnameAvailable(user)).thenReturn(true);
        // newUser (e.g. new group)
        when(user.getId()).thenReturn(0);

        usersManage.save();

        verify(groupManagementService).setUsernameToGeneratedGroupname(user);
        verify(groupManagementService).saveWithAuditColumnsAndRefresh(user, permissions);
        assertTrue(usersManage.getActionMessages().contains("GroupSavedSuccessfully"));
    }

    @Test
    public void testSave_NewGroup_NameIsNotAvailableResetsAndAddsError() throws Exception {
        saveGroupCommon();

        when(i18nCache.getText(eq("UsersManage.GroupnameNotAvailable"), eq(Locale.ENGLISH), any())).thenReturn("GroupnameNotAvailable");
        when(groupManagementService.isGroupnameAvailable(user)).thenReturn(false);
        // newUser (e.g. new group)
        when(user.getId()).thenReturn(0);

        usersManage.save();

        verify(groupManagementService).resetGroup(user);
        assertTrue(usersManage.getActionErrors().contains("GroupnameNotAvailable"));
    }

    @Test
    public void testSave_InitializesLazyCollections() throws Exception {
        saveGroupCommon();

        usersManage.save();

        verify(user).getGroups();
        verify(user).getOwnedPermissions();
    }

    @Test
    public void testSave_SetsWebformGroupVariableInUserObject() throws Exception {
        saveGroupCommon();

        usersManage.save();

        ArgumentCaptor<YesNo> captor = ArgumentCaptor.forClass(YesNo.class);
        verify(user).setIsGroup(captor.capture());
        assertTrue(captor.getValue().isTrue());
    }

    @Test
    public void testSave_FieldErrorResetsUserAndReturnsProperStrutsResult() throws Exception {
        saveGroupCommon();
        usersManage.addFieldError("test", "test message");

        String strutsResult = usersManage.save();

        verify(userManagementService).resetUser(user);
        assertEquals(PicsActionSupport.INPUT_ERROR, strutsResult);

    }

    private void saveGroupCommon() {
        when(user.isGroup()).thenReturn(true);
        when(user.getAccount()).thenReturn(account);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        usersManage.setUserIsGroup(YesNo.Yes);
    }

    @Test
    public void testUnlock_ProxiesToServiceAddsMessageAndRedirects() throws Exception {
        when(i18nCache.getText(eq("UsersManage.Unlocked"), eq(Locale.ENGLISH), any())).thenReturn("unlocked");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);

        String strutsResult = usersManage.unlock();

        verify(userManagementService).unlock(user);
        assertTrue(usersManage.getActionMessages().contains("unlocked"));
        assertEquals("UsersManage.action?account=456&user=123", usersManage.getUrl());
        assertEquals(PicsActionSupport.REDIRECT, strutsResult);
    }

    @Test
    public void testMove_ProxiesToServiceAddsMessageAndRedirects() throws Exception {
        when(i18nCache.getText(eq("UsersManage.SuccessfullyMoved"), eq(Locale.ENGLISH), anyVararg())).thenReturn("SuccessfullyMoved");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);
        when(user.isGroup()).thenReturn(false);
        when(userManagementService.userIsMovable(user)).thenReturn(new UserGroupManagementStatus());
        usersManage.setMoveToAccount(999);

        String strutsResult = usersManage.move();

        verify(userManagementService).moveUserToNewAccount(user, 999);
        assertTrue(usersManage.getActionMessages().contains("SuccessfullyMoved"));
        assertEquals("UsersManage.action?account=456&user=123", usersManage.getUrl());
        assertEquals(PicsActionSupport.REDIRECT, strutsResult);
    }

    @Test
    public void testMove_ErrorDoesNotProxyAddsErrorAndRedirects() throws Exception {
        when(i18nCache.getText(eq("NotOkToMove"), eq(Locale.ENGLISH), any())).thenReturn("NotOkToMove");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);
        when(user.isGroup()).thenReturn(false);
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        status.isOk = false;
        status.notOkErrorKey = "NotOkToMove";
        when(userManagementService.userIsMovable(user)).thenReturn(status);
        usersManage.setMoveToAccount(999);

        String strutsResult = usersManage.move();

        verify(userManagementService, never()).moveUserToNewAccount(user, 999);
        assertTrue(usersManage.getActionErrors().contains("NotOkToMove"));
        assertEquals("UsersManage.action?account=456&user=123", usersManage.getUrl());
        assertEquals(PicsActionSupport.REDIRECT, strutsResult);
    }

    @Test
    public void testDeactivate_ProxiesToServiceAddsMessageAndRedirects() throws Exception {
        when(i18nCache.getText(eq("UsersManage.UserInactivated"), eq(Locale.ENGLISH), anyVararg())).thenReturn("UserInactivated");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);
        when(user.isGroup()).thenReturn(false);
        when(userManagementService.userIsDeactivatable(user, account)).thenReturn(new UserGroupManagementStatus());

        String strutsResult = usersManage.deactivate();

        verify(userManagementService).deactivate(user, permissions);
        assertTrue(usersManage.getActionMessages().contains("UserInactivated"));
        assertEquals(Action.SUCCESS, strutsResult);
    }

    @Test
    public void testDeactivate_NotOkAddsMessageAndRedirects() throws Exception {
        when(i18nCache.getText(eq("NotOkToDeactivate"), eq(Locale.ENGLISH), anyVararg())).thenReturn("NotOkToDeactivate");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);
        when(user.isGroup()).thenReturn(false);
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        status.isOk = false;
        status.notOkErrorKey = "NotOkToDeactivate";
        when(userManagementService.userIsDeactivatable(user, account)).thenReturn(status);


        String strutsResult = usersManage.deactivate();

        verify(userManagementService, never()).deactivate(user, permissions);
        assertTrue(usersManage.getActionErrors().contains("NotOkToDeactivate"));
        assertEquals(Action.SUCCESS, strutsResult);
    }

    @Test
    public void testActivate_Happy() throws Exception {
        when(i18nCache.getText(eq("UsersManage.UserActivated"), eq(Locale.ENGLISH), anyVararg())).thenReturn("UserActivated");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);

        String strutsResult = usersManage.activate();

        verify(userManagementService).activateUser(user);
        assertEquals("UsersManage.action?account=456&user=123", usersManage.getUrl());
        assertEquals(PicsActionSupport.REDIRECT, strutsResult);
        verify(emailQueueDAO).removeEmailAddressExclusions(anyString());
    }

    @Test
    public void testDelete_ProxiesToServiceAddsMessageAndRedirects() throws Exception {
        when(i18nCache.getText(eq("UsersManage.SuccessfullyRemoved"), eq(Locale.ENGLISH), anyVararg())).thenReturn("SuccessfullyRemoved");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);
        when(user.isGroup()).thenReturn(false);
        when(userManagementService.userIsDeletable(user)).thenReturn(new UserGroupManagementStatus());

        String strutsResult = usersManage.delete();

        verify(userManagementService).userIsDeletable(user);
        assertTrue(usersManage.getActionMessages().contains("SuccessfullyRemoved"));
        assertEquals(Action.SUCCESS, strutsResult);
    }

    @Test
    public void testDelete_AddsErrorMessageAndRedirects_NotOk() throws Exception {
        when(i18nCache.getText(eq("NotOkToDelete"), eq(Locale.ENGLISH), anyVararg())).thenReturn("NotOkToDelete");
        when(user.getId()).thenReturn(123);
        when(account.getId()).thenReturn(456);
        usersManage.setUser(user);
        usersManage.setAccount(account);
        when(user.getAccount()).thenReturn(account);
        when(user.isGroup()).thenReturn(false);
        UserGroupManagementStatus status = new UserGroupManagementStatus();
        status.isOk = false;
        status.notOkErrorKey = "NotOkToDelete";
        when(userManagementService.userIsDeletable(user)).thenReturn(status);

        String strutsResult = usersManage.delete();

        verify(userManagementService, never()).delete(user, permissions);
        assertTrue(usersManage.getActionErrors().contains("NotOkToDelete"));
        assertEquals(Action.SUCCESS, strutsResult);
    }

    @Test
    public void testDelete_UserNotGroup_DeletesUserThatIsNotThePrimary() throws Exception {
        User primaryContact = mock(User.class);
        when(user.isGroup()).thenReturn(false);
        when(user.getAccount()).thenReturn(account);
        when(account.getPrimaryContact()).thenReturn(primaryContact);
        usersManage.setAccount(account);
        usersManage.setUser(user);
        when(userManagementService.userIsDeletable(user)).thenReturn(new UserGroupManagementStatus());

        usersManage.delete();

        verify(userManagementService).delete(user, permissions);
    }

    @Test
    public void testAdd_ProxiesToUserManagementServiceAndSetsInstanceVariable() throws Exception {
        when(userManagementService.initializeNewUser(any(Account.class))).thenReturn(user);
        when(permissions.getAccountId()).thenReturn(12345);
        when(account.getId()).thenReturn(12345);
        when(accountDAO.find(12345)).thenReturn(account);
        usersManage.setAccount(account);
        usersManage.setUserIsGroup(YesNo.No);
        usersManage.add();
        User userSetAfterInitialization = usersManage.getUser();

        assertThat(userSetAfterInitialization, is(equalTo(user)));
    }

	@Test
	public void testSetUserResetHash() throws Exception {
		user = new User();
		Whitebox.setInternalState(usersManage, "user", user);
		Whitebox.invokeMethod(usersManage, "setUserResetHash");
		assertNotNull(user.getResetHash());
		verify(userDAO).save(user);
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

	@Test
	public void testExecute_NoException() throws Exception {
		Account account = new Account();
		User user = new User(INVALID_USER_ID);
		user.setAccount(account);
		usersManage.setAccount(account);
		usersManage.setUser(user);

		usersManage.execute();
	}

	@Test
	public void testSave_ShouldCallValidateInput() throws Exception {
		setUpUserAndAccount();
		UsersManage usersManageSpy = spy(usersManage);

		usersManageSpy.save();

		verify(usersManageSpy).validateInputAndRecordErrors();
		verify(inputValidator).validateFirstName(anyString());
		verify(inputValidator).validateLastName(anyString());
		verify(inputValidator).validateEmail(anyString());
		verify(inputValidator).validateUsername(anyString());
		verify(inputValidator).validateUsernameAvailable(anyString(), anyInt());
		verify(inputValidator, times(2)).validatePhoneNumber(anyString(), anyBoolean());
		verify(inputValidator).validateLocale((Locale) any());
	}

	@Test
	public void testAddCountry_Duplicated() {
		List<String> countriesServiced = new ArrayList<String>();
		countriesServiced.add("US");

		when(user.getCountriesServiced()).thenReturn(countriesServiced);

		usersManage.setUser(user);
		usersManage.setSelectedCountry("US");

		assertEquals(PicsActionSupport.SUCCESS, usersManage.addCountry());
		assertFalse(usersManage.hasActionMessages());
		assertTrue(usersManage.hasActionErrors());

		verify(userDAO, never()).save(user);
	}

	@Test
	public void testAddCountry_NotDuplicated() {
		usersManage.setUser(user);
		usersManage.setSelectedCountry("US");

		assertEquals(PicsActionSupport.SUCCESS, usersManage.addCountry());
		assertFalse(usersManage.hasActionErrors());
		assertTrue(usersManage.hasActionMessages());

		verify(userDAO).save(user);
	}

	@Test
	public void testRemoveCountry() throws Exception {
		when(user.getCountriesServiced()).thenReturn(new ArrayList<String>() {{
			add("US");
		}});

		Account account = new Account();
		account.setId(1);

		when(user.getAccount()).thenReturn(account);
		when(permissions.getAccountId()).thenReturn(1);

		usersManage.setAccount(account);
		usersManage.setUser(user);
		usersManage.setRemoveCountry("US");

		assertEquals(PicsActionSupport.REDIRECT, usersManage.removeCountry());

		assertEquals(0, user.getCountriesServiced().size());
	}

	private void setUpUserAndAccount() {
		// Just seed some data so we don't get NPEs when calling functions
		User user = new User(123);
		user.setName("USER NAME");
		user.setUsername("USERNAME");
		user.setEmail("me@here.com");
		Account account = new Account();
		user.setAccount(account);
		usersManage.setUser(user);
		usersManage.setAccount(account);
	}

}
