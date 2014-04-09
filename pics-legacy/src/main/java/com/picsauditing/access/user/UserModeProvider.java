package com.picsauditing.access.user;

import java.util.Set;

public interface UserModeProvider {

	Set<UserMode> getAvailableUserModes(int appUserId);

}
