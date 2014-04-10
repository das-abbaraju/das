package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;

final class ModeSwitcherImpl implements ModeSwitcher {

	public final void switchMode(final Permissions permissions, final UserMode switchToMode)
			throws InvalidModeException {

		if (!permissions.getAvailableUserModes().contains(switchToMode)) {
			throw new InvalidModeException("User does not have permission to switch to mode.");
		}

		permissions.setCurrentMode(switchToMode);
	}

}
