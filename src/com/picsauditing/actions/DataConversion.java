package com.picsauditing.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.util.AppVersion;
import com.picsauditing.util.Strings;

/**
 * This is a generic data conversion utility that we should use when releasing.
 * 
 * This should be called once when releasing.
 */
@SuppressWarnings("serial")
public class DataConversion extends PicsActionSupport {
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	@Anonymous
	public String execute() throws Exception {
		if (applicationNeedsUpgrade()) {
			addActionMessage("Database needs upgrading to " + AppVersion.current.getVersion());
			return SUCCESS;
		} else {
			addAlertMessage("Application is already up to date");
			return BLANK;
		}
	}

	private boolean applicationNeedsUpgrade() {
		String versionMajor = appPropertyDAO.getProperty("VERSION.major");
		if (Strings.isEmpty(versionMajor))
			return true;

		String versionMinor = appPropertyDAO.getProperty("VERSION.minor");
		if (Strings.isEmpty(versionMajor))
			return true;

		if (AppVersion.current.greaterThan(Integer.parseInt(versionMajor), Integer.parseInt(versionMinor))) {
			return true;
		}
		return false;
	}

	@Anonymous
	public String upgrade() throws Exception {
		if (!applicationNeedsUpgrade()) {
			addActionError("Application is already up to date");
			return BLANK;
		}
		long startTime = System.currentTimeMillis();
		runDataConversion();
		updateDatabaseVersions();
		long endTime = System.currentTimeMillis();
		addActionMessage("Data conversion completed successfully in " + (endTime - startTime) + " ms");
		return BLANK;
	}

	private void updateDatabaseVersions() {
		appPropertyDAO.setProperty("VERSION.major", AppVersion.current.getMajor() + "");
		appPropertyDAO.setProperty("VERSION.minor", AppVersion.current.getMinor() + "");
	}

	/*
	 * For each release of PICS Organizer clear this out and write a new version
	 */
	private void runDataConversion() {
	}
}
