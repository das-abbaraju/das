package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Anonymous;
import com.picsauditing.controller.PicsRestActionSupport;

public class AngularLoaderAction extends PicsRestActionSupport {

	public String load() {
		return BLANK;
	}

	@Anonymous
	public String anonymousLoad() {
		return BLANK;
	}

}
