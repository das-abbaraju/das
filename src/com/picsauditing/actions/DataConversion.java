package com.picsauditing.actions;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
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
		long startTime = System.currentTimeMillis();
		convertEmployeeGuard();
		updateDatabaseVersions();
		long endTime = System.currentTimeMillis();
		addActionMessage("Data conversion completed successfully in " + (endTime - startTime) + " ms");
		return BLANK;
	}

	private void updateDatabaseVersions() {
		appPropertyDAO.setProperty("VERSION.major", PicsOrganizerVersion.major + "");
		appPropertyDAO.setProperty("VERSION.minor", PicsOrganizerVersion.minor + "");
	}

	private void convertEmployeeGuard() {
		List<ContractorAudit> auditList = dao.findWhere(ContractorAudit.class, "auditType.id IN (17,29)");
		for (ContractorAudit conAudit : auditList) {
			System.out.println(" Converting: " + conAudit.getId() + " " + conAudit.getAuditType().getName() + " "
					+ conAudit.getAuditFor());
			if (conAudit.getAuditType().getId() == 17)
				convertIntegrityManagementAudit(conAudit);
			if (conAudit.getAuditType().getId() == 29)
				convertImplementationAuditPlusAudit(conAudit);

		}
	}

	private void convertImplementationAuditPlusAudit(ContractorAudit conAudit) {

	}

	private void convertIntegrityManagementAudit(ContractorAudit conAudit) {

	}

	private String getFileName(int certID) {
		return PICSFileType.certs + "_" + certID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(certID));
		return FileUtils.getSimilarFiles(dir, getFileName(certID));
	}

}
