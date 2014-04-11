package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ModeSwitcherImplTest {

	private ModeSwitcher modeSwitcher;

	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		modeSwitcher = new ModeSwitcherImpl();

		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
	}

	@Test(expected = InvalidModeException.class)
	public void testSwitchMode_InvalidMode() throws Exception {
		when(sessionInfoProvider.getPermissions()).thenReturn(new Permissions());

		modeSwitcher.switchMode(UserMode.EMPLOYEE);
	}

	@Test
	public void testSwitchMode() throws Exception {
		Permissions permissions = setupTestSwitchMode();

		modeSwitcher.switchMode(UserMode.EMPLOYEE);

		verifyTestSwitchMode(permissions);
	}

	private Permissions setupTestSwitchMode() {
		Permissions permissions = buildFakePermissions();
		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
		return permissions;
	}

	private void verifyTestSwitchMode(Permissions permissions) {
		assertEquals(UserMode.EMPLOYEE, permissions.getCurrentMode());
		verify(sessionInfoProvider).putInSession(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
		verify(sessionInfoProvider).getPermissions();
	}

	private Permissions buildFakePermissions() {
		Permissions permissions = new Permissions();

		permissions.setCurrentMode(UserMode.ADMIN);
		permissions.setAvailableUserModes(new HashSet<>(Arrays.asList(UserMode.ADMIN, UserMode.EMPLOYEE)));

		return permissions;
	}

}
