package com.picsauditing.authentication.service;

import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.entities.builder.AppUserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AppUserServiceTest {

	private static final String AVAILABLE_USER_NAME = "Available User Name";
	private static final String UNAVAILABLE_USER_NAME = "Unavailable User Name";
	public static final AppUser FAKE_APP_USER = new AppUserBuilder()
			.username(UNAVAILABLE_USER_NAME)
			.build();
	private static final String PASSWORD = "password";
	private static final int APP_USER_ID = 890;
	private static final int APP_USER_ID_TO_TEST_DAO_CALLS = 214;

	// Class under test
	private AppUserService appUserService;

	@Mock
	private AppUserDAO appUserDAO;

	@Before
	public void setUp() throws Exception {
		appUserService = new AppUserService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(appUserService, "appUserDAO", appUserDAO);

		when(appUserDAO.findByUserName(AVAILABLE_USER_NAME)).thenReturn(null);
		when(appUserDAO.findByUserName(UNAVAILABLE_USER_NAME)).thenReturn(FAKE_APP_USER);

		when(appUserDAO.findById(APP_USER_ID_TO_TEST_DAO_CALLS)).thenReturn(new AppUserBuilder()
				.id(APP_USER_ID_TO_TEST_DAO_CALLS)
				.build());
	}

	@Test
	public void isUserNameAvailable_UsernameIsAvailable() {
		boolean result = appUserService.isUserNameAvailable(AVAILABLE_USER_NAME);

		assertTrue(result);
	}

	@Test
	public void isUserNameAvailable_UsernameIsNotAvailable() {
		boolean result = appUserService.isUserNameAvailable(UNAVAILABLE_USER_NAME);

		assertFalse(result);
	}

	@Test
	public void testGenerateNewAppUser() {
		setupTestGenerateNewAppUser();

		AppUser appUser = appUserService.generateNewAppUser("user", "password");

		verifyTestGenerateNewAppUser(appUser);
	}

	private void setupTestGenerateNewAppUser() {
		final int numberOfCalls = 0;
		when(appUserDAO.save(any(AppUser.class))).thenAnswer(new Answer<AppUser>() {

			@Override
			public AppUser answer(InvocationOnMock invocationOnMock) throws Throwable {
				AppUser appUser = (AppUser) invocationOnMock.getArguments()[0];
				if (numberOfCalls == 0) {
					appUser.setId(APP_USER_ID);
				}

				return appUser;
			}
		});
	}

	private void verifyTestGenerateNewAppUser(AppUser appUser) {
		verify(appUserDAO, times(2)).save(any(AppUser.class));
		assertEquals(APP_USER_ID, appUser.getId());
		assertEquals(Integer.toString(APP_USER_ID), appUser.getHashSalt());
		assertNotNull(appUser.getPassword());
	}

	@Test
	public void testFindById() {
		AppUser result = appUserService.findById(APP_USER_ID_TO_TEST_DAO_CALLS);

		assertEquals(APP_USER_ID_TO_TEST_DAO_CALLS, result.getId());
		verify(appUserDAO).findById(APP_USER_ID_TO_TEST_DAO_CALLS);
	}

	@Test
	public void testFindByUsername() {
		AppUser result = appUserService.findByUsername(UNAVAILABLE_USER_NAME);

		assertEquals(UNAVAILABLE_USER_NAME, result.getUsername());
		verify(appUserDAO).findByUserName(UNAVAILABLE_USER_NAME);
	}

	@Test
	public void testFindByUsernameAndUnencodedPassword() {
		when(appUserDAO.findByUserNameAndPassword(anyString(), anyString())).thenReturn(FAKE_APP_USER);

		AppUser result = appUserService.findByUsernameAndUnencodedPassword(UNAVAILABLE_USER_NAME, PASSWORD);

		verifyTestFindByUsernameAndUnencodedPassword(result);
	}

	private void verifyTestFindByUsernameAndUnencodedPassword(AppUser result) {
		assertEquals(UNAVAILABLE_USER_NAME, result.getUsername());
		verify(appUserDAO).findByUserName(UNAVAILABLE_USER_NAME);
		verify(appUserDAO).findByUserNameAndPassword(anyString(), anyString());
	}

	@Test
	public void testSave() {
		appUserService.save(FAKE_APP_USER);

		verify(appUserDAO).save(FAKE_APP_USER);
	}
}
