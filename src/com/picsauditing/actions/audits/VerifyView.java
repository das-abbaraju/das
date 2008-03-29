package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.PqfDataDAO;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.PqfData;
import com.picsauditing.jpa.entities.PqfQuestion;

public class VerifyView extends ContractorActionSupport {
	private int oshaID = 0;
	private OshaLog osha;
	private PqfDataDAO pqfDao;
	private HashMap<Integer, PqfData> emr;

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
		ArrayList<Integer> emrQuestions = new ArrayList<Integer>();
		emrQuestions.add(PqfQuestion.EMR07);
		emrQuestions.add(PqfQuestion.EMR06);
		emrQuestions.add(PqfQuestion.EMR05);
		emrQuestions.add(PqfQuestion.EMR04);
		pqfDao.findAnswers(this.id, emrQuestions);

		return SUCCESS;
	}

	public int getOshaID() {
		return oshaID;
	}

	public void setOshaID(int oshaID) {
		this.oshaID = oshaID;
	}

	public HashMap<Integer, PqfData> getEmr() {
		return emr;
	}

	public void setEmr(HashMap<Integer, PqfData> emr) {
		this.emr = emr;
	}
}
