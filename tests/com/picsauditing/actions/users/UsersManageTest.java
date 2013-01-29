package com.picsauditing.actions.users;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import com.picsauditing.validator.InputValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UsersManageTest extends PicsActionTest {

	private static int NOT_GROUP_CSR = User.GROUP_CSR++;
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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		usersManage = new UsersManage();
		super.setUp(usersManage);

		Whitebox.setInternalState(usersManage, "userDAO", userDAO);
		Whitebox.setInternalState(usersManage, "inputValidator", inputValidator);
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

		verify(usersManageSpy).validateInput();
		verify(inputValidator).validateName(anyString());
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
