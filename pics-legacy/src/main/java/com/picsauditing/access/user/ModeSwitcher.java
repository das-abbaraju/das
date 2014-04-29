package com.picsauditing.access.user;

public interface ModeSwitcher {

	/**
	 * Responsible for switching from the user's current mode to their new mode.
	 *
	 * @param switchToMode The mode the user wants to switch to
	 * @throws InvalidModeException Thrown if the user cannot switch to that mode
	 */
	void switchMode(UserMode switchToMode) throws InvalidModeException;

}
