package com.picsauditing.actions;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PicsOrganizerVersion;
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
			addActionMessage("Database needs upgrading to " + PicsOrganizerVersion.getVersion());
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
		
		if (PicsOrganizerVersion.greaterThan(Integer.parseInt(versionMajor), Integer.parseInt(versionMinor))) {
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
		sampleUpdate1();
		updateDatabaseVersions();
		addActionMessage("Data conversion completed successfully");
		return BLANK;
	}

	private void updateDatabaseVersions() {
		appPropertyDAO.setProperty("VERSION.major", PicsOrganizerVersion.major + "");
		appPropertyDAO.setProperty("VERSION.minor", PicsOrganizerVersion.minor + "");
	}

	private void sampleUpdate1() {
		// TODO Auto-generated method stub
		
//		File[] files = getFiles(certs.get(i).getId());
//		PicsLogger.log("  found " + files.length
//				+ " files for certificate id="
//				+ certs.get(i).getId());
//		for (File file : files)
//			FileUtils.moveFile(file, getFtpDir() + "/cert_cleanup/");

	}
	
	private String getFileName(int certID) {
		return PICSFileType.certs + "_" + certID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/"
				+ FileUtils.thousandize(certID));
		return FileUtils.getSimilarFiles(dir, getFileName(certID));
	}
	
}
