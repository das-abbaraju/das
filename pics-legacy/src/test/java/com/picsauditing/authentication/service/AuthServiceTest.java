package com.picsauditing.authentication.service;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest extends PicsActionTest {
	private AuthService authService;
	@Mock
	private AppUserDAO appUserDAO;
	@Mock
	private AppUser appUser;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		authService = new AuthService();
		appUser.setId(1);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(authService, this);

		Whitebox.setInternalState(authService, "appUserDAO", appUserDAO);
		when(appUserDAO.save(any(AppUser.class))).thenReturn(appUser);
	}

	@Test
	public void testCreateNewAppUserNullUsername() throws Exception {
		authService.setUsername(null);
		authService.setPassword("blah");

		assertEquals(PicsActionSupport.JSON, authService.createNewAppUser());

		verify(appUserDAO, never()).save(any(AppUser.class));
	}

	@Test
	public void testCreateNewAppUserDuplicateUsername() throws Exception {
		authService.setUsername("username");
		authService.setPassword("password");

		when(appUserDAO.findListByUserName(anyString())).thenReturn(Arrays.asList(new AppUser(), new AppUser()));

		authService.createNewAppUser();

		assertEquals("FAIL", authService.getJson().get("status"));
	}

	@Test
	public void testCreateNewAppUserEmptyPassword() throws Exception {
		authService.setUsername("username");
		authService.setPassword("");

		authService.createNewAppUser();

		assertEquals("FAIL", authService.getJson().get("status"));
		verify(appUserDAO, never()).save(any(AppUser.class));
	}

	@Test
	public void testCreateNewAppUser() throws Exception {
		authService.setUsername("username");
		authService.setPassword("password");

		authService.createNewAppUser();

		assertEquals("SUCCESS", authService.getJson().get("status"));
		verify(appUserDAO, times(2)).save(any(AppUser.class));
	}

	@Test
	public void testCheckUsername() throws Exception {
		when(appUserDAO.findListByUserName(anyString())).thenReturn(Arrays.asList(new AppUser(), new AppUser()));
		authService.checkUserName();

		assertEquals("Taken", authService.getJson().get("status"));
	}
}
