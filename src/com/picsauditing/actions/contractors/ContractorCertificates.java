package com.picsauditing.actions.contractors;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.dao.OperatorAccountDAO;

public class ContractorCertificates extends ContractorActionSupport {
	private static final long serialVersionUID = 2438788697676816034L;

	private CertificateDAO certificateDAO;
	private OperatorAccountDAO operatorDAO;

	private int caoID;
	private int catDataID;

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

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	@SuppressWarnings("deprecation")
	public List<Certificate> getCertificates() {
		if (certificates == null)
			certificates = certificateDAO.findByConId(contractor.getId(), permissions, true);

		if (permissions.isOperatorCorporate()) {
			int topID = permissions.getTopAccountID();
			OperatorAccount opAcc = operatorDAO.find(topID);

			List<Integer> allowedList = new ArrayList<Integer>();
			allowedList = opAcc.getOperatorHeirarchy();

			for (OperatorAccount tmpOp : opAcc.getOperatorChildren()) {
				allowedList.add(tmpOp.getId());
			}

			Iterator<Certificate> itr = certificates.iterator();

			while (itr.hasNext()) {
				Certificate c = itr.next();
				// int certUser = c.getUpdatedBy2().getAccount().getId();
				int certID = c.getId();

				List<Integer> ops = certificateDAO.findOpsByCert(certID);

				boolean remove = true;

				for (Integer i : ops) {
					if (allowedList.contains(i)) {
						remove = false;
						break;
					}
				}
				if (remove) {
					itr.remove();
				}
			}
		}
		return certificates;
	}

	public boolean isAllExpired() {
		boolean expired = true;
		for (Certificate c : getCertificates()) {
			if (!c.isExpired())
				expired = false;
		}

		return expired;
	}

	public void setOperatorDAO(OperatorAccountDAO operatorDAO) {
		this.operatorDAO = operatorDAO;
	}
}
