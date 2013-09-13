package com.picsauditing.actions.contractors;

import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.Certificate;

public class ContractorCertificates extends ContractorActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	private int caoID;
	private int catDataID;

	public ContractorCertificates(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDAO) {
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

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public boolean isAllExpired() {
		boolean expired = true;
		for (Certificate c : getCertificates()) {
			if (!c.isExpired())
				expired = false;
		}

		return expired;
	}
}
