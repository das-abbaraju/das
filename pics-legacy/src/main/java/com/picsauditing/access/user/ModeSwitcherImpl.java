package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;
import com.picsauditing.web.SessionInfoProviderFactory;

final class ModeSwitcherImpl implements ModeSwitcher {

	public final void switchMode(final UserMode switchToMode)
			throws InvalidModeException {

		Permissions permissions = SessionInfoProviderFactory.getSessionInfoProvider().getPermissions();

		if (!permissions.getAvailableUserModes().contains(switchToMode)) {
			throw new InvalidModeException("User does not have permission to switch to mode.");
		}

		permissions.setCurrentMode(switchToMode);

		SessionInfoProviderFactory.getSessionInfoProvider()
				.putInSession(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
	}

}
