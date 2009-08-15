package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.Certificate;

public class ContractorCertificates extends ContractorActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	private CertificateDAO certificateDAO;

	private int caoID;

	private List<Certificate> certificates = null;

	public ContractorCertificates(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findContractor();

		return SUCCESS;
	}

	public int getCaoID() {
		return caoID;
	}

	public void setCaoID(int caoID) {
		this.caoID = caoID;
	}

	public List<Certificate> getCertificates() {
		if (certificates == null) {
			certificates = certificateDAO.findByConId(contractor.getId(), permissions, false);
		}

		return certificates;
	}
}
