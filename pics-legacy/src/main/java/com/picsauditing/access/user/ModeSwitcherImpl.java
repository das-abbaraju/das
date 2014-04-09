package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;

final class ModeSwitcherImpl {

	public final void switchMode(final Permissions permissions, final UserMode switchToMode)
			throws InvalidModeException {

		if (!permissions.getAvailableUserModes().contains(switchToMode)) {
			throw new InvalidModeException();
		}

		permissions.setCurrentMode(switchToMode);
	}

}
