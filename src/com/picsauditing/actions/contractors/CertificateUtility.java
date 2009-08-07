package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
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

		List<String> dupeHashes = certificateDAO.findDupeHashes(num);
		for (String hash : dupeHashes) {
			List<Certificate> certs = certificateDAO.findWhere("fileHash = '" + hash + "'");

			Certificate keeper = certs.get(0);
			for (int i = 1; i < certs.size(); i++) {
				for (ContractorAuditOperator cao : certs.get(i).getCaos()) {
					cao.setCertificate(keeper);
				}
				File[] files = getFiles(certs.get(i).getId());
				for (File file : files)
					FileUtils.deleteFile(file);
				deleted.add(certs.get(i).getId());
				certificateDAO.remove(certs.get(i));
				count++;
			}
		}

		addActionMessage("The folling hashes have been fixed");
		for (String s : dupeHashes)
			addActionMessage(s);

		addActionMessage(count + " certificates have been removed:");
		for (Integer i : deleted)
			addActionMessage(i.toString());

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
