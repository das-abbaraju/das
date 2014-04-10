package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class ModeSwitcherImplTest {

	@Test(expected = InvalidModeException.class)
	public void testSwitchMode_InvalidMode() throws Exception {
		ModeSwitcher modeSwitcher = new ModeSwitcherImpl();

		modeSwitcher.switchMode(UserMode.EMPLOYEE);
	}

	@Test
	public void testSwitchMode() throws Exception {
		ModeSwitcher modeSwitcher = new ModeSwitcherImpl();

		Permissions permissions = buildFakePermissions();

		modeSwitcher.switchMode(UserMode.EMPLOYEE);

		assertEquals(UserMode.EMPLOYEE, permissions.getCurrentMode());
	}

	private Permissions buildFakePermissions() {
		Permissions permissions = new Permissions();

		permissions.setCurrentMode(UserMode.ADMIN);
		permissions.setAvailableUserModes(new HashSet<>(Arrays.asList(UserMode.ADMIN, UserMode.EMPLOYEE)));

		return permissions;
	}
}
