package com.picsauditing.actions;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;

public class ReferenceTest extends PicsActionTest {

	Reference reference;

	@Mock
	private Permissions permissions;
	@Mock
	private UserDAO userDAO;
	@Mock
	private FeatureToggle featureToggle;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		reference = new Reference();
		super.setUp(reference);

		reference.permissions = permissions;

		PicsTestUtil.autowireDAOsFromDeclaredMocks(reference, this);
		Whitebox.setInternalState(reference, "featureToggle", featureToggle);
	}

	@Test
	public void testExecute_UpdateFirstTimeUser() throws Exception {
		when(permissions.isUsingVersion7Menus()).thenReturn(true);
		when(permissions.getUsingVersion7MenusDate()).thenReturn(null);
		when(userDAO.find(anyInt())).thenReturn(new User());

		String result = reference.navigationMenu();

		verify(permissions, times(1)).setUsingVersion7MenusDate(any(Date.class));
		verify(userDAO, times(1)).save(any(User.class));
		assertEquals("navigation-menu", result);
	}

	@Test
	public void testExecute_NotUpdateFirstTimeUser() throws Exception {
		when(permissions.isUsingVersion7Menus()).thenReturn(true);
		when(permissions.getUsingVersion7MenusDate()).thenReturn(new Date());

		String result = reference.navigationMenu();

		verifyZeroInteractions(userDAO);
		verify(permissions, never()).setUsingVersion7MenusDate(any(Date.class));
		assertEquals("navigation-menu", result);
	}

}
