/**
 *
 *
 * User: aphatarphekar
 * Date: 7/17/2014
 * Time: 2:56 PM
 *
 */
package com.picsauditing.employeeguard.util;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

import com.picsauditing.service.AppPropertyService;
import com.picsauditing.util.system.PicsEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

public class PicsEnvironmentUtil {

	@Autowired
	private AppPropertyService appPropertyService;

	private PicsEnvironment picsEnvironment;

	public PicsEnvironmentUtil() {
	}

	private PicsEnvironment getPicsEnvInstance(){
		if(picsEnvironment==null)
			picsEnvironment = new PicsEnvironment(appPropertyService.getPropertyString(AppProperty.VERSION_MAJOR),
						appPropertyService.getPropertyString(AppProperty.VERSION_MINOR));

		return picsEnvironment;
	}

	public String getPicsEnvironment() {

		return getPicsEnvInstance().getEnvironment();
	}

	public boolean isLocalhostEnvironment() {
		return getPicsEnvInstance().isLocalhost();
	}
}

 