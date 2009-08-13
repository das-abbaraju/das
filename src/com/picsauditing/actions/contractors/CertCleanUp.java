package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.log.LoggingRule;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class CertCleanUp extends ContractorActionSupport {
	private ContractorAuditOperatorDAO caoDAO;
	private CertificateDAO certificateDAO;

	private int num = 10;
	private int count = 0;

	public CertCleanUp(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, ContractorAuditOperatorDAO caoDAO,
			CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.caoDAO = caoDAO;
		this.certificateDAO = certificateDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.DevelopmentEnvironment);
		PicsLogger.getRules().add(new LoggingRule("CertCleanUp", true));
		PicsLogger.start("CertCleanUp", "Starting certificate clean up");

		List<String> dupeHashes = certificateDAO.findDupeHashes(num);
		for (String hash : dupeHashes) {

			PicsLogger.log("For hash " + hash + ":");

			Map<ContractorAccount, List<Certificate>> conCerts = certificateDAO
					.findConCertMap(hash);

			if (conCerts.size() > 1)
				PicsLogger.log(" found " + conCerts.size()
						+ " different contractors that use this file");

			for (Map.Entry<ContractorAccount, List<Certificate>> entry : conCerts
					.entrySet()) {
				List<Certificate> certs = entry.getValue();
				PicsLogger.log(" Contractor.id=" + entry.getKey().getId()
						+ " has " + certs.size() + " duplicate certificates");

				Certificate keeper = certs.get(0);
				PicsLogger.log("  keeping the certificate with id="
						+ keeper.getId());

				for (int i = 1; i < certs.size(); i++) {
					PicsLogger.log("  For cert id=" + certs.get(i).getId());
					for (ContractorAuditOperator cao : certs.get(i).getCaos()) {
						PicsLogger.log("   change cao cert for cao id="
								+ cao.getId() + " - opName= "
								+ cao.getOperator().getName());
						cao.setCertificate(keeper);
						caoDAO.save(cao);
					}

					File[] files = getFiles(certs.get(i).getId());
					PicsLogger.log("  found " + files.length
							+ " files for certificate id="
							+ certs.get(i).getId());
					for (File file : files)
						FileUtils.moveFile(file, getFtpDir() + "/cert_cleanup/");

					certificateDAO.remove(certs.get(i));
					count++;
				}

				PicsLogger.log("    keeper's old expiration date="
						+ keeper.getExpirationDate());
				keeper.updateExpirationDate();
				PicsLogger.log("    keeper's new expiration date="
						+ keeper.getExpirationDate());
				certificateDAO.save(keeper);
			}
		}

		addActionMessage("Deleted " + count + " certificates");

		PicsLogger.log("Deleted " + count + " certificates");
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

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
