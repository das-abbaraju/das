package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashMap;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.YesNo;

public class VerifyView extends ContractorActionSupport {
	private int oshaID = 0;
	private OshaLog osha;
	private AuditDataDAO pqfDao;
	private HashMap<Integer, AuditData> emr;

	public VerifyView(AccountDAO accountDao, AuditDataDAO pqfDao) {
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
		emrQuestions.add(AuditQuestion.EMR07);
		emrQuestions.add(AuditQuestion.EMR06);
		emrQuestions.add(AuditQuestion.EMR05);
		emrQuestions.add(AuditQuestion.EMR04);
		emr = pqfDao.findAnswers(this.id, emrQuestions);

		return SUCCESS;
	}

	public OshaLog getOsha() {
		return osha;
	}
	
	public int getOshaID() {
		return oshaID;
	}

	public void setOshaID(int oshaID) {
		this.oshaID = oshaID;
	}

	public HashMap<Integer, AuditData> getEmr() {
		return emr;
	}

	public void setEmr(HashMap<Integer, AuditData> emr) {
		this.emr = emr;
	}
	
	public int getYear1() {
		return 2007;
	}
	
	public int getYear2() {
		return this.getYear1() - 1;
	}
	
	public int getYear3() {
		return this.getYear1() - 2;
	}
	
	public AuditData getEmr1() {
		return emr.get(AuditQuestion.EMR07);
	}
	
	public AuditData getEmr2() {
		return emr.get(AuditQuestion.EMR06);
	}
	
	public AuditData getEmr3() {
		return emr.get(AuditQuestion.EMR05);
	}
	
	public YesNo[] getYesNos() {
		return YesNo.values();
	}
	
	public ArrayList<String> getOshaProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Contradicting Data");
		list.add("Missing 300");
		list.add("Missing 300a");
		list.add("Incomplete");
		list.add("Incorrect Form");
		list.add("Incorrect Year");
		return list;
	}

	public ArrayList<String> getEmrProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Need EMR");
		list.add("Need Loss Run");
		list.add("Not Insurance Issued");
		list.add("Incorrect Upload");
		list.add("Incorrect Year");
		return list;
	}
	
	public String getContractorNotes() {
		String notes = this.contractor.getContractor().getNotes();
		notes = notes.replace("\n", "<br>");
		
		int position = 0;
		for(int i=0; i < 5; i++) {
			position = notes.indexOf("<br>", position+1);
			if (position == -1) return notes;
		}
		
		notes = notes.substring(0, position);
		return notes;
	}
}
