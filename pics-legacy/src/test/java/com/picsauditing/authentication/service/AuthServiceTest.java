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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class AuthServiceTest extends PicsActionTest {
	private AuthService authService;
	@Mock
	private AppUserDAO appUserDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		authService = new AuthService();

		PicsTestUtil.autowireDAOsFromDeclaredMocks(authService, this);

		Whitebox.setInternalState(authService, "appUserDAO", appUserDAO);
	}

	@Test
	public void testCreateNewAppUserNullUsername() throws Exception {
		authService.setUsername(null);
		authService.setPassword("blah");

		assertEquals(PicsActionSupport.JSON, authService.createNewAppUser());

		verify(appUserDAO, never()).save(any(AppUser.class));
	}
}
