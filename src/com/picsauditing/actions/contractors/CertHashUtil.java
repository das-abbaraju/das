package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class CertHashUtil extends ContractorActionSupport {
	private CertificateDAO certificateDAO;

	List<Certificate> certs;
	int num = 10;

	public CertHashUtil(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
	}

	public String execute() throws Exception {

		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.DevelopmentEnvironment);
		PicsLogger.getRules().add(new LoggingRule("CertCleanUp", true));
		PicsLogger.start("CertCleanUp",
				"Starting certificate hash data conversion");

		certs = certificateDAO.findWhere("fileHash IS NULL", num);
		for (Certificate cert : certs) {
			PicsLogger.log(" starting process for cert.id=" + cert.getId());
			File[] files = getFiles(cert.getId());

			// we are going to remove any other files that are not associated
			// with the
			// certificate object
			File file = null;
			for (File f : files) {
				if (f.getName().endsWith(cert.getFileType())) {
					file = f;
					PicsLogger.log(" found the matching file extension - "
							+ file.getName());
				} else {
					PicsLogger.log(" found file with different extension - moving " + f.getName());
					FileUtils.moveFile(f, getFtpDir() + "/cert_cleanup/");
				}
			}

			// if file is null the hash will just be null
			cert.setFileHash(FileUtils.getFileMD5(file));
			PicsLogger.log(" fileHash set to " + cert.getFileHash());

			if (cert.getExpirationDate() == null) {
				PicsLogger
						.log(" expirationDate not found - setting to 6 months in the future");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, 6);
				cert.setExpirationDate(cal.getTime());
			} else
				PicsLogger.log(" expirationDate set to "
						+ cert.getExpirationDate());

			certificateDAO.save(cert);
		}

		addActionMessage("Successfully updated " + certs.size()
				+ " certificates");
		PicsLogger.stop();
		PicsLogger.getRules().remove(new LoggingRule("CertCleanUp", true));
		return SUCCESS;
	}

	private String getFileName(int certID) {
		return PICSFileType.certs + "_" + certID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/"
				+ FileUtils.thousandize(certID));
		return FileUtils.getSimilarFiles(dir, getFileName(certID));
	}

	public List<Certificate> getCerts() {
		return certs;
	}

	public void setCerts(List<Certificate> certs) {
		this.certs = certs;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
