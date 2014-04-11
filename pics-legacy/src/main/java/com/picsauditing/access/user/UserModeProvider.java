package com.picsauditing.access.user;

import java.util.Set;

public interface UserModeProvider {

	/**
	 * Returns all the available user modes the current user has access to. For example, the if a PICSORG user
	 * has access to PICSORG and is an Employee in EmployeeGUARD, this method will return both of those UserModes.
	 *
	 * @param appUserId The appUserId of the currently logged in user
	 * @return
	 */
	Set<UserMode> getAvailableUserModes(int appUserId);

	/**
	 * Returns the current user mode based on the entry point to the application (EmployeeGUARD or PICSORG)
	 *
	 * @return
	 */
	UserMode getCurrentUserMode();

}
