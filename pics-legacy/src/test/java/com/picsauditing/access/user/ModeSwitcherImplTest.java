package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ModeSwitcherImplTest {

	@Mock
	private Permissions permissions;
	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Before
	public void setUp() {
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);

		when(sessionInfoProvider.getPermissions()).thenReturn(permissions);
	}

	@Test(expected = InvalidModeException.class)
	public void testSwitchMode_InvalidMode() throws Exception {
		ModeSwitcher modeSwitcher = new ModeSwitcherImpl();

		modeSwitcher.switchMode(UserMode.EMPLOYEE);
	}

	@Test
	public void testSwitchMode() throws Exception {
		ModeSwitcher modeSwitcher = new ModeSwitcherImpl();

		modeSwitcher.switchMode(UserMode.EMPLOYEE);

		assertEquals(UserMode.EMPLOYEE, permissions.getCurrentMode());
	}

	private void setupMockPermissions() {
		when(permissions.getCurrentMode()).thenReturn(UserMode.ADMIN);
		when(permissions.getAvailableUserModes())
				.thenReturn(new HashSet<>(Arrays.asList(UserMode.ADMIN, UserMode.EMPLOYEE)));
	}
}
