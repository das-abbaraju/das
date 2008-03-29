package com.picsauditing.actions.audits;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.PqfDataDAO;
import com.picsauditing.jpa.entities.OshaLog;

public class VerifyView extends ContractorActionSupport {
	private int oshaID = 0;
	private OshaLog osha;
	private PqfDataDAO pqfDao;

	public VerifyView(AccountDAO accountDao, PqfDataDAO pqfDao) {
		this.accountDao = accountDao;
		this.pqfDao = pqfDao;
	}

	public String execute() throws Exception {
		contractor = accountDao.find(id);
		if (contractor.getId() == 0)
			return INPUT;

		// Retreive the osha record we selected
		// or pick the only child if only one exists
		if (oshaID > 0) {
			for (OshaLog row : contractor.getOshas())
				if (row.getId() == oshaID)
					osha = row;
		} else if (contractor.getOshas().size() == 1) {
			osha = contractor.getOshas().get(0);
			oshaID = osha.getId();
		}
		
		// Now get the EMR data
		pqfDao.

		return SUCCESS;
	}

	public int getOshaID() {
		return oshaID;
	}

	public void setOshaID(int oshaID) {
		this.oshaID = oshaID;
	}
}
