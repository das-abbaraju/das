package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;

public interface ModeSwitcher {

	/**
	 * Responsible for switching from the user's current mode to their new mode.
	 *
	 * @param permissions Permissions object from the Sessions
	 * @param switchToMode The mode the user wants to switch to
	 * @throws InvalidModeException Thrown if the user cannot switch to that mode
	 */
	void switchMode(final Permissions permissions, final UserMode switchToMode) throws InvalidModeException;

}
