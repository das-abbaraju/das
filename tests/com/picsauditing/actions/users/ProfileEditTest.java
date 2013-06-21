package com.picsauditing.actions.users;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileEditTest extends PicsActionTest {
	private ProfileEdit profileEdit;

	@Mock
	private User user;
	@Mock
	private UserDAO userDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();
		super.setupMocks();

		profileEdit = new ProfileEdit();

		Whitebox.setInternalState(profileEdit, "userDAO", userDAO);
	}

	@Test
	public void testVersion6Menu() throws Exception {
		profileEdit.setU(user);

		when(request.getHeader("Referer")).thenReturn("/Home.action");

		assertEquals(PicsActionSupport.REDIRECT, profileEdit.version6Menu());

		verify(user).setUsingDynamicReports(false);
		verify(user).setUsingVersion7Menus(false);
		verify(userDAO).save(user);
	}

	@Test
	public void testVersion7Menu() throws Exception {
		profileEdit.setU(user);

		when(request.getHeader("Referer")).thenReturn("/Home.action");

		assertEquals(PicsActionSupport.REDIRECT, profileEdit.version7Menu());

		verify(user).setUsingDynamicReports(true);
		verify(user).setusingDynamicReportsDate(any(Date.class));
		verify(user).setUsingVersion7Menus(true);
		verify(user).setUsingVersion7MenusDate(any(Date.class));
		verify(userDAO).save(user);
	}
}
