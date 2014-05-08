package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.service.user.UserService;
import com.picsauditing.web.NameSpace;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class UserModeProviderImplTest {

	public static final int APP_USER_ID = 123;
	UserModeProvider userModeProvider;

	@Mock
	private ProductSubscriptionService productSubscriptionService;
	@Mock
	private SessionInfoProvider sessionInfoProvider;
	@Mock
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userModeProvider = new UserModeProviderImpl();

		Whitebox.setInternalState(userModeProvider, "productSubscriptionService", productSubscriptionService);
		Whitebox.setInternalState(userModeProvider, "userService", userService);
		org.powermock.reflect.Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
	}

	@Test
	public void testGetAvailableUserModes() throws Exception {
		setupTestGetAvailableUserModes();

		Set<UserMode> results = userModeProvider.getAvailableUserModes(APP_USER_ID);

		verifyTestGetAvailableUserModes(results);
	}

	private void setupTestGetAvailableUserModes() {
		when(productSubscriptionService.isEmployeeGUARDEmployeeUser(APP_USER_ID)).thenReturn(true);
		when(userService.findByAppUserId(APP_USER_ID)).thenReturn(new User());
	}

	private void verifyTestGetAvailableUserModes(Set<UserMode> results) {
		assertTrue(results.contains(UserMode.ADMIN));
		assertTrue(results.contains(UserMode.EMPLOYEE));
	}

	@Test
	public void testGetCurrentUserMode_ADMIN_Mode() throws Exception {
		when(sessionInfoProvider.getNamespace()).thenReturn(NameSpace.PICSORG);

		UserMode result = userModeProvider.getCurrentUserMode(new Permissions());

		assertEquals(UserMode.ADMIN, result);
	}

	@Test
	public void testGetCurrentUserMode_Employee_Mode() throws Exception {
		Permissions fakePermissions = setupTestGetCurrentUserMode_Employee_Mode();

		UserMode result = userModeProvider.getCurrentUserMode(fakePermissions);

		assertEquals(UserMode.EMPLOYEE, result);
	}

	private Permissions setupTestGetCurrentUserMode_Employee_Mode() {
		Permissions fakePermissions = new Permissions();
		fakePermissions.setAvailableUserModes(new HashSet<UserMode>() {{
			add(UserMode.EMPLOYEE);
		}});

		when(sessionInfoProvider.getNamespace()).thenReturn(NameSpace.EMPLOYEEGUARD);

		return fakePermissions;
	}
}
