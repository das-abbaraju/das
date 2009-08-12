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

@SuppressWarnings("serial")
public class CertHashUtil extends ContractorActionSupport {
	private CertificateDAO certificateDAO;

	List<Certificate> certs;
	int num = 10;
	int count = 0;

	public CertHashUtil(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
	}

	public String execute() throws Exception {

		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.DevelopmentEnvironment);

		certs = certificateDAO.findWhere("fileHash IS NULL", num);
		for (Certificate cert : certs) {
			File[] files = getFiles(cert.getId());

			File file = null;
			if (files.length > 0)
				file = files[0];

			cert.setFileHash(FileUtils.getFileMD5(file));

			for (ContractorAuditOperator cao : cert.getCaos()) {
				if (cert.getExpirationDate() == null
						|| (cao.getAudit().getExpiresDate() != null && cert.getExpirationDate().before(
								cao.getAudit().getExpiresDate())))
					cert.setExpirationDate(cao.getAudit().getExpiresDate());
			}

			if (cert.getExpirationDate() == null) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, 6);
				cert.setExpirationDate(cal.getTime());
			}

			certificateDAO.save(cert);
			count++;
		}

		addActionMessage("Successfully updated " + count + " certificates");
		return SUCCESS;
	}

	private String getFileName(int certID) {
		return PICSFileType.certs + "_" + certID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(certID));
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
