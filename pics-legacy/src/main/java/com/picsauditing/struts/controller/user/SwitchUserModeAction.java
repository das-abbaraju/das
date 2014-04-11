package com.picsauditing.struts.controller.user;

import com.picsauditing.access.user.ModeSwitcher;
import com.picsauditing.access.user.UserMode;
import com.picsauditing.controller.PicsRestActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SwitchUserModeAction extends PicsRestActionSupport {

	@Autowired
	private ModeSwitcher modeSwitcher;

	private String mode;

	public String changeMode() throws IOException {
		UserMode userMode = UserMode.mapUserModeFromValue(mode);
		modeSwitcher.switchMode(userMode);

		return setUrlForRedirect(userMode.getHomeUrl());
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
