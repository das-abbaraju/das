package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class CertificateUtility extends ContractorActionSupport {
	private CertificateDAO certificateDAO;

	private int num = 10;
	private int count = 0;

	List<Integer> deleted = new ArrayList<Integer>();

	public CertificateUtility(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.DevelopmentEnvironment);

		PicsLogger.start("CertificateConvert", "Beggining certificate cleanup");

		List<String> dupeHashes = certificateDAO.findDupeHashes(num);
		for (String hash : dupeHashes) {

			PicsLogger.log(" start hash " + hash);

			Map<ContractorAccount, List<Certificate>> conCerts = certificateDAO.findConCertMap(hash);

			if (conCerts.size() > 1)
				PicsLogger.log(" found " + conCerts.size() + " different contractors that use this file");

			for (List<Certificate> certsList : conCerts.values()) {
				PicsLogger.log(" this contractor has " + certsList.size() + " certificates");
				Certificate keeper = certsList.get(0);
				PicsLogger.log(" keeping the certificate with id=" + keeper.getId());
				for (int i = 1; i < certsList.size(); i++) {
					PicsLogger.log(" start removal for certificate with id=" + certsList.get(i).getId());
					for (ContractorAuditOperator cao : certsList.get(i).getCaos()) {
						PicsLogger.log("  reassigning cao for " + cao.getOperator().getName());
						cao.setCertificate(keeper);
					}
					File[] files = getFiles(certsList.get(i).getId());
					PicsLogger.log(" found " + files.length + " files for certificate id=" + certsList.get(i).getId());
					for (File file : files)
						FileUtils.deleteFile(file);
					deleted.add(certsList.get(i).getId());
					certificateDAO.remove(certsList.get(i));
					count++;
				}
			}

		}

		addActionMessage("Deleted " + count + " certificates");
		return SUCCESS;
	}

	private String getFileName(int certID) {
		return PICSFileType.certs + "_" + certID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(certID));
		return FileUtils.getSimilarFiles(dir, getFileName(certID));
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
